package com.example.myapplication.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
//import coil3.compose.AsyncImage
import coil.compose.AsyncImage
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.Recipe
import com.example.myapplication.ui.EditRecipeProposal
import com.example.myapplication.ui.RecipeViewModel
import com.example.myapplication.ui.ReviewComponent
import com.example.myapplication.ui.commoncomponents.ProfilePicture
import com.example.myapplication.ui.theme.MyRed
import com.example.myapplication.R


@Composable
fun ShowRecipeProposalDetails(
    recipe: Recipe,
    importRecipe: () -> Unit,
    increment:()->Unit,
    decrement:()->Unit ,
    counter:Int ,
    onSaveClick: () -> Unit,
    onCookClick: () -> Unit,
    isSaved: Boolean,
    isCooked: Boolean,
    onViewAllReviewsClick: (String) -> Unit,
    onWriteReviewClick: (String) -> Unit
) {

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        //image
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = recipe.recipePicture,
                    contentDescription = "recipe picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )


            }
        }

        //recipe title and rating
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.title.uppercase(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = recipe.rating.toString() + " ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    painter = painterResource(id = R.drawable.egg_fried_heart),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        //recipe author and cuisine
        item {
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {

                ProfilePicture(recipe.author, 40.dp)

                Text(
                    text = " " + recipe.author.name + " " + recipe.author.surname,
                    modifier = Modifier
                        .weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    //fontSize = 18.sp,
                    //fontWeight = FontWeight.Bold
                )

                Text(
                    text = recipe.cuisineType.uppercase() + " CUISINE",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        //save,cooked and import button
        item {
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                if (isSaved) {
                    FilledTonalButton(
                        onClick = { onSaveClick() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )

                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "save",
                            tint = MyRed
                        )
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(0.dp, 4.dp)
                        )
                    }
                } else {
                    FilledTonalButton(
                        onClick = { onSaveClick() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "save",
                            tint = MyRed
                        )
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(0.dp, 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (isCooked) {
                    FilledTonalButton(
                        onClick = { onCookClick() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )

                    ) {
                        Icon(
                            imageVector = Icons.Default.EggAlt,
                            contentDescription = "cooked",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cooked",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(0.dp, 4.dp)
                        )
                    }
                } else {
                    FilledTonalButton(
                        onClick = { onCookClick() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                    ) {
                        Icon(
                            imageVector = Icons.Default.Egg,
                            contentDescription = "cook",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cook",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(0.dp, 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                FilledTonalButton(
                    onClick = { importRecipe() },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    enabled = ( SessionManagerFacade.currentUserId!= recipe.author.id )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "import",
                        tint = MyRed
                    )
                    Text(
                        text = "Import",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                }
            }
        }

        //time,difficulty,cost
        item {
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "\uD83D\uDD52",
                        fontSize = 30.sp,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recipe.time.toString() + " min",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = recipe.difficulty.emoji,
                        fontSize = 30.sp,
                        //fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                    Text(
                        text = "LEVEL",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recipe.difficulty.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = "€",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                    Text(
                        text = "COST",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recipe.cost.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(0.dp, 4.dp)
                    )
                }
            }
        }

        //Ingredients
        item {
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 4.dp),
                    fontWeight = FontWeight.Bold
                )

                //servings
                Box(
                    modifier = Modifier
                        //.padding(12.dp,8.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick =  decrement
                        ) {
                            Text(
                                text = "-",
                                fontSize = 30.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = counter.toString() + " people",
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = increment
                        ) {
                            Text(
                                text = "+",
                                fontSize = 30.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        items(recipe.ingredients)
        { ingredient ->
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = " • ",
                    fontSize = 30.sp,
                    //style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = ((ingredient.quantity * counter) / recipe.servings).toString() + " " + ingredient.unityOfMeasurement + " " + ingredient.name,
                    style = MaterialTheme.typography.bodyLarge,
                    //fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }


        //Steps
        item {
            Row(
                Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preparation",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        itemsIndexed(recipe.steps)
        { index, step ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                    //containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .padding(0.dp, 8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    Text(
                        text = step.title,
                        modifier = Modifier
                            //.weight(1f)
                            .padding(8.dp, 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                AsyncImage(
                    model = step.picture,
                    contentDescription = "recipe picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Text(
                    text = step.description,
                    modifier = Modifier
                        //.weight(1f)
                        .padding(8.dp, 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    //fontSize = 18.sp,
                )
            }
        }

        //Reviews
        item{
            Row(Modifier
                .fillMaxWidth(.9f)
                .padding(0.dp, 8.dp),
                verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 4.dp),
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {onViewAllReviewsClick(recipe.id)}
                ) {
                    Text(
                        text = "View all",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(recipe.reviews.take(3)) { review ->
            ReviewComponent(review)
        }

        if(recipe.author.id != SessionManagerFacade.currentUserId) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth(.9f)
                        .padding(0.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { onWriteReviewClick(recipe.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "write review"
                            )

                            Spacer(modifier = Modifier.width(2.dp))

                            Text(
                                text = "Write a review",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                    }
                }
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeProposalScreen(
    idRecipe: String,
    onBack: () -> Unit,
    onViewAllReviewsClick: (String) -> Unit,
    onWriteReviewClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onCoverPictureClick: () -> Unit,
    onStepPictureClick: (Int) -> Unit,
    onTimePickerClick: (Int) -> Unit,
    onAuthRequired: () -> Unit,
    viewModel: RecipeViewModel
) {
    LaunchedEffect(idRecipe) {
        viewModel.loadRecipe(idRecipe)
    }

    val recipe by viewModel.recipe.collectAsState()
    val uiState by viewModel.newRecipe.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    val moreActionsExpanded by viewModel.moreActionsExpanded.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val counter by viewModel.count.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isCooked by viewModel.isCooked.collectAsState()

    if (uiState.isLoading) {
        LoadingScreen()
    }
    else {
        Column(Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("Recipe") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearRecipe()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    if (!isEditing) {
                        Box(Modifier) {
                            IconButton(onClick = { viewModel.showMoreActions() }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreHoriz,
                                    contentDescription = "More Actions"
                                )
                            };
                            DropdownMenu(
                                expanded = moreActionsExpanded,
                                onDismissRequest = { viewModel.hideMoreActions() },
                                modifier = Modifier.fillMaxWidth(0.4f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (recipe?.author?.id == SessionManagerFacade.currentUserId)
                                    DropdownMenuItem(
                                        text = { Text("Modify") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = null
                                            )
                                        },
                                        onClick = {
                                            viewModel.hideMoreActions()
                                            viewModel.enableEditMode()
                                        }
                                    )

                                DropdownMenuItem(
                                    text = { Text("Share") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Share,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        viewModel.hideMoreActions()
                                        viewModel.shareRecipeLink()
                                    }
                                )



                                if (recipe?.author?.id == SessionManagerFacade.currentUserId) {
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = MyRed) },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = null,
                                                tint = MyRed
                                            )
                                        },
                                        onClick = {
                                            viewModel.hideMoreActions()
                                            onDeleteClick()
                                        }
                                    )
                                }

                            }

                        }
                    }

                }
            )

            if (isEditing && recipe != null) {
                EditRecipeProposal(
                    viewModel = viewModel,
                    onRecipeCreated = { _ -> },
                    onCoverPictureClick = onCoverPictureClick,
                    onStepPictureClick = onStepPictureClick,
                    onTimePickerClick = onTimePickerClick
                )
            } else if (isImporting && recipe != null) {
                EditRecipeProposal(
                    viewModel = viewModel,
                    onRecipeCreated = { newId -> viewModel.loadRecipe(newId) },
                    onCoverPictureClick = onCoverPictureClick,
                    onStepPictureClick = onStepPictureClick,
                    onTimePickerClick = onTimePickerClick
                )
            } else if (recipe != null) {


                ShowRecipeProposalDetails(
                    recipe = recipe as Recipe,
                    increment = viewModel::increment,
                    decrement = viewModel::decrement,
                    counter = counter,
                    isSaved = isSaved,
                    isCooked = isCooked,
                    onViewAllReviewsClick = onViewAllReviewsClick,
                    onWriteReviewClick = onWriteReviewClick,
                    onSaveClick = { viewModel.onSaveClick(onAuthRequired = onAuthRequired) },
                    onCookClick = { viewModel.onCookClick(onAuthRequired = onAuthRequired) },
                    importRecipe = { viewModel.importRecipe(onAuthRequired = onAuthRequired) },
                )
            }

        }
    }


}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Recipe",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this recipe? This action cannot be undone.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Delete", color = MaterialTheme.colorScheme.onError)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

