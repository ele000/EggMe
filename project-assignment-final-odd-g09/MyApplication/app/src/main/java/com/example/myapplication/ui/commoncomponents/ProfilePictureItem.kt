package com.example.myapplication.ui.commoncomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.User
/*
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
*/
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.Author


@Composable
fun Monogram(name: String, surname: String, modifier: Modifier = Modifier, size: Dp = 210.dp) {
    val monogram = "${name.take(1)}${surname.take(1)}".uppercase()
    val fontSize = (size.value * 0.4f).sp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(
                BorderStroke(maxOf(1.dp, size / 50), MaterialTheme.colorScheme.primary),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = monogram,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfilePicture(author: Author, size: Dp=210.dp) {
    if (author.profilePicture != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(author.profilePicture)
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    BorderStroke(maxOf(1.dp, size / 50), MaterialTheme.colorScheme.primary),
                    CircleShape
                )
        )

    } else {
        Monogram(name = author.name, surname = author.surname, size = size)
    }
}
