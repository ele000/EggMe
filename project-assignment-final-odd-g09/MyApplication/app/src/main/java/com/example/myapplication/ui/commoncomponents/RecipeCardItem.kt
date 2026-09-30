package com.example.myapplication.ui.commoncomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil3.compose.AsyncImage
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.Recipe
import com.example.myapplication.data.RecipeForCard
import com.example.myapplication.domain.RecipeWithSaved
import com.example.myapplication.ui.theme.MyRed


@Composable
fun RecipeCardItem(
    recipeWithSaved: RecipeWithSaved,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    onToggleFavorite: (Recipe) -> Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(recipeWithSaved.recipe.id) },
        shape = RoundedCornerShape(12.dp * scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = recipeWithSaved.recipe.recipePicture,
                    contentDescription = "Image of ${recipeWithSaved.recipe.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp * scale),
                    contentScale = ContentScale.Crop,
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp * scale)
                        .size(30.dp * scale),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    IconButton (
                        onClick = { onToggleFavorite(recipeWithSaved.recipe) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if(recipeWithSaved.isSaved){
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.padding(6.dp * scale),
                                tint = MyRed
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.padding(6.dp * scale),
                                tint = MyRed
                            )
                        }

                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp * scale)) {
                Text(
                    text = recipeWithSaved.recipe.title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp * scale),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🕒 ", fontSize = (12.sp.value * scale).sp)
                        Text(
                            text = "${recipeWithSaved.recipe.time} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${recipeWithSaved.recipe.rating} ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.egg_fried_heart),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp * scale)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCardItemWithoutButton(
    recipe: RecipeForCard,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id) },
        shape = RoundedCornerShape(12.dp * scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {

            AsyncImage(
                model = recipe.recipePicture,
                contentDescription = "Image of ${recipe.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp * scale),
                contentScale = ContentScale.Crop,
            )




            Column(modifier = Modifier.padding(10.dp * scale)) {
                Text(
                    text = recipe.title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp * scale),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🕒 ", fontSize = (12.sp.value * scale).sp)
                        Text(
                            text = "${recipe.time} min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${recipe.rating} ",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.egg_fried_heart),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp * scale)
                        )
                    }
                }
            }
        }
    }
}



