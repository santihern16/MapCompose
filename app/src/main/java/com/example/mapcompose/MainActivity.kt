package com.example.mapcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mapcompose.ui.theme.MapComposeTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapComposeTheme {
                Surface {
                    MyGoogleMaps()
                }
            }
        }
    }
}

@Composable
fun MyGoogleMaps() {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val properties = remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = false,
                mapType = MapType.HYBRID
            )
        )
    }

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    val hasPermission = ContextCompat.checkSelfPermission(
        context,
        locationPermission
    ) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            properties.value = properties.value.copy(isMyLocationEnabled = true)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    LaunchedEffect(true) {
        if (hasPermission) {
            properties.value = properties.value.copy(isMyLocationEnabled = true)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        } else {
            permissionLauncher.launch(locationPermission)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = properties.value,
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        ),
        onMapLoaded = {
            // Aquí puedes hacer algo cuando se carga el mapa
        }
    ) {
        // Muestra un marcador en la ubicación del usuario
        userLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Aquí estás tú",
                snippet = "Puedes reportar un daño aquí"
            )
        }
    }

    // Puedes guardar `userLocation` al presionar un botón de "Reportar"
    // y mandarlo a una base de datos o pantalla de confirmación
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapComposeTheme {
        Greeting("Android")
    }
}