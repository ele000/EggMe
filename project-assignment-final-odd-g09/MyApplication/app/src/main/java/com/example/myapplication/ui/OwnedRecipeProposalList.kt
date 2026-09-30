package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Author
import com.example.myapplication.data.toAuthor
import com.example.myapplication.ui.commoncomponents.ProfilePicture
import com.example.myapplication.ui.commoncomponents.RecipeCardItem
import com.example.myapplication.ui.commoncomponents.RecipeCardItemWithoutButton

@Composable
fun OwnedRecipeProposalList(
    viewModel: OwnedRecipeListViewModel,
    onRecipeClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit
) {
    val userLists by viewModel.userLists.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val user = currentUser ?: return


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer,RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProfilePicture(user.toAuthor(), size = 100.dp)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = user.userName.uppercase(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = user.cookingRole.uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        border = null
                    )
                }

                IconButton(
                    onClick = {onSettingsClick()},
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            userLists.forEach { title, list ->
                item {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (list.isEmpty()) {
                    item {
                        Text(
                            text = "This list is empty",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Light
                        )
                    }
                } else {
                    item {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(list) { recipe ->
                                RecipeCardItemWithoutButton(
                                    recipe = recipe,
                                    modifier = Modifier.width(210.dp),
                                    scale = 1.15f,
                                    onClick = onRecipeClick
                                )
                            }
                        }
                    }
                }

            }

            //TODO BOTTONE AGGIUNTA LISTE
        }
    }
}
