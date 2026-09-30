package com.example.myapplication.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.data.Farmer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPoint
import kotlinx.coroutines.launch
@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FindYourFarmersScreen(onBack: () -> Unit,viewModel: FindYourFarmersViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val torino = LatLng(45.0703, 7.6868)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(torino, 9f)
    }

    val farmersList by viewModel.farmersList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val showResults by viewModel.showResults.collectAsState()
    val filteredFarmers by viewModel.filteredFarmers.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Find Your Farmers") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            windowInsets = WindowInsets(0),
            navigationIcon = {
                IconButton(
                    onClick = {
                        onBack()
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search farms or locations") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

        }


        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                MapEffect(Unit) { map ->
                    try {
                        val layer = KmlLayer(map, R.raw.find_your_farmers, context)
                        layer.addLayerToMap()
                        if (farmersList.isEmpty()) {
                            val searchList = mutableListOf<Farmer>()

                            fun extractPins(containers: Iterable<KmlContainer>) {
                                for (container in containers) {
                                    for (placemark in container.placemarks) {
                                       if (placemark.geometry != null && placemark.geometry.geometryType == "Point") {
                                            val point = placemark.geometry as KmlPoint
                                            val name = placemark.getProperty("name") ?: "Farmer"
                                            searchList.add(Farmer(name, point.geometryObject))
                                        }
                                    }
                                    if (container.hasContainers()) {
                                        extractPins(container.containers)
                                    }
                                }
                            }
                            extractPins(layer.containers)

                            viewModel.setFarmers(searchList)                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            if (showResults && filteredFarmers.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    LazyColumn {
                        items(filteredFarmers) { farmer ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onSearchQueryChange(farmer.name)
                                        viewModel.hideResults()
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                update = CameraUpdateFactory.newLatLngZoom(farmer.location, 16f),
                                                durationMs = 1500
                                            )
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = farmer.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            HorizontalDivider(
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}