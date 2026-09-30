package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Author
import com.example.myapplication.ui.commoncomponents.ProfilePicture


@Composable
fun MyBottomBar(
    navController: NavHostController,
    authUser: Author?,
    onProfileClick: () -> Unit,
    onCreateClick: () -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar (){

        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true,
            onClick = { navController.navigate(HomeRoute) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home",modifier = Modifier.size(30.dp)) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute<RecipesListRoute>() } == true,
            onClick = { navController.navigate(RecipesListRoute) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search",modifier = Modifier.size(30.dp)) },
            label = { Text("Search") }
        )

        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute<CreateRecipeRoute>() } == true,
            onClick = onCreateClick,
            icon = { Icon(Icons.Default.Add, contentDescription = "Create",modifier = Modifier.size(30.dp)) },
            label = { Text("Create") }
        )

        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute<EggRoute>() } == true,
            onClick = { navController.navigate(EggRoute) },
            icon = { Icon(Icons.Outlined.Egg, contentDescription = "Farmers",modifier = Modifier.size(30.dp)) },
            label = { Text("Farmers") }
        )

        NavigationBarItem(
            selected = currentDestination?.hierarchy?.any { it.hasRoute<MyRecipesRoute>() } == true,
            onClick = onProfileClick,
            icon = {
                if (authUser != null) {
                    ProfilePicture(
                        author = authUser,
                        size = 30.dp
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Profile",modifier = Modifier.size(30.dp))
                }
            },
            label = { Text("Profile") }
        )
    }
}