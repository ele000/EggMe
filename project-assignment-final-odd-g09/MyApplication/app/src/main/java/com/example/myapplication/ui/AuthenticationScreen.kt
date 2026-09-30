package com.example.myapplication.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.myapplication.data.auth.SessionManagerFacade

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel,
    onSuccess: () -> Unit,
    onNewUser: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    if (isLoading) {
        LoadingScreen()
    }
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 48.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = com.example.myapplication.R.drawable.gallina),
                            contentDescription = "Logo Hen",
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "LOGIN/REGISTRATION",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("We are ")
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("EGG")
                                }
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("cited")
                                }
                                append(" to see you")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val result = SessionManagerFacade.signIn(context)
                                if (result.isSuccess) {
                                    viewModel.registerUser(
                                        result = result,
                                        onNewUser = onNewUser,
                                        onSuccess = onSuccess
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    ) {
                        Text(
                            text = "Login/Register with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Login/Register to be part of our coop",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

        }
    }
}