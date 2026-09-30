package com.example.myapplication.ui

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.net.Uri
import com.example.myapplication.R
import kotlin.Unit

@Composable
fun YourImpactPane(
    onFindYourFarmersClick: () -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Top
    ) {
        item {
            MapBox(onFindYourFarmersClick = onFindYourFarmersClick)
        }

        item{
            ExternalLinkCard(
                title = "Fight with us against animal cruelty",
                description = "CIWF is a global NGO campaigning for better farm animal welfare and an end to industrial factory farming.",
                buttonText = "Visit CIWF website",
                url= "https://www.ciwf.it/"
            )
        }

        item{
            ExternalLinkCard(
                title = "Adopt a hen",
                description = "Become a 'feathered friend' sponsor! Remote-adopt a hen today and make a real difference for farm animal welfare.",
                buttonText = "Adopt now",
                url= "https://fattoriailrosmarino.it/adozioni/adotta-un-animale/adotta-una-gallina/"
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggMeScreen(
    onFindYourFarmersClick: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Your Impact") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),
            actions = {
                //
            }
        )

        YourImpactPane(onFindYourFarmersClick = onFindYourFarmersClick)

    }
}

@Composable
fun MapBox(onFindYourFarmersClick: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ),
    ){
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = painterResource(id = R.drawable.hen),
                contentDescription = "hen",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Find your farmers",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Locate the nearest organic farms to buy fresh eggs from happy hens.",
                //fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onFindYourFarmersClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = "map",
                        //tint = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = "Open Map",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

        }
    }
}

@Composable
fun ExternalLinkCard(
    title: String,
    description: String,
    buttonText: String,
    url: String
){

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ),
    ){
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Outlined.OpenInBrowser,
                contentDescription = "link",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End).size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                //fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium
                    )

            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}