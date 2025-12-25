package com.example.projektbptb.ui.components

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.projektbptb.ui.theme.BluePrimary
import com.example.projektbptb.ui.theme.GrayDark
import com.example.projektbptb.ui.theme.GrayLight
import com.example.projektbptb.ui.theme.White
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.io.IOException
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressInput(
    address: String,
    onAddressChange: (String, Double?, Double?) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pilih atau ketik alamat"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var addressText by remember { mutableStateOf(address) }
    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var currentMarker by remember { mutableStateOf<Marker?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    
    // Places Autocomplete
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var placesClient by remember { mutableStateOf<PlacesClient?>(null) }
    
    // Address detail card state
    var showAddressDetailCard by remember { mutableStateOf(false) }
    var selectedPrediction by remember { mutableStateOf<AutocompletePrediction?>(null) }
    
    // Initialize Places SDK
    LaunchedEffect(Unit) {
        try {
            if (!Places.isInitialized()) {
                // Get API key from manifest meta-data
                val apiKey = context.packageManager
                    .getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
                    .metaData?.getString("com.google.android.geo.API_KEY") ?: ""
                
                if (apiKey.isNotEmpty() && apiKey != "YOUR_GOOGLE_MAPS_API_KEY") {
                    Places.initialize(context, apiKey)
                    placesClient = Places.createClient(context)
                } else {
                    android.util.Log.w("AddressInput", "Google Maps API Key not configured")
                }
            } else {
                placesClient = Places.createClient(context)
            }
        } catch (e: Exception) {
            android.util.Log.e("AddressInput", "Error initializing Places: ${e.message}")
        }
    }
    
    // Default location (Universitas Andalas, Padang, Sumatera Barat, Indonesia)
    // Koordinat: -0.9134, 100.3609 (Kampus Unand Limau Manis)
    val defaultLocation = GeoPoint(-0.9134, 100.3609)
    
    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }
    
    // Update addressText when address prop changes
    LaunchedEffect(address) {
        addressText = address
    }
    
    // Fetch autocomplete predictions - More responsive
    LaunchedEffect(addressText) {
        // Clear previous predictions when text changes
        if (addressText.length < 2) {
            predictions = emptyList()
            showSuggestions = false
            return@LaunchedEffect
        }
        
        if (addressText.length >= 2 && placesClient != null) {
            val token = AutocompleteSessionToken.newInstance()
            
            // Build request with location bias to Indonesia
            // Prioritize Indonesia dengan setCountries
            val requestBuilder = FindAutocompletePredictionsRequest.builder()
                .setQuery(addressText)
                .setSessionToken(token)
                .setCountries("ID") // Prioritize Indonesia
            
            val request = requestBuilder.build()
            
            placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
                predictions = response.autocompletePredictions
                showSuggestions = predictions.isNotEmpty() && addressText.length >= 2
                
                // Jika user mengetik "universitas andalas" dan ada prediction yang relevan, langsung fokus ke Unand
                val queryLower = addressText.lowercase()
                if ((queryLower.contains("universitas andalas") || queryLower.contains("unand")) && predictions.isNotEmpty()) {
                    // Cari prediction yang paling relevan dengan Unand
                    val unandPrediction = predictions.firstOrNull { pred ->
                        val primaryText = pred.getPrimaryText(null).toString().lowercase()
                        val secondaryText = pred.getSecondaryText(null)?.toString()?.lowercase() ?: ""
                        primaryText.contains("universitas andalas") || 
                        primaryText.contains("unand") ||
                        secondaryText.contains("padang") ||
                        secondaryText.contains("sumatera barat")
                    }
                    
                    // Jika ditemukan, langsung fetch dan update map
                    unandPrediction?.let { pred ->
                        val placeFields = listOf(
                            Place.Field.ID,
                            Place.Field.NAME,
                            Place.Field.ADDRESS,
                            Place.Field.LAT_LNG
                        )
                        val fetchRequest = FetchPlaceRequest.newInstance(pred.placeId, placeFields)
                        
                        placesClient?.fetchPlace(fetchRequest)?.addOnSuccessListener { fetchResponse ->
                            val place = fetchResponse.place
                            val fullAddress = place.address ?: place.name ?: ""
                            val latLng = place.latLng
                            
                            if (latLng != null) {
                                addressText = fullAddress
                                currentLatitude = latLng.latitude
                                currentLongitude = latLng.longitude
                                showSuggestions = false
                                
                                onAddressChange(fullAddress, latLng.latitude, latLng.longitude)
                                
                                // Update map immediately
                                if (latLng != null && mapView != null && isMapReady) {
                                    val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                                    mapView?.controller?.animateTo(geoPoint)
                                    mapView?.controller?.setZoom(16.0)
                                    
                                    // Remove old marker
                                    currentMarker?.let { mapView?.overlays?.remove(it) }
                                    
                                    // Add new marker
                                    val marker = Marker(mapView)
                                    marker.position = geoPoint
                                    marker.title = fullAddress
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    mapView?.overlays?.add(marker)
                                    currentMarker = marker
                                    mapView?.invalidate()
                                }
                            }
                        }
                    }
                }
            }?.addOnFailureListener {
                android.util.Log.e("AddressInput", "Error fetching predictions: ${it.message}")
                predictions = emptyList()
                showSuggestions = false
            }
        } else {
            predictions = emptyList()
            showSuggestions = false
        }
    }
    
    // Close suggestions when clicking outside
    LaunchedEffect(Unit) {
        // This will be handled by the Box clickable if needed
    }
    
    // Initialize map when coordinates change from address input
    LaunchedEffect(currentLatitude, currentLongitude) {
        if (currentLatitude != null && currentLongitude != null && mapView != null && isMapReady) {
            val location = GeoPoint(currentLatitude!!, currentLongitude!!)
            mapView?.controller?.animateTo(location)
            mapView?.controller?.setZoom(15.0)
            
            // Remove old marker
            currentMarker?.let { mapView?.overlays?.remove(it) }
            
            // Add new marker
            val marker = Marker(mapView)
            marker.position = location
            marker.title = addressText.ifEmpty { "Lokasi dipilih" }
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView?.overlays?.add(marker)
            currentMarker = marker
            mapView?.invalidate()
        }
    }
    
    Column(modifier = modifier) {
        Text(
            "Alamat",
            color = BluePrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = addressText,
            onValueChange = { newValue ->
                addressText = newValue
            },
            placeholder = { Text(placeholder, color = GrayDark) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = BluePrimary,
                unfocusedIndicatorColor = BluePrimary,
                focusedContainerColor = White,
                unfocusedContainerColor = White
            ),
            shape = MaterialTheme.shapes.medium,
            leadingIcon = {
                    IconButton(
                        onClick = {
                            // Show address detail card when location icon is clicked
                            if (predictions.isNotEmpty()) {
                                showAddressDetailCard = true
                                showSuggestions = false // Hide dropdown suggestions
                            } else if (addressText.isNotBlank() && addressText.length >= 2) {
                                // If no predictions but has text, trigger search first
                                showAddressDetailCard = true
                                // Predictions will be loaded by LaunchedEffect
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                            contentDescription = "Tampilkan detail alamat",
                            tint = BluePrimary,
                            modifier = Modifier.size(24.dp)
                )
                    }
            },
            trailingIcon = {
                if (addressText.isNotEmpty()) {
                    IconButton(onClick = { 
                        addressText = ""
                        currentLatitude = null
                        currentLongitude = null
                            predictions = emptyList()
                            showSuggestions = false
                        onAddressChange("", null, null)
                        // Reset map to default location
                            if (mapView != null && isMapReady) {
                                mapView?.controller?.animateTo(defaultLocation)
                                mapView?.controller?.setZoom(14.0)
                                currentMarker?.let { mapView?.overlays?.remove(it) }
                                currentMarker = null
                                mapView?.invalidate()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = GrayDark
                        )
                    }
                }
            }
        )
        
            // Autocomplete Suggestions Dropdown - Styled like Google Maps
            if (showSuggestions && predictions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .zIndex(1000f), // Ensure dropdown appears above other elements
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(predictions.take(8)) { prediction ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Fetch place details
                                        val placeFields = listOf(
                                            Place.Field.ID,
                                            Place.Field.NAME,
                                            Place.Field.ADDRESS,
                                            Place.Field.LAT_LNG
                                        )
                                        val request = FetchPlaceRequest.newInstance(
                                            prediction.placeId,
                                            placeFields
                                        )
                                        
                                        placesClient?.fetchPlace(request)?.addOnSuccessListener { response ->
                                            val place = response.place
                                            val fullAddress = place.address ?: place.name ?: ""
                                            val latLng = place.latLng
                                            
                                            addressText = fullAddress
                                            currentLatitude = latLng?.latitude
                                            currentLongitude = latLng?.longitude
                                            showSuggestions = false
                                            predictions = emptyList()
                                            
                                            onAddressChange(fullAddress, latLng?.latitude, latLng?.longitude)
                                            
                                            // Update map with better zoom and marker
                                            if (latLng != null && mapView != null && isMapReady) {
                                                val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                                                mapView?.controller?.animateTo(geoPoint)
                                                mapView?.controller?.setZoom(16.0)
                                                
                                                // Remove old marker
                                                currentMarker?.let { mapView?.overlays?.remove(it) }
                                                
                                                // Add new marker
                                                val marker = Marker(mapView)
                                                marker.position = geoPoint
                                                marker.title = place.name ?: fullAddress
                                                marker.snippet = fullAddress
                                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                mapView?.overlays?.add(marker)
                                                currentMarker = marker
                                                mapView?.invalidate()
                                            }
                                        }?.addOnFailureListener {
                                            android.util.Log.e("AddressInput", "Error fetching place: ${it.message}")
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Location icon with better styling
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            BluePrimary.copy(alpha = 0.1f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = prediction.getPrimaryText(null).toString(),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black,
                                        lineHeight = 20.sp
                                    )
                                    if (prediction.getSecondaryText(null) != null) {
                                        Text(
                                            text = prediction.getSecondaryText(null).toString(),
                                            fontSize = 13.sp,
                                            color = GrayDark,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                            
                            // Divider between items
                            if (prediction != predictions.take(8).last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 72.dp), // Align with text content
                                    color = GrayLight.copy(alpha = 0.5f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Address Detail Card - Muncul saat icon lokasi diklik
        if (showAddressDetailCard) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .zIndex(1000f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header dengan tombol close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detail Alamat",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { 
                                showAddressDetailCard = false
                                selectedPrediction = null
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = GrayDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // List alamat dari predictions
                    if (predictions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Ketik alamat untuk mencari",
                                    fontSize = 14.sp,
                                    color = GrayDark
                                )
                                Text(
                                    text = "atau pilih dari suggestions di atas",
                                    fontSize = 12.sp,
                                    color = GrayDark
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(predictions.take(5)) { prediction ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                        // Fetch place details dan pilih alamat
                                        val placeFields = listOf(
                                            Place.Field.ID,
                                            Place.Field.NAME,
                                            Place.Field.ADDRESS,
                                            Place.Field.LAT_LNG
                                        )
                                        val request = FetchPlaceRequest.newInstance(
                                            prediction.placeId,
                                            placeFields
                                        )
                                        
                                        placesClient?.fetchPlace(request)?.addOnSuccessListener { response ->
                                            val place = response.place
                                            val fullAddress = place.address ?: place.name ?: ""
                                            val latLng = place.latLng
                                            
                                            if (latLng != null) {
                                                addressText = fullAddress
                                                currentLatitude = latLng.latitude
                                                currentLongitude = latLng.longitude
                                                showAddressDetailCard = false
                                                showSuggestions = false
                                                predictions = emptyList()
                                                
                                                onAddressChange(fullAddress, latLng.latitude, latLng.longitude)
                                                
                                                // Update map
                                                if (latLng != null && mapView != null && isMapReady) {
                                                    val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
                                                    mapView?.controller?.animateTo(geoPoint)
                                                    mapView?.controller?.setZoom(16.0)
                                                    
                                                    // Remove old marker
                                                    currentMarker?.let { mapView?.overlays?.remove(it) }
                                                    
                                                    // Add new marker
                                                    val marker = Marker(mapView)
                                                    marker.position = geoPoint
                                                    marker.title = place.name ?: fullAddress
                                                    marker.snippet = fullAddress
                                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                                    mapView?.overlays?.add(marker)
                                                    currentMarker = marker
                                                    mapView?.invalidate()
                                                }
                                            }
                                        }?.addOnFailureListener {
                                            android.util.Log.e("AddressInput", "Error fetching place: ${it.message}")
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = BluePrimary.copy(alpha = 0.05f)
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Location icon
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    BluePrimary.copy(alpha = 0.1f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = BluePrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        
                                        // Address details
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = prediction.getPrimaryText(null).toString(),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black,
                                                lineHeight = 20.sp
                                            )
                                            if (prediction.getSecondaryText(null) != null) {
                                                Text(
                                                    text = prediction.getSecondaryText(null).toString(),
                                                    fontSize = 13.sp,
                                                    color = GrayDark,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // OpenStreetMap View (OSMDroid)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(top = 12.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapView = this@apply
                        
                        // Configure OSMDroid
                        setTileSource(TileSourceFactory.MAPNIK) // Use OpenStreetMap tiles
                        setMultiTouchControls(true)
                        minZoomLevel = 3.0
                        maxZoomLevel = 20.0
                        
                        // Set initial location
                            val initialLocation = if (currentLatitude != null && currentLongitude != null) {
                            GeoPoint(currentLatitude!!, currentLongitude!!)
                            } else {
                                defaultLocation
                            }
                        
                        controller.setZoom(15.0)
                        controller.setCenter(initialLocation)
                        
                        // Add compass overlay
                        val compassOverlay = CompassOverlay(ctx, InternalCompassOrientationProvider(ctx), this)
                        compassOverlay.enableCompass()
                        overlays.add(compassOverlay)
                        
                        // Add rotation gesture overlay
                        val rotationGestureOverlay = RotationGestureOverlay(this)
                        rotationGestureOverlay.isEnabled = true
                        overlays.add(rotationGestureOverlay)
                        
                        // Add default marker if no location selected
                        if (currentLatitude == null || currentLongitude == null) {
                            val defaultMarker = Marker(this)
                            defaultMarker.position = defaultLocation
                            defaultMarker.title = "Universitas Andalas, Padang"
                            defaultMarker.snippet = "Kampus Limau Manis, Padang, Sumatera Barat"
                            defaultMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(defaultMarker)
                            currentMarker = defaultMarker
                        }
                        
                        // Handle map click to select location using MapEventsOverlay
                        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p?.let { geoPoint ->
                                    currentLatitude = geoPoint.latitude
                                    currentLongitude = geoPoint.longitude
                                
                                // Reverse geocode to get address
                                    reverseGeocode(context, geoPoint.latitude, geoPoint.longitude) { addr ->
                                    addressText = addr
                                        onAddressChange(addr, geoPoint.latitude, geoPoint.longitude)
                                }
                                
                                // Update marker
                                    currentMarker?.let { overlays.remove(it) }
                                    val marker = Marker(this@apply)
                                    marker.position = geoPoint
                                    marker.title = "Lokasi dipilih"
                                    marker.snippet = addressText.ifEmpty { "Tap untuk memilih lokasi" }
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    overlays.add(marker)
                                    currentMarker = marker
                                    
                                    controller.animateTo(geoPoint)
                                    controller.setZoom(16.0)
                                    invalidate()
                                }
                                return true
                            }
                            
                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                return false
                            }
                        })
                        overlays.add(0, mapEventsOverlay) // Add at index 0 to handle clicks first
                        
                        isMapReady = true
                        android.util.Log.d("AddressInput", "OpenStreetMap initialized successfully")
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    // Update map when coordinates change
                    if (currentLatitude != null && currentLongitude != null && isMapReady) {
                        val location = GeoPoint(currentLatitude!!, currentLongitude!!)
                        view.controller.animateTo(location)
                        view.controller.setZoom(16.0)
                        
                        // Update marker
                        currentMarker?.let { view.overlays.remove(it) }
                        val marker = Marker(view)
                        marker.position = location
                        marker.title = addressText.ifEmpty { "Lokasi dipilih" }
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        view.overlays.add(marker)
                        currentMarker = marker
                        view.invalidate()
                    }
                }
            )
        }
        
        // Show hint
        Text(
            text = "Ketik alamat atau tap di peta untuk memilih lokasi",
            style = MaterialTheme.typography.bodySmall,
            color = GrayDark,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
    
    // Handle lifecycle properly - CRITICAL for MapView to work
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView?.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView?.onPause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun geocodeAddress(
    context: android.content.Context,
    address: String,
    onResult: (Double?, Double?) -> Unit
) {
    if (address.isEmpty()) {
        onResult(null, null)
        return
    }
    
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(address, 1)
        
        if (addresses != null && addresses.isNotEmpty()) {
            val addressResult = addresses[0]
            val latitude = addressResult.latitude
            val longitude = addressResult.longitude
            onResult(latitude, longitude)
        } else {
            onResult(null, null)
        }
    } catch (e: IOException) {
        // Geocoder service not available
        onResult(null, null)
    } catch (e: Exception) {
        // Other errors
        onResult(null, null)
    }
}

fun reverseGeocode(
    context: android.content.Context,
    latitude: Double,
    longitude: Double,
    onResult: (String) -> Unit
) {
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        
        if (addresses != null && addresses.isNotEmpty()) {
            val addressResult = addresses[0]
            val fullAddress = addressResult.getAddressLine(0) ?: ""
            onResult(fullAddress)
        } else {
            onResult("")
        }
    } catch (e: IOException) {
        onResult("")
    } catch (e: Exception) {
        onResult("")
    }
}
