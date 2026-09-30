
// TO DEPLOY THE FUNCTIONS TO FIREBASE
//open terminal in firebase folder
//cd functions
//npm install
//cd ..
//firebase deploy --only functions

//Triggers: https://firebase.google.com/docs/functions/firestore-events?hl=it
//Firebase query: https://firebase.google.com/docs/firestore/query-data/queries?hl=it
//Schedule a function: https://firebase.google.com/docs/functions/schedule-functions?hl=it

import { onDocumentCreated, onDocumentUpdated, onDocumentDeleted } from "firebase-functions/v2/firestore";
import {onSchedule} from "firebase-functions/scheduler";
import * as admin from "firebase-admin";

admin.initializeApp();


//notification sent to the recipe's author when a new review is added to one of his recipes
//and update the average rating of the recipe
export const onReviewCreated = onDocumentUpdated("recipes/{recipeId}", async (event) => {
  const before = event.data?.before?.data();
  const after = event.data?.after?.data();

  if (!before || !after) {
    return null;
  }

  const beforeReviews = before.reviews ?? [];
  const afterReviews = after.reviews ?? [];

  if (afterReviews.length <= beforeReviews.length) {
    //no new review, no actions
    //by doing this check, we cannot update the rating when a review is deleted or updated, but users cannot do that, so it's not a problem (unless future features are added)
    return null;
  }

  const newReview = afterReviews[afterReviews.length - 1];
  const recipeAuthorId = after.author?.id ?? "";  //the receiver of the notification
  const reviewer = newReview.author ?? {};
  const reviewerId = reviewer.id ?? "";

  if (!recipeAuthorId || !reviewerId /*|| recipeAuthorId === reviewerId*/) {
    return null;
  }

  const notificationRef = admin.firestore().collection("notifications").doc();

  const notification = {
    id: notificationRef.id,
    userId: recipeAuthorId,
    type: "REVIEW",
    title: "New review",
    message: "reviewed your recipe",
    recipeId: after.id,
    author: {
      id: reviewer.id ?? "",
      name: reviewer.name ?? "",
      surname: reviewer.surname ?? "",
    	profilePicture: reviewer.profilePicture ?? null
    },
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    isRead: false
  };

  await notificationRef.set(notification);


  //ALSO UPDATE THE AVERAGE RATING OF THE RECIPE
  const totalRating = afterReviews.reduce((sum: number, review: { rating: number; }) => sum + review.rating, 0);
  const averageRating = totalRating / afterReviews.length;
  const updatedRating = parseFloat(averageRating.toFixed(1));
  await admin.firestore().collection("recipes").doc(after.id).update({
    rating: updatedRating
  });

  //AND UPDATE THE RATING IN USERS' LISTS
  const usersSnapshot = await admin.firestore().collection("users").get();

  for (const userDoc of usersSnapshot.docs) {
    const userLists = userDoc.data().userLists ?? {};
    const updatedUserLists: Record<string, any[]> = {};
    let hasChanges = false;

    for (const [listName, recipes] of Object.entries(userLists) as [string, Array<{ id: string; rating: number; [key: string]: any }>][]) {
      const updatedRecipes = [];

      for (const recipe of recipes) {
        if (recipe.id === after.id) {
          updatedRecipes.push({
            ...recipe,
            rating: updatedRating
          });
          hasChanges = true;
        } else {
          updatedRecipes.push(recipe);
        }
      }

      updatedUserLists[listName] = updatedRecipes;
    }

    if (hasChanges) {
      await admin.firestore().collection("users").doc(userDoc.id).update({
        userLists: updatedUserLists
      });
    }
  }

  //ALSO SEND PUSH NOTIFICATION TO THE RECIPE'S AUTHOR
  const userDoc = await admin.firestore().collection("users").doc(recipeAuthorId).get();
    
  if (!userDoc.exists) {
    //something went wrong, no actions
    return null;
  }

  const userData = userDoc.data();
  const tokens = userData?.fcmTokens ?? []; 

  if (tokens.length === 0) {
    //no token, no push notification
    return null;
  }

  const sendMessages = tokens.map(async (token: string) => {
    const message = {
      token: token,
      data: {
        title: notification.title,
        body: (reviewer.name && reviewer.surname) ? `${reviewer.name} ${reviewer.surname} reviewed your recipe` : "Someone reviewed your recipe. Tap to see it!",
        recipeId: String(notification.recipeId)
      },
      android: {
        priority: "high" as const
      }
    };

    await admin.messaging().send(message);
  });

  await Promise.all(sendMessages);

  console.log(`Trigger onReviewCreated executed. Notification sent to ${recipeAuthorId}.`);


  return null;
});


