package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Cost
import com.example.myapplication.data.Difficulty
import com.example.myapplication.data.Ingredient
import com.example.myapplication.data.Step
import coil.compose.AsyncImage
import com.example.myapplication.data.IngredientOption
import com.example.myapplication.ui.commoncomponents.TimeMinutesPickerField


@Composable
fun EditRecipeProposal(
    viewModel: RecipeViewModel,
    onRecipeCreated: (String) -> Unit,
    onCoverPictureClick: () -> Unit,
    onStepPictureClick: (Int) -> Unit,
    onTimePickerClick: (Int) -> Unit
) {
    val uiState by viewModel.newRecipe.collectAsStateWithLifecycle()
    val ingredients by viewModel.availableIngredients.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val idRecipeCreated by viewModel.idRecipeCreated.collectAsStateWithLifecycle()

    LaunchedEffect(idRecipeCreated) {
        if (idRecipeCreated.isNotBlank()) {
            val newId = idRecipeCreated
            viewModel.resetIdRecipeCreated()
            onRecipeCreated(newId)
        }
    }

    CreationPane(
        uiState = uiState,
        availableIngredients = ingredients,
        onTitleChanged = { viewModel.updateTitle(it) },
        onPictureChanged = { viewModel.updatePicture(it) },
        onCostChanged = { viewModel.updateCost(it) },
        onDifficultyChanged = { viewModel.updateDifficulty(it) },
        onCuisineChanged = { viewModel.updateCuisine(it) },
        onTimeChanged = { viewModel.updateTime(it) },
        onServingsChanged = { viewModel.updateServings(it) },
        onStepChanged = { index, step -> viewModel.updateStep(index, step) },
        onStepRemove = { index -> viewModel.removeStep(index) },
        onAddStep = { viewModel.addStep() },
        onRestrictionsChanged = { restriction -> viewModel.updateRestrictions(restriction) },
        onAddIngredient = { viewModel.addIngredient() },
        onRemoveIngredient = { index -> viewModel.removeIngredient(index) },
        onUpdateIngredient = { index, quantity -> viewModel.updateIngredient(index, quantity) },
        onSelectIngredient = { index, ingredient -> viewModel.selectIngredient(index, ingredient) },
        onAddClicked = { viewModel.addRecipe() },
        isEditing = isEditing,
        onCoverPictureClick = onCoverPictureClick,
        onStepPictureClick = onStepPictureClick,
        onTimePickerClick = onTimePickerClick
    )
}

