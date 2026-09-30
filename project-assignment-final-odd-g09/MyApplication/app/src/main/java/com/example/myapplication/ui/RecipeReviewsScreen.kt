package com.example.myapplication.ui



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import com.example.myapplication.R
import com.example.myapplication.data.Review
import com.example.myapplication.data.User
import com.example.myapplication.data.toAuthor
import com.example.myapplication.ui.commoncomponents.ProfilePicture


@Composable
fun CreationPaneReviews(
    user: User,
    uiState: ReviewUiState,
    onDescriptionChanged: (String) -> Unit,
    onRatingChanged: (Int) -> Unit,
    onPhotoChanged: (String) -> Unit,
    onAddClicked: () -> Unit,
    onReviewPictureClick: () -> Unit
){

    LazyColumn(Modifier
                .fillMaxSize()
                .padding(0.dp),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.Top
    ) {

        item{
            UserInfo(user)
        }

        item {
            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "YOUR EGG-RATING",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            EggRating(
                rating = uiState.inputRating,
                onRatingChanged = onRatingChanged
            )
            if (uiState.validation.ratingError.isNotBlank()) {
                Text(
                    text = uiState.validation.ratingError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item{
            ReviewDescription(
                description = uiState.inputDescription,
                onDescriptionChanged = onDescriptionChanged
            )
            if (uiState.validation.descriptionError.isNotBlank()) {
                Text(
                    text = uiState.validation.descriptionError,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item{
            ReviewPhoto(
                photo = uiState.inputPhoto,
                onReviewPictureClick=onReviewPictureClick)
        }

        item {
            FilledTonalButton(
                onClick = onAddClicked,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "SUBMIT REVIEW",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(0.dp, 8.dp)
                )
            }
        }

    }

}

@Composable
fun PresentationPaneReviews(
    reviews: List<Review>,
    rating: Double
){
    LazyColumn(Modifier
        .fillMaxSize()
        .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        item {
            AverageEggRating(reviews,rating)
        }

        item{
            RatingLinearIndicator(reviews)
        }

        item{
            reviews.forEach { review ->
                ReviewComponent(review)
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewCreationScreen(
    idRecipe: String,
    onBack: () -> Unit,
    viewModel: RecipeViewModel,
    onReviewPictureClick: () -> Unit,
) {

    LaunchedEffect(idRecipe) {
        viewModel.loadRecipe(idRecipe)
    }

    val recipe by viewModel.recipe.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val uiState by viewModel.newReview.collectAsState()
    val reviewCreated by viewModel.reviewCreated.collectAsState()

    LaunchedEffect(reviewCreated) {
        if(reviewCreated) {
            viewModel.resetReviewCreated()
            onBack()
        }
    }

    if (uiState.isLoading) {
        LoadingScreen()
    }
    else {
        Column(Modifier.fillMaxSize()) {
            CenterAlignedTopAppBar(
                title = { Text("New Review") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                            viewModel.resetReview()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    //
                }
            )

            CreationPaneReviews(
                user as User,
                uiState = uiState,
                onDescriptionChanged = { viewModel.updateReviewDescription(it) },
                onRatingChanged = { viewModel.updateReviewRating(it) },
                onPhotoChanged = { viewModel.updateReviewPhoto(it) },
                onAddClicked = { viewModel.addReview() },
                onReviewPictureClick = onReviewPictureClick
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewPresentationScreen(
    idRecipe: String,
    onBack: () -> Unit,
    viewModel: RecipeViewModel
) {

    LaunchedEffect(idRecipe) {
        viewModel.loadRecipe(idRecipe)
    }

    val recipe by viewModel.recipe.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Reviews") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),
            navigationIcon = {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            },
            actions = {
                //
            }
        )

            PresentationPaneReviews(
                reviews = recipe?.reviews ?: emptyList(),
                rating = recipe?.rating ?: 0.0
            )
    }
}

//components used to create a new review

@Composable
fun UserInfo(
    user: User
){

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {

            ProfilePicture(user.toAuthor(),130.dp)

            Spacer(modifier = Modifier.width(30.dp))

            Text(
                text = user.name+" "+user.surname,
                style = MaterialTheme.typography.headlineMedium,
                //fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }

}

@Composable
fun ReviewDescription(
    description: String,
    onDescriptionChanged: (String) -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            modifier = Modifier.fillMaxWidth().height(100.dp),
            placeholder = { Text("Write your review...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun ReviewPhoto(
    photo: String,
    onReviewPictureClick: () -> Unit
){

    Column() {

        Box(
            modifier = Modifier.fillMaxWidth(0.9f),
            contentAlignment = Alignment.Center
        ) {

            OutlinedButton(
                onClick = onReviewPictureClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {

                if (photo.isNotBlank()) {
                    AsyncImage(
                        model = photo,
                        contentDescription = "Review image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )


                } else {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "camera",
                                tint = MaterialTheme.colorScheme.tertiaryContainer,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Text(
                            text = "Insert Image",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Upload a high quality photo of your dish",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

}

@Composable
fun EggRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit
){

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            for(i in 1..5){

                IconButton(
                    onClick = {onRatingChanged(i)},
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.egg_fried_heart),
                        contentDescription = null,
                        tint = if(i<=rating) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

}


//components used to visualize all the reviews of a recipe

@Composable
fun AverageEggRating(
    reviews: List<Review>,
    rating: Double
){

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.Start,
    ){

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = rating.toString(),
                //modifier = Modifier.size(70.dp),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.egg_fried_heart),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(70.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = reviews.size.toString()+" reviews",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun ReviewComponent(
    review: Review
){

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {

            ProfilePicture(review.author,50.dp)

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text (
                    text = review.author.name+" "+review.author.surname,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatRelativeTime(review.reviewDate),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            for(i in 1..5){
                Icon(
                    painter = painterResource(id = R.drawable.egg_fried_heart),
                    contentDescription = null,
                    tint = if(i<=review.rating) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),

                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = review.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (review.photo.isNotBlank()) {
            AsyncImage(
                model = review.photo,
                contentDescription = "Review image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))
    }

}

private fun formatRelativeTime(timestamp: Timestamp?): String {
    val time = timestamp ?: return ""
    val diffMillis = System.currentTimeMillis() - time.toDate().time
    if (diffMillis < 60_000L) return "a few seconds ago"

    val minutes = diffMillis / 60_000L
    if (minutes < 60L) return if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"

    val hours = minutes / 60L
    if (hours < 24L) return if (hours == 1L) "1 hour ago" else "$hours hours ago"

    val days = hours / 24L
    return if (days == 1L) "1 day ago" else "$days days ago"
}

@Composable
fun RatingLinearIndicator(
    reviews: List<Review>
) {

    Column(
        modifier = Modifier.fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        for(i in 1..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    text = i.toString(),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(8.dp))

                LinearProgressIndicator(
                    progress = {
                        if (reviews.isNotEmpty()) (reviews.count { review -> review.rating == i }.toFloat()) / (reviews.size.toFloat())
                        else 0f
                    },
                    modifier = Modifier.fillMaxWidth(0.8f).height(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondary,
                    gapSize = 0.dp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (reviews.isNotEmpty())
                                ((((reviews.count{ review -> review.rating == i }) / (reviews.size.toFloat()))*100).toInt()).toString()+" %"
                           else "0 %",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

