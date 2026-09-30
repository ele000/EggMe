package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.commoncomponents.RecipeCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeProposalList(
    viewModel: RecipeListViewModel, 
    onRecipeClick: (String) -> Unit, 
    onFilterClick: () -> Unit,    
    onAuthRequired: () -> Unit
) {

    val recipes by viewModel.filteredRecipes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Search Recipe")},
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),

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
                onClick = onFilterClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Open Filters",
                )
            }
        }


                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipes) { recipe ->
                            RecipeCardItem(
                                recipeWithSaved = recipe,
                                onClick = onRecipeClick,
                                onToggleFavorite =  { recipe ->
                                    viewModel.onToggleFavorite(recipe, onAuthRequired = onAuthRequired)
                                }
                            )
                        }
                    }
                }
            }




