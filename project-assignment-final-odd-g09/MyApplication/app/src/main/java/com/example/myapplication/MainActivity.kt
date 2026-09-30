package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import com.example.myapplication.ui.RecipeListViewModel
import com.example.myapplication.ui.RecipeViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.domain.GetAuthUserDataUseCase
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.domain.DeleteReadNotificationUseCase
import com.example.myapplication.domain.GetMostLovedRecipesUseCase
import com.example.myapplication.domain.GetNotificationsByUserUseCase
import com.example.myapplication.domain.GetPickedForYouRecipesUseCase
import com.example.myapplication.domain.GetRecipesUseCase
import com.example.myapplication.domain.GetTodayBestRecipesUseCase
import com.example.myapplication.domain.GetTrySomethingNewRecipesUseCase
import com.example.myapplication.ui.UserViewModel
import com.example.myapplication.ui.navigation.*
import com.example.myapplication.domain.GetUserListsUseCase
import com.example.myapplication.domain.GetUserRecipesUseCase
import com.example.myapplication.domain.RemoveRecipeFromUserListUseCase
import com.example.myapplication.domain.SaveRecipeInUserListUseCase
import com.example.myapplication.domain.ToggleFavoriteRecipeUseCase
import com.example.myapplication.domain.UpdateNotificationReadUseCase
import com.example.myapplication.ui.AuthenticationViewModel
import com.example.myapplication.ui.FindYourFarmersViewModel
import com.example.myapplication.ui.HomeViewModel
import com.example.myapplication.ui.MainViewModel
import com.example.myapplication.ui.NotificationViewModel
import com.example.myapplication.ui.OwnedRecipeListViewModel
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        (application as MyApplication).container
    }

    private val recipeRepository by lazy { appContainer.recipeRepository }
    private val userRepository by lazy { appContainer.userRepository }
    private val ingredientRepository by lazy { appContainer.ingredientRepository }
    private val metadataRepository by lazy { appContainer.metadataRepository }
    private val notificationRepository by lazy { appContainer.notificationRepository }

    //domain
    private val getUserListsUseCase by lazy { GetUserListsUseCase(userRepository, recipeRepository) }
    private val saveRecipeInUserListUseCase by lazy {SaveRecipeInUserListUseCase(userRepository)}
    private val removeRecipeFromUserListUseCase by lazy { RemoveRecipeFromUserListUseCase(userRepository)}
    private val toggleFavoriteRecipeUseCase by lazy { ToggleFavoriteRecipeUseCase(userRepository)}
    private val getPickedForYouRecipesUseCase by lazy { GetPickedForYouRecipesUseCase(userRepository, recipeRepository)}
    private val getMostLovedRecipesUseCase by lazy { GetMostLovedRecipesUseCase(userRepository, recipeRepository)}
    private val getTodayBestRecipesUseCase by lazy { GetTodayBestRecipesUseCase(userRepository, recipeRepository)}
    private val getTrySomethingNewRecipesUseCase by lazy { GetTrySomethingNewRecipesUseCase(userRepository, recipeRepository)}
    private val getRecipesUseCase by lazy { GetRecipesUseCase(userRepository, recipeRepository)}
    private val getUserRecipesUseCase by lazy { GetUserRecipesUseCase(userRepository, recipeRepository)}

    private val getAuthUserDataUseCase by lazy { GetAuthUserDataUseCase(userRepository)}
    private val getNotificationsByUserUseCase by lazy { GetNotificationsByUserUseCase(notificationRepository)}
    private val updateNotificationReadUseCase by lazy { UpdateNotificationReadUseCase(notificationRepository)}

    private val deleteReadNotificationUseCase by lazy { DeleteReadNotificationUseCase(notificationRepository) }

    private val avm: AuthenticationViewModel by viewModels {
        AuthenticationViewModel.provideFactory(
            userRepository
        )
    }
    private val hvm: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(
            toggleFavoriteRecipeUseCase,
            getPickedForYouRecipesUseCase,
            getMostLovedRecipesUseCase,
            getTodayBestRecipesUseCase,
            getTrySomethingNewRecipesUseCase
        )
    }

    private val nvm: NotificationViewModel by viewModels {
        NotificationViewModel.provideFactory(
            getNotificationsByUserUseCase,
            updateNotificationReadUseCase,
            deleteReadNotificationUseCase
        )
    }

    private val rlvm: RecipeListViewModel by viewModels {
        RecipeListViewModel.provideFactory(
            userRepository,
            ingredientRepository,
            metadataRepository,
            toggleFavoriteRecipeUseCase,
            getRecipesUseCase,
            getUserRecipesUseCase
        )
    }

    private val rvm: RecipeViewModel by viewModels {
        RecipeViewModel.provideFactory(
            recipeRepository,
            ingredientRepository,
            userRepository,
            metadataRepository,
            saveRecipeInUserListUseCase,
            removeRecipeFromUserListUseCase
        )
    }

    private val uvm: UserViewModel by viewModels {
        UserViewModel.provideFactory(
            userRepository,
            metadataRepository,
            ingredientRepository
        )
    }

    private val orlvm: OwnedRecipeListViewModel by viewModels {
        OwnedRecipeListViewModel.provideFactory(
            userRepository,
            getUserListsUseCase
        )
    }

    private val fvm: FindYourFarmersViewModel by viewModels {
        FindYourFarmersViewModel.provideFactory()
    }

    private val mvm: MainViewModel by viewModels {
        MainViewModel.provideFactory(
            getAuthUserDataUseCase
        )
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var pendingIntent by mutableStateOf<Intent?>(null)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingIntent = intent
    }

    //from https://developer.android.com/training/sharing/send?hl=it
    fun shareRecipeLink(context: Context, idRecipe: String) {
        val sendIntent: Intent = Intent().apply {
            action = ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hey! Check this EggTastic recipe!  \n\nhttps://eggme.com/recipe/$idRecipe")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pendingIntent = intent

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        //request permission to send notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        //open a channel to receive notifications
        val channelId = "recipe_notifications"
        val channelName = "Recipe notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Receive notification about your recipes or suggestions"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        setContent {
            val authUser by mvm.authUser.collectAsStateWithLifecycle()

            val navController = rememberNavController()

            LaunchedEffect(pendingIntent) {
                pendingIntent?.let { navController.handleDeepLink(it) }
            }

            val snackbarHostState = remember { SnackbarHostState() }
            //val notification by hvm.lastNotificationForSnackbar.collectAsStateWithLifecycle()


            LaunchedEffect(Unit) {
                SessionManagerFacade.currentUserStateFlow.collect { userId ->
                    if (userId != null) {
                        try {
                            val token = FirebaseMessaging.getInstance().token.await()
                            userRepository.addFcmToken(userId, token)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            LaunchedEffect(hvm) {
                nvm.lastNotificationForSnackbar.collect {notification ->

                    val result = snackbarHostState.showSnackbar(
                        message = "New notification : "+notification.title,
                        actionLabel = "View",
                        duration = SnackbarDuration.Long,
                        withDismissAction = true
                    )

                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            navController.navigate(RecipeDetailRoute(notification.recipeId))
                            nvm.onNotificationClicked(notification)
                        }
                        SnackbarResult.Dismissed -> { }
                    }

                }
            }

            val context = LocalContext.current
            LaunchedEffect(rvm) {
                rvm.shareEvent.collect { idRecipe ->
                    shareRecipeLink(context, idRecipe)
                }
            }


            MyApplicationTheme(darkTheme = false) {
                //val navController = rememberNavController()


                Scaffold(

                    snackbarHost = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .statusBarsPadding()
                                    .padding(top = 150.dp)
                                    //.padding(top = 8.dp)
                                    //.offset(y = 16.dp)
                            )
                            { data ->
                                Snackbar(
                                    snackbarData = data,
                                    //containerColor = MaterialTheme.colorScheme.primary,
                                    //contentColor = MaterialTheme.colorScheme.onPrimary,
                                    actionColor = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },

                    bottomBar = {
                        MyBottomBar(
                            navController, authUser = authUser,
                            onProfileClick = {
                                if (SessionManagerFacade.isLoggedIn) {
                                    navController.navigate(MyRecipesRoute)
                                } else {
                                    navController.navigate(AuthGraph)
                                }
                            },
                            onCreateClick = {
                                if (SessionManagerFacade.isLoggedIn) {
                                    navController.navigate(CreateRecipeRoute)
                                } else {
                                    navController.navigate(AuthGraph)
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = MainGraph
                        ) {
                            authGraph(
                                navController= navController,
                                avm = avm)
                            mainGraph(
                                navController = navController,
                                orlvm = orlvm,
                                rlvm = rlvm,
                                rvm = rvm,
                                uvm = uvm,
                                hvm = hvm,
                                nvm = nvm,
                                fvm = fvm
                            )
                        }
                    }
                }
            }
        }
    }
}