//notification sent to the original recipe's author when a recipe is forked from one of his recipes
export const onRecipeForked = onDocumentCreated("recipes/{recipeId}", async (event) => {
  
	const recipeCreated = event.data?.data();
  if (!recipeCreated?.forkedFromUserId) {
    //recipe not forked, no actions
    return null;
  }
  const originalRecipeAuthorId = recipeCreated?.forkedFromUserId ?? "";  //the receiver of the notification
  const forker = recipeCreated?.author ?? {};
  const forkerId = forker.id ?? "";

  if (!originalRecipeAuthorId || !forkerId /*|| originalRecipeAuthorId === forkerId*/) {
    return null;
  }

  const notificationRef = admin.firestore().collection("notifications").doc();

  const notification = {
    id: notificationRef.id,
    userId: originalRecipeAuthorId,
    type: "IMPORTED",
    title: "New import",
    message: "imported your recipe",
    recipeId: recipeCreated?.id,  //bring to the new recipe
    author: {
      id: forker.id ?? "",
      name: forker.name ?? "",
      surname: forker.surname ?? "",
    	profilePicture: forker.profilePicture ?? null
    },
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    isRead: false
  };

  await notificationRef.set(notification);


  //ALSO SEND PUSH NOTIFICATION TO THE ORIGINAL RECIPE'S AUTHOR
  const userDoc = await admin.firestore().collection("users").doc(originalRecipeAuthorId).get();
    
  if (!userDoc.exists) {
    //something went wrong, no actions
    return null;
  }

  const userData = userDoc.data();
  const tokens = userData?.fcmTokens ?? []; 

  if (tokens.length === 0) {
    //no token, no push notification
    return null;
  }

  const sendMessages = tokens.map(async (token: string) => {
    const message = {
      token: token,
      data: {
        title: notification.title,
        body: (forker.name && forker.surname) ? `${forker.name} ${forker.surname} imported your recipe` : "Someone imported your recipe. Tap to see it!",
        recipeId: String(notification.recipeId)
      },
      android: {
        priority: "high" as const
      }
    };

    await admin.messaging().send(message);
  });

  await Promise.all(sendMessages);

  console.log(`Trigger onRecipeForked executed. Notification sent to ${originalRecipeAuthorId}.`);

  return null;
});


//notification sent to users that might be interested in a recipe when a new recipe is created
export const onRecipeCreated = onDocumentCreated("recipes/{recipeId}", async (event) => {
  
	const recipeCreated = event.data?.data();
  if (!recipeCreated) {
    //something went wrong, no actions
    return null;
  }

	const author = recipeCreated?.author ?? {};
  const authorId = author.id ?? "";

	if (!authorId) {
		//something went wrong, no actions
    return null;	
  }

	const recipeIngredients = recipeCreated.ingredients.map((i: { name: string; }) => i.name);
  const recipeRestrictions = recipeCreated.dietaryRestrictions ?? [];

	//research of users to notify based on their preferences and restrictions
	const usersToNotifySnapshot = await admin.firestore().collection("users")
		.where("favoriteIngredients","array-contains-any", recipeIngredients)
		.get();


	for (let userDoc of usersToNotifySnapshot.docs) {
		//quick check if the user's restirictions are compatible with the recipe's ones
		const userRestrictions = userDoc.data().restriction ?? [];
		if (userRestrictions.length > 0) {
  		if ( ! userRestrictions.every((restriction: string) => recipeRestrictions.includes(restriction))) 
				continue;
		} //no restrictions, no need to check

		if (userDoc.id === authorId) {
			//don't notify the author
			continue;
		}

		const notificationRef = admin.firestore().collection("notifications").doc();

  	const notification = {
			id: notificationRef.id,
			userId: userDoc.id, //receiver of the notification
			type: "RECOMMENDED",
			title: "Recommended for you",
			message: "published a recipe you might like",
			recipeId: recipeCreated.id,  //bring to the new recipe
			author: {
				id: author.id ?? "",
				name: author.name ?? "",
				surname: author.surname ?? "",
				profilePicture: author.profilePicture ?? null
			},
   		createdAt: admin.firestore.FieldValue.serverTimestamp(),
   		isRead: false
		};

  	await notificationRef.set(notification);

    //ALSO SEND PUSH NOTIFICATION TO THE USER
    const tokens = userDoc.data()?.fcmTokens ?? []; 

    if (tokens.length === 0) {
      //no token, no push notification
      continue;
    }

    const sendMessages = tokens.map(async (token: string) => {
      const message = {
        token: token,
        data: {
          title: notification.title,
          body: (author.name && author.surname) ? `${author.name} ${author.surname} published a recipe you might like` : "Someone published a recipe you might like. Tap to see it!",
          recipeId: String(notification.recipeId)
        },
        android: {
          priority: "high" as const
        }
      };

      await admin.messaging().send(message);
    });

    await Promise.all(sendMessages);

    console.log(`Trigger onRecipeCreated executed. Notification sent to ${userDoc.id}.`);
	}


  return null;
});