@Composable
fun CreationPane(
    uiState: RecipeUiState,
    availableIngredients: List<IngredientOption>,
    onTitleChanged: (String) -> Unit,
    onPictureChanged: (String) -> Unit,
    onCostChanged: (Cost) -> Unit,
    onDifficultyChanged: (Difficulty) -> Unit,
    onCuisineChanged: (String) -> Unit,
    onTimeChanged: (Int) -> Unit,
    onServingsChanged: (String) -> Unit,
    onRestrictionsChanged: (String) -> Unit,
    onStepChanged: (Int, Step) -> Unit,
    onStepRemove: (Int) -> Unit,
    onAddStep: () -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (Int) -> Unit,
    onUpdateIngredient: (Int, Int) -> Unit,
    onSelectIngredient: (Int, IngredientOption) -> Unit,
    onAddClicked: () -> Unit,
    isEditing: Boolean,
    onCoverPictureClick: () -> Unit,
    onStepPictureClick: (Int) -> Unit,
    onTimePickerClick: (Int) -> Unit
) {


    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        //title
        item {
            TitleField(
                title = uiState.inputTitle,
                onTitleChanged = onTitleChanged
            )
            if (uiState.validation.titleError.isNotBlank()) {
                Text(
                    text = uiState.validation.titleError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        //Cover photo
        item {
            CoverPhotoField(
                image = uiState.inputPicture,
                onImageClick = onCoverPictureClick
            )
            if (uiState.validation.pictureError.isNotBlank()) {
                Text(
                    text = uiState.validation.pictureError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        //cost
        item {
            CostSelector(
                selectedCost = uiState.inputCost,
                onCostSelected = onCostChanged
            )
        }

        //difficulty
        item {
            DifficultySelector(
                selectedDifficulty = uiState.inputDifficulty,
                onDifficultySelected = onDifficultyChanged
            )
        }

        //preparation time
        item {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text(
                    text = "Preparation Time",
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                TimeMinutesPickerField(
                    minutes = uiState.inputTime,
                    minutesLabel = uiState.inputTimeLabel,
                    onClick = { onTimePickerClick(uiState.inputTime) }
                )

                if (uiState.validation.timeError.isNotBlank()) {
                    Text(
                        text = uiState.validation.timeError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        //type of cuisine
        item {
            CuisineSelector(
                selectedCuisine = uiState.inputCuisine,
                options = uiState.availableCuisines,
                onCuisineSelected = onCuisineChanged
            )
        }

        //restriction
        item {
            RestrictionsSelector(
                restrictionsSelected = uiState.inputRestrictions,
                options = uiState.availableRestrictions,
                onRestrictionsSelected = onRestrictionsChanged
            )
        }

        //servings
        item {
            ServingsField(
                servings = uiState.inputServings,
                onServingsChanged = onServingsChanged
            )
            if (uiState.validation.servingsError.isNotBlank()) {
                Text(
                    text = uiState.validation.servingsError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }


        //ingredients
        item {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                //eggs
                IngredientEggsForm(
                    quantity = uiState.inputIngredients[0].quantity,
                    onQuantityChange = { newQuantity -> onUpdateIngredient(0, newQuantity) }
                )

                //other ingredients
                uiState.inputIngredients
                    .drop(1)
                    .forEachIndexed { index, ingredient ->

                        IngredientForm(
                            ingredient = ingredient,
                            availableIngredients = availableIngredients,
                            onSelect = { selectedIngredient ->
                                onSelectIngredient(
                                    index + 1,
                                    selectedIngredient
                                )
                            },
                            onQuantityChange = { quantity ->
                                onUpdateIngredient(
                                    index + 1,
                                    quantity
                                )
                            },
                            onRemove = { onRemoveIngredient(index + 1) }
                        )
                    }

                //add another ingredient
                OutlinedButton(
                    onClick = onAddIngredient,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = "+ Add one more ingredient",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            if (uiState.validation.eggError.isNotBlank()) {
                Text(
                    text = uiState.validation.eggError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (uiState.validation.ingredientsError.isNotBlank()) {
                Text(
                    text = uiState.validation.ingredientsError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        //preparation
        item {
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text("Preparation")

                //step
                uiState.inputSteps.forEachIndexed { index, step ->

                    StepForm(
                        step = step,
                        onStepChanged = { updatedStep -> onStepChanged(index, updatedStep) },
                        onRemove = { onStepRemove(index) },
                        onImageClick = { onStepPictureClick(index) }
                    )
                }

                //add new step
                OutlinedButton(
                    onClick = onAddStep,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = "+ Add one more step",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            if (uiState.validation.stepsError.isNotBlank()) {
                Text(
                    text = uiState.validation.stepsError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        //create button
        item {
            FilledTonalButton(
                onClick = { onAddClicked() },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isEditing) {
                        "MODIFY RECIPE"
                    } else {
                        "CREATE RECIPE"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(0.dp, 8.dp)
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(0.9f)
                )
            } else if (!uiState.validation.isValid) {
                Text(
                    text = if (isEditing) {
                        "ERROR : unable to edit the recipe. Please check the data."
                    } else {
                        "ERROR : unable to create the recipe. Please check the data."
                    },
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(0.9f)
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCreationScreen(
    viewModel: RecipeViewModel,
    onBack: () -> Unit,
    onCoverPictureClick: () -> Unit,
    onStepPictureClick: (Int) -> Unit,
    onTimePickerClick: (Int) -> Unit
) {
    val uiState by viewModel.newRecipe.collectAsStateWithLifecycle()
    if (uiState.isLoading) {
        LoadingScreen()
    }
    else {
        Column(Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("New Recipe") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                        viewModel.resetRecipe()
                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    //
                }
            )

            EditRecipeProposal(
                viewModel = viewModel,
                onRecipeCreated = { _ -> onBack() },
                onCoverPictureClick = onCoverPictureClick,
                onStepPictureClick = onStepPictureClick,
                onTimePickerClick = onTimePickerClick
            )
        }
    }

}


//TYPE OF CUISINE
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CuisineSelector(
    selectedCuisine: String,
    options: List<String>,
    onCuisineSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = "Type of cuisine")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                options.forEach { option ->

                    val isSelected = option == selectedCuisine

                    FilledTonalButton(
                        onClick = { onCuisineSelected(option) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface,

                            contentColor =
                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            width =
                                if (isSelected) 1.dp
                                else 0.dp,
                            color =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Text(option)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


//RESTRICTIONS
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RestrictionsSelector(
    restrictionsSelected: List<String>,
    options: List<String>,
    onRestrictionsSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(text = "Restrictions")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                options.forEach { option ->

                    val isSelected = option in restrictionsSelected

                    FilledTonalButton(
                        onClick = {
                            onRestrictionsSelected(option)
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surface,

                            contentColor =
                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            width =
                                if (isSelected) 1.dp
                                else 0.dp,
                            color =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Text(option)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


//TITLE
@Composable
fun TitleField(
    title: String,
    onTitleChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = "Recipe title",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Apple Pie") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

//COST
@Composable
fun CostSelector(
    selectedCost: Cost,
    onCostSelected: (Cost) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = "Estimated Cost",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Cost.entries.forEach { cost ->

                val isSelected = cost == selectedCost

                FilledTonalButton(
                    onClick = { onCostSelected(cost) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor =
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,

                        contentColor =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        width =
                            if (isSelected) 1.dp
                            else 0.dp,
                        color =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Text(
                        text = cost.symbol,
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

//DIFFICULTY
@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = "Difficulty",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Difficulty.entries.forEach { difficulty ->

                val isSelected = difficulty == selectedDifficulty

                FilledTonalButton(
                    onClick = { onDifficultySelected(difficulty) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor =
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,

                        contentColor =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        width =
                            if (isSelected) 1.dp
                            else 0.dp,
                        color =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = difficulty.emoji,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = difficulty.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

//SERVINGS
@Composable
fun ServingsField(
    servings: String,
    onServingsChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = "Servings",
            modifier = Modifier.padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = servings,
            onValueChange = onServingsChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Number of servings") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Diversity3,
                    contentDescription = "people",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

//COVER PHOTO
@Composable
fun CoverPhotoField(
    image: String,
    onImageClick: () -> Unit
) {
    Column() {

        Text(
            text = "Cover Photo",
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 4.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth(0.9f),
            contentAlignment = Alignment.Center
        ) {

            OutlinedButton(
                onClick = onImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {

                if (image.isNotBlank()) {
                    AsyncImage(
                        model = image,
                        contentDescription = "Cover image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )


                } else {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "camera",
                                tint = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Text(
                            text = "Insert Image",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Upload a high quality photo of your dish",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

//STEPS
@Composable
fun StepForm(
    step: Step,
    onStepChanged: (Step) -> Unit,
    onRemove: () -> Unit,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //step title
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = step.title,
                    onValueChange = { newTitle ->
                        onStepChanged(step.copy(title = newTitle))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 12.dp),
                    placeholder = { Text("Add step title") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                //remove step
                TextButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove step",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //step picture
            OutlinedButton(
                onClick = onImageClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                if (step.picture.isNotBlank()) {
                    AsyncImage(
                        model = step.picture,
                        contentDescription = "Step image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add image",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Text(
                            text = "Add Image",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            //step description
            OutlinedTextField(
                value = step.description,
                onValueChange = { newDesc ->
                    onStepChanged(step.copy(description = newDesc))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 12.dp),
                placeholder = { Text("Describe the step here") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}


//INGREDIENTS
@Composable
fun IngredientEggsForm(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text("Ingredients")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {

                Text("How many eggs? 🥚")

                Spacer(modifier = Modifier.height(8.dp))

                //number of eggs
                OutlinedTextField(
                    value = if (quantity == 0) "" else quantity.toString(),
                    onValueChange = { onQuantityChange(it.toIntOrNull() ?: 0) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun IngredientForm(
    ingredient: Ingredient,
    availableIngredients: List<IngredientOption>,
    onSelect: (IngredientOption) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            //ingredient quantity
            OutlinedTextField(
                value = if (ingredient.quantity == 0) "" else ingredient.quantity.toString(),
                onValueChange = { onQuantityChange(it.toIntOrNull() ?: 0) },
                modifier = Modifier.weight(0.3f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            //chose ingredient from the list (with unit)
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)) {

                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text =
                            if (ingredient.name.isBlank()) "Select ingredient"
                            else "${ingredient.name} (${ingredient.unityOfMeasurement})",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableIngredients.forEach { ingredient ->
                        DropdownMenuItem(
                            text = { Text("${ingredient.name} (${ingredient.unityOfMeasurement})") },
                            onClick = {
                                onSelect(ingredient)
                                expanded = false
                            }
                        )
                    }
                }
            }

            //remove ingredient
            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete ingredient",
                )
            }
        }
    }
}