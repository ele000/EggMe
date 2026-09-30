package com.example.myapplication.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.myapplication.ui.AuthenticationScreen
import com.example.myapplication.ui.AuthenticationViewModel

fun NavGraphBuilder.authGraph(navController: NavHostController, avm: AuthenticationViewModel) {
    navigation<AuthGraph>(startDestination = LoginRoute) {

        composable<LoginRoute> {
            AuthenticationScreen(
                viewModel = avm,
                onSuccess = {
                    navController.navigate(MainGraph) {
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                },
                onNewUser = {
                    navController.navigate(CreateProfileRoute) {
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                }
            )
        }
    }
}