//notification sent to users at a specific time of the day with a recipe suggestion
export const onScheduleNotifications = onSchedule({
    //change this hour and redeploy to test the function
    schedule: "every day 18:00",  //setted at 18:00 (when people usually stop working)    
    region: "europe-west1",
    timeZone: "Europe/Rome",
  }, async (event) => {
  
  const allUsers = await admin.firestore().collection("users").get();

  for (let userDoc of allUsers.docs) {
    const userRestriction = userDoc.data().restriction ?? [];
    const userFavoriteIngredients = userDoc.data().favoriteIngredients ?? [];

    let recipes;

    if (userFavoriteIngredients.length === 0) {
      recipes = await admin.firestore().collection("recipes")
        .get()
    } else {
      recipes = await admin.firestore().collection("recipes")
        .where("ingredients", "array-contains-any", userFavoriteIngredients)
        .get()
    }

    //random order using Fisher-Yates alg.
    for (let i = recipes.docs.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [recipes.docs[i], recipes.docs[j]] = [recipes.docs[j], recipes.docs[i]];
    }

    for (let recipeDoc of recipes.docs) {
      const recipeRestrictions = recipeDoc.data().dietaryRestrictions ?? [];
      if ( userRestriction.length > 0 && !userRestriction.every((restriction: string) => recipeRestrictions.includes(restriction))) {
        //does not match the user's restrictions
        continue;
      } else {
        //suggest the first recipe (the recipe is random)
        const notificationRef = admin.firestore().collection("notifications").doc();

        const notification = {
          id: notificationRef.id,
          userId: userDoc.id,
          type: "RECOMMENDED",
          title: "Recommended for you",
          message: "We think you might like this recipe",
          recipeId: recipeDoc.id,
          //Inizialmente avevavmo pensato di mostrare le notifiche solo dopo 
          //un'azione di un utente, per questo abbiamo inserito il campo author.
          //Per questa notifica che si basa sull'ora del giorno, non abbiamo un autore da mostrare
          //quindi utilizzo la foto della ricetta come "foto profilo"
          author: {
            id: "",
            name: "",
            surname: "",
            profilePicture: recipeDoc.data().recipePicture ?? null
          },
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          isRead: false
        };

        await notificationRef.set(notification);

        //ALSO SEND PUSH NOTIFICATION TO THE USER
        const tokens = userDoc.data()?.fcmTokens ?? []; 

        if (tokens.length === 0) {
          //no token, no push notification
          continue;
        }

        const sendMessages = tokens.map(async (token: string) => {
          const message = {
            token: token,
            data: {
              title: notification.title,
              body: notification.message,
              recipeId: String(notification.recipeId)
            },
            android: {
              priority: "high" as const
            }
          };

          await admin.messaging().send(message);
        });

        await Promise.all(sendMessages);

        console.log(`Trigger onScheduleNotifications executed. Notification sent to ${userDoc.id}.`);
      
        break; //notifications sended, we an skip to the next user without spamming users
      }	
    }
    //if we are here we found a recipe to recommend or there are no recipes that match the user's preferences
  }
});


