package com.example.myapplication.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.User
import com.example.myapplication.data.auth.SessionManagerFacade
import com.example.myapplication.data.toAuthor
import com.example.myapplication.ui.commoncomponents.ProfilePicture
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(uvm: UserViewModel, onBack: () -> Unit, onEditClick: () -> Unit,onLogout: () -> Unit) {
    val uiState by uvm.uiState.collectAsState()
    val loggedIn by uvm.isUserLoggedIn.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        val currentId = SessionManagerFacade.currentUserId
        if (currentId != null) {
            uvm.loadUser(currentId, isOwner = true)
        }
    }


    Column(
        Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text(if (uiState.isOwner) "Your Profile" else "Profile") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
            actions = {
                if (uiState.isOwner) {
                    IconButton(onClick = {
                        uvm.edit()
                        onEditClick()
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        )

            PresentationPane(
                user = uiState.user,
                isOwner = uiState.isOwner,
                isLoggedIn = loggedIn,
                onLogout = onLogout
            )
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uvm: UserViewModel,
    onBack: () -> Unit,
    onProfilePictureClick: () -> Unit,
    onAddIngredientClick: (List<String>, List<String>) -> Unit,
    onAddCuisineClick: (List<String>, List<String>) -> Unit,
    onAddRestrictionClick: (List<String>, List<String>) -> Unit
) {
    val uiState by uvm.uiState.collectAsState()

    BackHandler(enabled = uiState.isEditing) {
        uvm.validateAndSave()
    }

    LaunchedEffect(uiState.isEditing) {
        if (!uiState.isEditing) {
            onBack()
        }
    }
    if (uiState.isLoading) {
        LoadingScreen()
    }
    else {
        Column(
            Modifier.fillMaxSize()
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    IconButton(onClick = { uvm.validateAndSave() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and go back"
                        )
                    }
                },
                actions = {
                    Button(onClick = uvm::validateAndSave) {
                        Text("Done")
                    }
                }
            )
            EditPane(
                user = uiState.draftUser,
                validation = uiState.validation,
                ingredientOptions = uiState.ingredientOptions,
                cuisineOptions = uiState.cuisineOptions,
                restrictionOptions = uiState.restrictionOptions,
                onProfilePictureClick = onProfilePictureClick,
                onNameChange = uvm::setName,
                onSurnameChange = uvm::setSurname,
                onUserNameChange = uvm::setUserName,
                onCookingRoleChange = uvm::setCookingRole,
                onAddIngredient = uvm::addIngredient,
                onRemoveIngredient = uvm::removeIngredient,
                onAddCuisine = uvm::addCuisine,
                onRemoveCuisine = uvm::removeCuisine,
                onAddRestriction = uvm::addRestriction,
                onRemoveRestriction = uvm::removeRestriction,
                onAddIngredientClick = onAddIngredientClick,
                onAddCuisineClick = onAddCuisineClick,
                onAddRestrictionClick = onAddRestrictionClick
            )
        }
    }
    }


@Composable
fun PresentationPane(user: User, isOwner: Boolean, isLoggedIn: Boolean,onLogout: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                ProfilePicture(user.toAuthor())
            }
            Column(
                modifier = Modifier
                    .weight(2f)
                    .verticalScroll(scrollState)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileInfo(user, isOwner, isLoggedIn, onLogout)
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ProfilePicture(user.toAuthor())
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                ProfileInfo(user = user, isOwner = isOwner, isLoggedIn = isLoggedIn, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun EditPane(
    user: User,
    validation: FormValidation,
    ingredientOptions: List<String>,
    cuisineOptions: List<String>,
    restrictionOptions: List<String>,
    onProfilePictureClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onCookingRoleChange: (String) -> Unit,
    onAddIngredient: (String) -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onAddCuisine: (String) -> Unit,
    onRemoveCuisine: (String) -> Unit,
    onAddRestriction: (String) -> Unit,
    onRemoveRestriction: (String) -> Unit,
    onAddIngredientClick: (List<String>, List<String>) -> Unit,
    onAddCuisineClick: (List<String>, List<String>) -> Unit,
    onAddRestrictionClick: (List<String>, List<String>) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ProfilePicture(user.toAuthor())

                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .padding(bottom = 12.dp, end = 12.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {

                    FilledIconButton(
                        onClick = onProfilePictureClick,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo"
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .verticalScroll(scrollState)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileEditInfo(
                    user = user,
                    validation = validation,
                    ingredientOptions = ingredientOptions,
                    cuisineOptions = cuisineOptions,
                    restrictionOptions = restrictionOptions,
                    onNameChange = onNameChange,
                    onSurnameChange = onSurnameChange,
                    onUserNameChange = onUserNameChange,
                    onCookingRoleChange = onCookingRoleChange,
                    onAddIngredient = onAddIngredient,
                    onRemoveIngredient = onRemoveIngredient,
                    onAddCuisine = onAddCuisine,
                    onRemoveCuisine = onRemoveCuisine,
                    onAddRestriction = onAddRestriction,
                    onRemoveRestriction = onRemoveRestriction,
                    onAddIngredientClick = onAddIngredientClick,
                    onAddCuisineClick = onAddCuisineClick,
                    onAddRestrictionClick = onAddRestrictionClick
                )
            }
        }
    } else {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {

                ProfilePicture(user.toAuthor())
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .padding(bottom = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FilledIconButton(
                        onClick = onProfilePictureClick,
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo"
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                ProfileEditInfo(
                    user = user,
                    validation = validation,
                    ingredientOptions = ingredientOptions,
                    cuisineOptions = cuisineOptions,
                    restrictionOptions = restrictionOptions,
                    onNameChange = onNameChange,
                    onSurnameChange = onSurnameChange,
                    onUserNameChange = onUserNameChange,
                    onCookingRoleChange = onCookingRoleChange,
                    onAddIngredient = onAddIngredient,
                    onRemoveIngredient = onRemoveIngredient,
                    onAddCuisine = onAddCuisine,
                    onRemoveCuisine = onRemoveCuisine,
                    onAddRestriction = onAddRestriction,
                    onRemoveRestriction = onRemoveRestriction,
                    onAddIngredientClick = onAddIngredientClick,
                    onAddCuisineClick = onAddCuisineClick,
                    onAddRestrictionClick = onAddRestrictionClick
                )
            }
        }
    }
}

@Composable
fun ProfileEditInfo(
    user: User,
    validation: FormValidation,
    ingredientOptions: List<String>,
    cuisineOptions: List<String>,
    restrictionOptions: List<String>,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onUserNameChange: (String) -> Unit,
    onCookingRoleChange: (String) -> Unit,
    onAddIngredient: (String) -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onAddCuisine: (String) -> Unit,
    onRemoveCuisine: (String) -> Unit,
    onAddRestriction: (String) -> Unit,
    onRemoveRestriction: (String) -> Unit,
    onAddIngredientClick: (List<String>, List<String>) -> Unit,
    onAddCuisineClick: (List<String>, List<String>) -> Unit,
    onAddRestrictionClick: (List<String>, List<String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        EditField(
            label = "Username",
            value = user.userName,
            error = validation.userNameError,
            onValueChange = onUserNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        AssistChip(
            onClick = {},
            label = {
                Text(
                    text = user.cookingRole.uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.tertiary),
            border = null
        )

        EditField(
            label = "Cooking Role",
            value = user.cookingRole,
            error = validation.roleError,
            onValueChange = onCookingRoleChange
        )

        TitleAndText(
            title = "Email",
            text = user.email,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditField(
                label = "Name",
                value = user.name,
                error = validation.nameError,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f)
            )
            EditField(
                label = "Surname",
                value = user.surname,
                error = validation.surnameError,
                onValueChange = onSurnameChange,
                modifier = Modifier.weight(1f)
            )
        }

        EditableTitleAndTextList(
            title = "Favorite Ingredients",
            textList = user.favoriteIngredients,
            onRemoveItem = onRemoveIngredient,
            onAddClick = { onAddIngredientClick(ingredientOptions, user.favoriteIngredients)}
        )

        EditableTitleAndTextList(
            title = "Cuisine Type",
            textList = user.cuisineType,
            onRemoveItem = onRemoveCuisine,
            onAddClick = { onAddCuisineClick(cuisineOptions, user.cuisineType) }
        )

        EditableTitleAndTextList(
            title = "Restrictions",
            textList = user.restriction,
            onRemoveItem = onRemoveRestriction,
            onAddClick = { onAddRestrictionClick(restrictionOptions, user.restriction)}
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Tap 'Done' to save your changes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}


@Composable
fun EditField(
    label: String,
    value: String,
    error: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = keyboardOptions
        )
        if (error.isNotBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TitleAndText(title: String, text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun TitleAndTextList(title: String, textList: List<String>, modifier: Modifier = Modifier) {
    if (textList.isNotEmpty()) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    textList.forEach { item ->
                        if (item.isNotBlank()) {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary),
                                border = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditableTitleAndTextList(
    title: String,
    textList: List<String>,
    onRemoveItem: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                textList.forEach { item ->
                    if (item.isNotBlank()) {
                        InputChipItem(text = item, onRemove = { onRemoveItem(item) })
                    }
                }

                InputChip(
                    selected = false,
                    onClick = onAddClick,
                    label = { Icon(Icons.Default.Add, contentDescription = "Add new") },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}


@Composable
fun ProfileInfo(user: User, isOwner: Boolean, isLoggedIn: Boolean, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.userName.uppercase(),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp, 0.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        text = user.cookingRole.uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.tertiary),
                border = null
            )
        }
        if (isOwner) {
            TitleAndText(title = "Email", text = user.email, modifier = Modifier.fillMaxWidth())
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleAndText(title = "Name", text = user.name, modifier = Modifier.weight(1f))
            TitleAndText(title = "Surname", text = user.surname, modifier = Modifier.weight(1f))
        }
        TitleAndTextList(
            title = "Favorite Ingredients",
            textList = user.favoriteIngredients,
            modifier = Modifier.fillMaxWidth()
        )
        TitleAndTextList(
            title = "Cuisine Type",
            textList = user.cuisineType,
            modifier = Modifier.fillMaxWidth()
        )
        TitleAndTextList(
            title = "Restrictions",
            textList = user.restriction,
            modifier = Modifier.fillMaxWidth()
        )
        if (isOwner) {
            if (isLoggedIn) {
                Button(
                    onClick = {scope.launch {
                        SessionManagerFacade.logOut()
                        onLogout()}},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = "LOG OUT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputChipItem(
    text: String,
    onRemove: () -> Unit
) {
    InputChip(
        selected = false,
        onClick = { },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier
                    .size(InputChipDefaults.AvatarSize)
                    .clickable { onRemove() },
                tint = Color.Black
            )
        },
        colors = InputChipDefaults.inputChipColors(
            containerColor = MaterialTheme.colorScheme.primary,
            labelColor = Color.Black
        ),
        shape = RoundedCornerShape(24.dp),
        border = null,
        modifier = Modifier.padding(4.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    uvm: UserViewModel,
    onBack: () -> Unit,
    onProfilePictureClick: () -> Unit,
    onAddIngredientClick: (List<String>, List<String>) -> Unit,
    onAddCuisineClick: (List<String>, List<String>) -> Unit,
    onAddRestrictionClick: (List<String>, List<String>) -> Unit
) {
    val uiState by uvm.uiState.collectAsState()
    val userId by SessionManagerFacade.currentUserStateFlow.collectAsState()


    LaunchedEffect(userId) {
        if (userId != null) {
            uvm.loadUser(userId!!, isOwner = true)
        }
    }

    LaunchedEffect(uiState.user.id,userId) {
        if (userId != null && uiState.user.id == userId && !uiState.isEditing) {
            uvm.edit()
        }
    }


    BackHandler(enabled = uiState.isEditing) {
        uvm.validateAndSave()
    }


    LaunchedEffect(uiState.isEditing, uiState.user.name, userId) {
        if (!uiState.isEditing && uiState.user.id == userId) {
            onBack()
        }
    }
    if (uiState.isLoading) {
        LoadingScreen()
    }
    else {

        Column(
            Modifier.fillMaxSize()
        ) {
            CenterAlignedTopAppBar(
                title = { Text("Create Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    IconButton(onClick = { uvm.validateAndSave() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and go back"
                        )
                    }
                },
                actions = {
                    Button(onClick = uvm::validateAndSave) {
                        Text("Done")
                    }
                }
            )
            if (uiState.isEditing) {
                EditPane(
                    user = uiState.draftUser,
                    validation = uiState.validation,
                    ingredientOptions = uiState.ingredientOptions,
                    cuisineOptions = uiState.cuisineOptions,
                    restrictionOptions = uiState.restrictionOptions,
                    onProfilePictureClick = onProfilePictureClick,
                    onNameChange = uvm::setName,
                    onSurnameChange = uvm::setSurname,
                    onUserNameChange = uvm::setUserName,
                    onCookingRoleChange = uvm::setCookingRole,
                    onAddIngredient = uvm::addIngredient,
                    onRemoveIngredient = uvm::removeIngredient,
                    onAddCuisine = uvm::addCuisine,
                    onRemoveCuisine = uvm::removeCuisine,
                    onAddRestriction = uvm::addRestriction,
                    onRemoveRestriction = uvm::removeRestriction,
                    onAddIngredientClick = onAddIngredientClick,
                    onAddCuisineClick = onAddCuisineClick,
                    onAddRestrictionClick = onAddRestrictionClick
                )
            }
        }
    }
}
