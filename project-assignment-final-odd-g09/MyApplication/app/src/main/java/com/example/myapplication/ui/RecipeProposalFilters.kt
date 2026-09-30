package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Cost
import com.example.myapplication.data.Difficulty
import com.example.myapplication.ui.commoncomponents.TimeMinutesPickerField

private const val FIXED_EGGS_FILTER = "EGGS"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    viewModel: RecipeListViewModel,
    onBack: () -> Unit,
    onTimePickerClick: (Int, String) -> Unit,
    onAddIngredientClick: (List<String>, List<String>) -> Unit,
    onAddCuisineClick: (List<String>, List<String>) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val ingredientFilters by viewModel.ingredientFilters.collectAsState()
    val availableIngredients by viewModel.availableIngredients.collectAsState()
    val selectedCost by viewModel.selectedCostFilter.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficultyFilter.collectAsState()
    val maxTimeMinutes by viewModel.maxTimeMinutes.collectAsState()
    val maxTimeLabel by viewModel.maxTimeLabel.collectAsState()
    val availableCuisines by viewModel.availableCuisines.collectAsState()
    val selectedCuisine by viewModel.selectedCuisineFilter.collectAsState()
    val recipesCount by viewModel.filteredRecipes.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Search Recipe") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0)

            )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search recipes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Filters",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        FiltersPanel(
            ingredientFilters = ingredientFilters,
            availableIngredients = availableIngredients,
            onAddIngredientClick = { onAddIngredientClick(availableIngredients, ingredientFilters) },
            onRemoveIngredient = viewModel::removeIngredientFilter,
            selectedCost = selectedCost,
            onSelectCost = viewModel::setCostFilter,
            selectedDifficulty = selectedDifficulty,
            onSelectDifficulty = viewModel::setDifficultyFilter,
            maxTimeMinutes = maxTimeMinutes,
            onSetMaxTime = viewModel::setMaxTimeFilter,
            maxTimeLabel = maxTimeLabel,
            onClearFilters = viewModel::clearFilters,
            onShowRecipes = onBack,
            recipesCount = recipesCount.size,
            availableCuisines = availableCuisines,
            selectedCuisine = selectedCuisine,
            onAddCuisineClick = { 
                onAddCuisineClick(availableCuisines, if (selectedCuisine != null) listOf(selectedCuisine!!) else emptyList()) 
            },
            onRemoveCuisine = { viewModel.setCuisineFilter(null) },
            onTimePickerClick = onTimePickerClick
        )
    }
}

@Composable
fun FiltersPanel(
    ingredientFilters: List<String>,
    availableIngredients: List<String>,
    onAddIngredientClick: () -> Unit,
    onRemoveIngredient: (String) -> Unit,
    selectedCost: Cost?,
    onSelectCost: (Cost) -> Unit,
    selectedDifficulty: Difficulty?,
    onSelectDifficulty: (Difficulty) -> Unit,
    maxTimeMinutes: Int,
    onSetMaxTime: (Int) -> Unit,
    maxTimeLabel: String,
    onClearFilters: () -> Unit,
    onShowRecipes: () -> Unit,
    recipesCount: Int,
    availableCuisines: List<String>,
    selectedCuisine: String?,
    onAddCuisineClick: () -> Unit,
    onRemoveCuisine: () -> Unit,
    onTimePickerClick: (Int, String) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Filter by Ingredients", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        EditableFilterRow(
            items = ingredientFilters,
            onAddItemClick = onAddIngredientClick,
            onRemoveItem = onRemoveIngredient,
            isIngredientSection = true // Per gestire il filtro fisso delle uova
        )

        Text("Cuisine Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        EditableFilterRow(
            items = if (selectedCuisine != null) listOf(selectedCuisine!!) else emptyList(),
            onAddItemClick = onAddCuisineClick,
            onRemoveItem = { onRemoveCuisine() }
        )

        Text("Filter by Cost", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Cost.values().forEach { cost ->
                FilterOption(
                    text = cost.symbol,
                    selected = selectedCost == cost,
                    onClick = { onSelectCost(cost) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text("Filter by Difficulty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Difficulty.values().forEach { diff ->
                FilterOption(
                    text = diff.emoji,
                    selected = selectedDifficulty == diff,
                    onClick = { onSelectDifficulty(diff) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Text("Set Max Time", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TimeMinutesPickerField(
            minutes = maxTimeMinutes,
            minutesLabel = maxTimeLabel,
            onClick = { onTimePickerClick(maxTimeMinutes, "Set max time") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onClearFilters,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text("Clear filters", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }

        Button(
            onClick = onShowRecipes,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Show $recipesCount recipes", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableFilterRow(
    items: List<String>,
    onAddItemClick: () -> Unit,
    onRemoveItem: (String) -> Unit,
    isIngredientSection: Boolean = false
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            val isFixed = isIngredientSection && item.equals(FIXED_EGGS_FILTER, ignoreCase = true)
            InputChip(
                selected = false,
                onClick = { },
                label = { Text(text = item, style = MaterialTheme.typography.bodyMedium, color = Color.Black) },
                trailingIcon = if (isFixed) null else {
                    {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(18.dp).clickable { onRemoveItem(item) }
                        )
                    }
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = if (isFixed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )
        }

        InputChip(
            selected = false,
            onClick = onAddItemClick,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("ADD", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            },
            colors = InputChipDefaults.inputChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp),
            border = null
        )
    }
}


@Composable
private fun FilterOption(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(42.dp).clickable { onClick() },
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary) else null
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}