//update the recipe's info in users' lists when the recipe is updated
export const onRecipeUpdated = onDocumentUpdated("recipes/{recipeId}", async (event) => {
  const before = event.data?.before?.data();
  const after = event.data?.after?.data();

  if (before?.title === after?.title && before?.recipePicture === after?.recipePicture && before?.time === after?.time) {
    //no relevant changes, no actions
    return null;
  }

  const usersSnapshot = await admin.firestore().collection("users").get();

  for (const userDoc of usersSnapshot.docs) {
    const userLists = userDoc.data().userLists ?? {};
    const updatedUserLists: Record<string, any[]> = {};
    let hasChanges = false;

    for (const [listName, recipes] of Object.entries(userLists) as [string, Array<{ id: string; rating: number; [key: string]: any }>][]) {
      const updatedRecipes = [];

      for (const recipe of recipes) {
        if (recipe.id === after?.id) {
          updatedRecipes.push({
            ...recipe,
            title: after?.title ?? recipe.title,
            recipePicture: after?.recipePicture ?? recipe.recipePicture,
            time: after?.time ?? recipe.time
          });
          hasChanges = true;
        } else {
          updatedRecipes.push(recipe);
        }
      }

      updatedUserLists[listName] = updatedRecipes;
    }

    if (hasChanges) {
      await admin.firestore().collection("users").doc(userDoc.id).update({
        userLists: updatedUserLists
      });
    }
  }

  return null;
});


//update the recipe's info in users' lists when the recipe is deleted
//and delete notifications related to that recipe
export const onRecipeDeleted = onDocumentDeleted("recipes/{recipeId}", async (event) => {
  const usersSnapshot = await admin.firestore().collection("users").get();

  for (const userDoc of usersSnapshot.docs) {
    const userLists = userDoc.data().userLists ?? {};
    const updatedUserLists: Record<string, any[]> = {};
    let hasChanges = false;

    for (const [listName, recipes] of Object.entries(userLists) as [string, Array<{ id: string; rating: number; [key: string]: any }>][]) {
      const updatedRecipes = [];

      for (const recipe of recipes) {
        if (recipe.id === event.data?.id) {
          hasChanges = true;
        } else {
          updatedRecipes.push(recipe);
        }
      }

      updatedUserLists[listName] = updatedRecipes;
    }

    if (hasChanges) {
      await admin.firestore().collection("users").doc(userDoc.id).update({
        userLists: updatedUserLists
      });
    }
  }

  //also delete notifications that contain the deleted recipe
  const notificationsSnapshot = await admin.firestore()
    .collection("notifications")
    .where("recipeId", "==", event.params.recipeId)
    .get();

  for (const notificationDoc of notificationsSnapshot.docs) {
    await notificationDoc.ref.delete();
  }

  return null;
});


//update the author's info in recipes, notifications and reviews when user info change
export const onUserUpdated = onDocumentUpdated("users/{userId}", async (event) => {
  const before = event.data?.before?.data();
  const after = event.data?.after?.data();

  if (!before || !after) {
    return null;
  }   

  if (before.name === after.name && before.surname === after.surname && before.profilePicture === after.profilePicture) {
    return null;
  }

  const newAuthor = {
    id: event.params.userId,
    name: after.name ,
    surname: after.surname ?? "",
    profilePicture: after.profilePicture ?? null
  };

  //update the recipe's author and the reviews' authors
  const recipesSnapshot = await admin.firestore().collection("recipes").get();
  for (const doc of recipesSnapshot.docs) {
    const recipe = doc.data() as any;
    const dataToUpdate: any = {};
    let hasChanges = false;

    //author update
    if (recipe?.author?.id === event.params.userId) {
      dataToUpdate.author = newAuthor;
      hasChanges = true;
    }

    //reviews update
    const reviews = recipe.reviews ?? [];
    const updatedReviews: any[] = [];
    let reviewsChanged = false;
    for (const review of reviews) {
      if (review?.author?.id === event.params.userId) {
        updatedReviews.push({ ...review, author: newAuthor });
        reviewsChanged = true;
      } else {
        updatedReviews.push(review);
      }
    }
    if (reviewsChanged) {
      dataToUpdate.reviews = updatedReviews;
      hasChanges = true;
    }

    if (hasChanges) {
      await doc.ref.update(dataToUpdate);
    }
  }


  //update the notification's author
  const notificationsSnapshot = await admin.firestore().collection("notifications").get();
  for (const doc of notificationsSnapshot.docs) {
    const notification = doc.data() as any;
    if (notification?.author?.id === event.params.userId) {
      await doc.ref.update({ author: newAuthor });
    }
  }
  
  return null;
});
