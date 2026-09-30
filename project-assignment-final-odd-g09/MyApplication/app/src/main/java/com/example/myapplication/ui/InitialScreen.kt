package com.example.myapplication.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun InitialScreen() {
  Box(
      modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight()
          .background(MaterialTheme.colorScheme.primary),
      contentAlignment = Alignment.Center
  ) {
          Image(
              painter = painterResource(id = R.drawable.gallina),
              contentDescription = "home hen",
              modifier = Modifier.size(200.dp)
          )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewInitialScreen() {
    MyApplicationTheme {
        InitialScreen()
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.uovo),
                    contentDescription = "loading egg",
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = "Cracking something delicious...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingScreen() {
    MyApplicationTheme {
        LoadingScreen()
    }
}
