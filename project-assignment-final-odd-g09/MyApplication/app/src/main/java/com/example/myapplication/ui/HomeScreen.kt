package com.example.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.data.Notification
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.ui.commoncomponents.RecipeCardItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    notificationViewModel: NotificationViewModel,
    onRecipeClick: (String) -> Unit,
    onAuthRequired: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    val recipesPickedForYou by viewModel.recipesPickedForYou.collectAsState()
    val recipesMostLoved by viewModel.recipesMostLoved.collectAsState()
    val recipesTodayBest by viewModel.recipesTodayBest.collectAsState()
    val recipesTrySomethingNew by viewModel.recipesTrySomethingNew.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(end=16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.gallina),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(vertical = 5.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "EggMe",
                modifier = Modifier.padding(vertical = 6.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            NotificationButton(notifications, onNotificationClick)
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            if (SessionManagerFacade.isLoggedIn && (recipesPickedForYou.size > 2) ) {
                item {
                    Text(
                        text = "PICKED FOR YOU",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipesPickedForYou) { recipeWIthSaved ->
                            RecipeCardItem(
                                recipeWithSaved = recipeWIthSaved,
                                modifier = Modifier.width(210.dp),
                                scale = 1.15f,
                                onClick = onRecipeClick,
                                onToggleFavorite = { recipe ->
                                    viewModel.onToggleFavorite(
                                        recipe,
                                        onAuthRequired = onAuthRequired
                                    )
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "MOST LOVED",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipesMostLoved) { recipeWithSaved ->
                        RecipeCardItem(
                            recipeWithSaved = recipeWithSaved,
                            modifier = Modifier.width(210.dp),
                            scale = 1.15f,
                            onClick = onRecipeClick,
                            onToggleFavorite = { recipe ->
                                viewModel.onToggleFavorite(recipe, onAuthRequired = onAuthRequired)
                            }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "TODAY'S BEST",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipesTodayBest) { recipe ->
                        RecipeCardItem(
                            recipeWithSaved = recipe,
                            modifier = Modifier.width(210.dp),
                            scale = 1.15f,
                            onClick = onRecipeClick,
                            onToggleFavorite = { recipe ->
                                viewModel.onToggleFavorite(recipe, onAuthRequired = onAuthRequired)
                            }
                        )
                    }
                }
            }

            if (recipesTrySomethingNew.size >= 2) { //Since we have only a few recipe, this list could be empty or very little
                item {
                    Text(
                        text = "TRY SOMETHING NEW",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipesTrySomethingNew) { recipe ->
                            RecipeCardItem(
                                recipeWithSaved = recipe,
                                modifier = Modifier.width(210.dp),
                                scale = 1.15f,
                                onClick = onRecipeClick,
                                onToggleFavorite = { recipe ->
                                    viewModel.onToggleFavorite(recipe, onAuthRequired = onAuthRequired)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun NotificationButton(
    notifications: List<Notification>,
    onNotificationClick: () -> Unit
) {

    val numNotifications = notifications.count{!it.isRead}

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BadgedBox(
            badge = {
                if (numNotifications > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp).size(24.dp)
                    ) {
                        Text(
                            text = numNotifications.toString(),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        ) {
            IconButton(
                onClick = onNotificationClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    modifier = Modifier.size(36.dp)
                    //modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}