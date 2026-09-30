package com.example.myapplication.ui.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.ui.CreateProfileScreen
import com.example.myapplication.ui.DeleteConfirmationDialog
import com.example.myapplication.ui.EditProfileScreen
import com.example.myapplication.ui.EggMeScreen
import com.example.myapplication.ui.FiltersScreen
import com.example.myapplication.ui.FindYourFarmersScreen
import com.example.myapplication.ui.FindYourFarmersViewModel
import com.example.myapplication.ui.HomeScreen
import com.example.myapplication.ui.HomeViewModel
import com.example.myapplication.ui.NotificationScreen
import com.example.myapplication.ui.NotificationViewModel
import com.example.myapplication.ui.OwnedRecipeListViewModel
import com.example.myapplication.ui.OwnedRecipeProposalList
import com.example.myapplication.ui.RecipeCreationScreen
import com.example.myapplication.ui.RecipeListViewModel
import com.example.myapplication.ui.RecipeProposalList
import com.example.myapplication.ui.RecipeProposalScreen
import com.example.myapplication.ui.RecipeViewModel
import com.example.myapplication.ui.ReviewCreationScreen
import com.example.myapplication.ui.ReviewPresentationScreen
import com.example.myapplication.ui.UserProfileScreen
import com.example.myapplication.ui.UserViewModel
import com.example.myapplication.ui.commoncomponents.GenericSelectionDialog
import com.example.myapplication.ui.commoncomponents.ImageUploadDialog
import com.example.myapplication.ui.commoncomponents.TimeMinutesInputDialog

fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    orlvm: OwnedRecipeListViewModel,
    rlvm: RecipeListViewModel,
    rvm: RecipeViewModel,
    uvm: UserViewModel,
    hvm: HomeViewModel,
    nvm: NotificationViewModel,
    fvm: FindYourFarmersViewModel
) {
    navigation<MainGraph>(startDestination = HomeRoute) {

        composable<HomeRoute> {
            HomeScreen(
                viewModel = hvm,
                notificationViewModel = nvm,
                onRecipeClick = { id ->
                    navController.navigate(RecipeDetailRoute(recipeId = id))
                },
                onAuthRequired = { navController.navigate(AuthGraph) },
                onNotificationClick = {
                    if(SessionManagerFacade.isLoggedIn){
                        navController.navigate(NotificationsRoute)}
                    else{
                        navController.navigate(AuthGraph)}}
            )
        }

        composable<NotificationsRoute> {
            //val route = backStackEntry.toRoute<NotificationsRoute>()
            NotificationScreen(
                viewModel = nvm,
                onNotificationClick = { id ->
                    navController.navigate(RecipeDetailRoute(recipeId = id))
                },
                onBack = { navController.popBackStack() },
                //onAuthRequired = { navController.navigate(AuthGraph) },
            )
        }


        composable<RecipesListRoute> {
            RecipeProposalList(
                viewModel = rlvm,
                onRecipeClick = { id ->
                    navController.navigate(RecipeDetailRoute(recipeId = id))
                },
                onFilterClick = {
                    navController.navigate(FiltersRoute)
                },
                onAuthRequired = { navController.navigate(AuthGraph) }
            )
        }

        composable<RecipeDetailRoute>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://eggme.com/recipe/{recipeId}"
                }
            )
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeDetailRoute>()
            RecipeProposalScreen(
                idRecipe = route.recipeId,
                onBack = { navController.popBackStack() },
                onViewAllReviewsClick = { navController.navigate(ReviewListRoute(route.recipeId)) },
                onWriteReviewClick = {
                    if(SessionManagerFacade.isLoggedIn){
                        navController.navigate(CreateReviewRoute(route.recipeId))
                    }
                    else{
                        navController.navigate(AuthGraph)
                    }},
                viewModel = rvm,
                onDeleteClick = { navController.navigate(DeleteConfirmationRoute) },
                onCoverPictureClick = { navController.navigate(RecipeCoverImageRoute) },
                onStepPictureClick = { index -> navController.navigate(RecipeStepImageRoute(index)) },
                onTimePickerClick = { minutes: Int -> navController.navigate(RecipeCreationTimeRoute(initialMinutes = minutes)) },
                onAuthRequired = { navController.navigate(AuthGraph) }
            )
        }
        /*
        composable<RecipeDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeDetailRoute>()
            RecipeProposalScreen(
                idRecipe = route.recipeId,
                onBack = { navController.popBackStack() },
                onViewAllReviewsClick = { navController.navigate(ReviewListRoute(route.recipeId)) },
                onWriteReviewClick = {
                    if(SessionManagerFacade.isLoggedIn){
                        navController.navigate(CreateReviewRoute(route.recipeId))
                    }
                    else{
                        navController.navigate(AuthGraph)
                    }},
                viewModel = rvm,
                onDeleteClick = { navController.navigate(DeleteConfirmationRoute) },
                onCoverPictureClick = { navController.navigate(RecipeCoverImageRoute) },
                onStepPictureClick = { index -> navController.navigate(RecipeStepImageRoute(index)) },
                onTimePickerClick = { minutes: Int -> navController.navigate(RecipeCreationTimeRoute(initialMinutes = minutes)) },
                onAuthRequired = { navController.navigate(AuthGraph) }
            )
        }
        */


        composable<CreateRecipeRoute> {
            RecipeCreationScreen(
                viewModel = rvm,
                onBack = { navController.popBackStack() },
                onCoverPictureClick = { navController.navigate(RecipeCoverImageRoute) },
                onStepPictureClick = { index -> navController.navigate(RecipeStepImageRoute(index)) } ,
                onTimePickerClick = { minutes: Int -> navController.navigate(RecipeCreationTimeRoute(initialMinutes = minutes)) }
            )
        }

        composable<FiltersRoute> {
            FiltersScreen(
                viewModel = rlvm,
                onBack = { navController.popBackStack() },
                onTimePickerClick = { minutes: Int, _ -> navController.navigate(RecipeFilterTimeRoute(initialMinutes = minutes)) },
                onAddIngredientClick = { suggestions, selected ->
                    navController.navigate(FilterIngredientsRoute(suggestions, selected))
                },
                onAddCuisineClick = { suggestions, selected ->
                    navController.navigate(FilterCuisinesRoute(suggestions, selected))
                }
            )
        }


        composable<MyRecipesRoute> {
            OwnedRecipeProposalList(
                viewModel = orlvm,
                onRecipeClick = { id ->
                    navController.navigate(RecipeDetailRoute(recipeId = id))
                },
                onSettingsClick = {
                    navController.navigate(ProfileRoute)
                },
                onBack = { navController.popBackStack() }
            )
        }


        composable<EggRoute> {
            EggMeScreen(onFindYourFarmersClick = {navController.navigate(FindYourFarmersRoute)})
        }

        composable<FindYourFarmersRoute> {
            FindYourFarmersScreen(onBack = { navController.popBackStack() },viewModel = fvm)
        }


        composable<ProfileRoute> {
            UserProfileScreen(
                uvm = uvm,
                onBack = { navController.popBackStack() },
                onEditClick = { navController.navigate(EditProfileRoute) },
                onLogout = {navController.navigate(HomeRoute){
                    popUpTo(MainGraph) { inclusive = true }
                } }
            )
        }

        composable<EditProfileRoute> {
            EditProfileScreen(
                uvm = uvm,
                onBack = { navController.popBackStack() },
                onProfilePictureClick = {
                    navController.navigate(ProfileImageRoute)},
                onAddIngredientClick = { suggestions, selected ->
                    navController.navigate(ProfileIngredientsRoute(suggestions, selected))
                },
                onAddCuisineClick = { suggestions, selected ->
                    navController.navigate(ProfileCuisinesRoute(suggestions, selected))
                },
                onAddRestrictionClick = { suggestions, selected ->
                    navController.navigate(ProfileRestrictionsRoute(suggestions, selected))
                }
            )
        }

        dialog<ProfileImageRoute> {
            ImageUploadDialog(
                title = "Upload a profile picture",
                onConfirm = { url ->
                    uvm.setProfilePicture(url)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<RecipeCoverImageRoute> {
            ImageUploadDialog(
                title = "Cover Image",
                onConfirm = { url ->
                    rvm.updatePicture(url)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<RecipeStepImageRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<RecipeStepImageRoute>()
            ImageUploadDialog(
                title = "Step Image",
                onConfirm = { url ->
                    rvm.updateStepImage(route.stepIndex, url)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<RecipeReviewImageRoute> {
            ImageUploadDialog(
                title = "Review Image",
                onConfirm = { url ->
                    rvm.updateReviewPhoto(url)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<DeleteConfirmationRoute> {
            DeleteConfirmationDialog(
                onDismiss = { navController.popBackStack() },
                onConfirm = {
                    rvm.deleteRecipe()
                    navController.popBackStack()
                    navController.popBackStack()
                }
            )
        }

        dialog<RecipeCreationTimeRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<RecipeCreationTimeRoute>()
            TimeMinutesInputDialog(
                initialMinutes = args.initialMinutes,
                title = "Set time",
                onConfirm = { minutes ->
                    rvm.updateTime(minutes)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<RecipeFilterTimeRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<RecipeFilterTimeRoute>()
            TimeMinutesInputDialog(
                initialMinutes = args.initialMinutes,
                title = "Set max time",
                onConfirm = { minutes ->
                    rlvm.setMaxTimeFilter(minutes)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<ProfileIngredientsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ProfileIngredientsRoute>()
            GenericSelectionDialog(
                dialogTitle = "Add to Favorite Ingredients",
                suggestions = args.suggestions,
                alreadySelectedItems = args.selected,
                onItemSelected = { item ->
                    uvm.addIngredient(item)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<ProfileCuisinesRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ProfileCuisinesRoute>()
            GenericSelectionDialog(
                dialogTitle = "Add to Cuisine Type",
                suggestions = args.suggestions,
                alreadySelectedItems = args.selected,
                onItemSelected = { item ->
                    uvm.addCuisine(item)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<ProfileRestrictionsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<ProfileRestrictionsRoute>()
            GenericSelectionDialog(
                dialogTitle = "Add to Restrictions",
                suggestions = args.suggestions,
                alreadySelectedItems = args.selected,
                onItemSelected = { item ->
                    uvm.addRestriction(item)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }


        dialog<FilterIngredientsRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<FilterIngredientsRoute>()
            GenericSelectionDialog(
                dialogTitle = "Filter by Ingredient",
                suggestions = args.suggestions,
                alreadySelectedItems = args.selected,
                onItemSelected = { item ->
                    rlvm.addIngredientFilter(item)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        dialog<FilterCuisinesRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<FilterCuisinesRoute>()
            GenericSelectionDialog(
                dialogTitle = "Filter by Cuisine Type",
                suggestions = args.suggestions,
                alreadySelectedItems = args.selected,
                onItemSelected = { item ->
                    rlvm.setCuisineFilter(item)
                    navController.popBackStack()
                },
                onDismiss = { navController.popBackStack() }
            )
        }

        composable<ReviewListRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ReviewListRoute>()

            ReviewPresentationScreen(
                idRecipe = route.recipeId,
                onBack = { navController.popBackStack() },
                viewModel = rvm
            )
        }

        composable<CreateReviewRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CreateReviewRoute>()

            ReviewCreationScreen(
                idRecipe = route.recipeId,
                onBack = { navController.popBackStack() },
                viewModel = rvm,
                onReviewPictureClick = { navController.navigate(RecipeReviewImageRoute) },

                )
        }

        composable<CreateProfileRoute> {
            CreateProfileScreen(
                uvm = uvm,
                onBack = { navController.popBackStack() },
                onProfilePictureClick = {
                    navController.navigate(ProfileImageRoute)
                },
                onAddIngredientClick = { suggestions, selected ->
                    navController.navigate(ProfileIngredientsRoute(suggestions, selected))
                },
                onAddCuisineClick = { suggestions, selected ->
                    navController.navigate(ProfileCuisinesRoute(suggestions, selected))
                },
                onAddRestrictionClick = { suggestions, selected ->
                    navController.navigate(ProfileRestrictionsRoute(suggestions, selected))
                }
            )
        }
    }
}