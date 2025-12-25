package com.example.projektbptb.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.projektbptb.screen.*
import com.example.projektbptb.viewmodel.ProfileViewModel
import com.example.projektbptb.viewmodel.SearchViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    object AddProfile : Screen("add_profile")
    object AddProduct : Screen("add_product")
    data class ProductDetail(val productId: Int) : Screen("product_detail/{productId}") {
        companion object {
            fun createRoute(productId: Int) = "product_detail/$productId"
        }
    }
    object Address : Screen("address")
    object Language : Screen("language")
    object ChangePassword : Screen("change_password")
    object HelpCenter : Screen("help_center")
    object EditProduct : Screen("edit_product")
    object Wishlist : Screen("wishlist")
    object AddressDetail : Screen("address_detail")
    object Search : Screen("search")
    object SearchResults : Screen("search_results")
    object Notification : Screen("notification")
    data class UserProfile(val userId: Int) : Screen("user_profile/{userId}") {
        companion object {
            fun createRoute(userId: Int) = "user_profile/$userId"
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLanding = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.Landing.route) {
            LandingScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            val loginViewModel: com.example.projektbptb.viewmodel.LoginViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginClick = {
                    // Setelah login berhasil, navigasi ke Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    // TODO: Implement forgot password navigation
                }
            )
        }
        
        composable(Screen.Register.route) {
            val loginViewModel: com.example.projektbptb.viewmodel.LoginViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            RegisterScreen(
                viewModel = loginViewModel,
                onRegisterClick = {
                    // Setelah register berhasil, navigasi ke Login agar user login manual
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.Home.route) { backStackEntry ->
            // Use key to ensure same instance is used across screens
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel(
                key = "HomeViewModel",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            
            // Reload products ketika kembali ke HomeScreen
            // Menggunakan backStackEntry sebagai key untuk LaunchedEffect
            androidx.compose.runtime.LaunchedEffect(backStackEntry) {
                // Refresh products agar produk baru langsung muncul
                homeViewModel.loadProducts()
            }
            
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSettings = {
                    navController.navigate(Screen.Profile.route) {
                        // Prevent multiple instances of the same destination
                        launchSingleTop = true
                    }
                },
                onNavigateToHome = {
                    // Already on home, do nothing
                },
                onNavigateToSell = {
                    navController.navigate(Screen.AddProduct.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToWishlist = {
                    navController.navigate(Screen.Wishlist.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToNotification = {
                    navController.navigate(Screen.Notification.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        // Pop back to home
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToSell = {
                    navController.navigate(Screen.AddProduct.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.Address.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLanguage = {
                    navController.navigate(Screen.Language.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHelpCenter = {
                    navController.navigate(Screen.HelpCenter.route) {
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Navigate to Landing screen and clear back stack
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(0) { inclusive = true } // Clear entire back stack
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                key = "profile",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToSell = {
                    navController.navigate(Screen.AddProduct.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAddProfile = {
                    navController.navigate(Screen.AddProfile.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.Address.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLanguage = {
                    navController.navigate(Screen.Language.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToChangePassword = {
                    navController.navigate(Screen.ChangePassword.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToHelpCenter = {
                    navController.navigate(Screen.HelpCenter.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToEditProduct = { product ->
                    // Navigate using product ID instead of name
                    navController.navigate("${Screen.EditProduct.route}/${product.id}")
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.AddProfile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                key = "profile",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            AddProfileScreen(
                viewModel = profileViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { username, phone, email, gender ->
                    // Profile data will be saved in ViewModel
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Address.route) {
            val addressViewModel: com.example.projektbptb.viewmodel.AddressViewModel = viewModel()
            AddressScreen(
                viewModel = addressViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onAddressClick = { address ->
                    navController.navigate("${Screen.AddressDetail.route}/${address.id}") {
                        launchSingleTop = true
                    }
                },
                onAddAddressClick = {
                    navController.navigate("${Screen.AddressDetail.route}/new") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${Screen.AddressDetail.route}/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressViewModel: com.example.projektbptb.viewmodel.AddressViewModel = viewModel()
            val addressId = backStackEntry.arguments?.getString("addressId") ?: ""
            val address = if (addressId == "new") {
                null
            } else {
                addressViewModel.savedAddresses.find { it.id == addressId }
            }
            
            AddressDetailScreen(
                address = address,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveClick = { newAddress ->
                    if (address == null) {
                        addressViewModel.addAddress(newAddress)
                    } else {
                        addressViewModel.updateAddress(address, newAddress)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Language.route) {
            LanguageScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Notification.route) {
            NotificationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${Screen.EditProduct.route}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileViewModel: ProfileViewModel = viewModel(
                key = "profile",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            
            // Load products ONCE when screen opens to get fresh data
            LaunchedEffect(Unit) {
                profileViewModel.loadMyProducts()
            }
            
            // Find product by ID (not name, because name can change!)
            val product = remember(productId, profileViewModel.myProducts.toList()) {
                profileViewModel.myProducts.find { it.id == productId }
                    ?: com.example.projektbptb.data.model.Product(
                        id = "",
                        name = "",
                        category = "",
                        price = "Rp 0",
                        imageRes = com.example.projektbptb.R.drawable.logo
                    )
            }
            

            
            // Show EditProductScreen or navigate back if not found
            if (product.id.isNotEmpty()) {
                EditProductScreen(
                    product = product,
                    viewModel = profileViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onEditClick = { updatedProduct, imageFile ->
                        profileViewModel.updateProduct(product, updatedProduct, imageFile)
                    },
                    onDeleteClick = {
                        profileViewModel.deleteProduct(product)
                        navController.popBackStack()
                    }
                )
            } else {
                // Product not found - either loading or deleted
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileViewModel.myProducts.isEmpty()) {
                        // Still loading
                        CircularProgressIndicator()
                    } else {
                        // Product not found after loading - go back
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }

        composable(Screen.AddProduct.route) {
            val addProductViewModel: com.example.projektbptb.viewmodel.AddProductViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            AddProductScreen(
                viewModel = addProductViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val context = LocalContext.current
            val sharedPreferences = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)
            
            val productDetailViewModel: com.example.projektbptb.viewmodel.ProductDetailViewModel = viewModel(
                key = "product_detail_$productId",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    context.applicationContext as android.app.Application
                )
            )
            
            // Load product detail when screen is opened
            LaunchedEffect(productId) {
                productDetailViewModel.loadProductDetail(productId, token)
            }
            
            val productDetail by productDetailViewModel.productDetail.collectAsState()
            val isLoading by productDetailViewModel.isLoading.collectAsState()
            val isFavorite by productDetailViewModel.isFavorite.collectAsState()
            val showAlert by productDetailViewModel.showAlert.collectAsState()
            val alertMessage by productDetailViewModel.alertMessage.collectAsState()
            
            // Alert Dialog untuk menampilkan pesan error
            if (showAlert && alertMessage != null) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = {
                        productDetailViewModel.showAlert.value = false
                        productDetailViewModel.alertMessage.value = null
                    },
                    title = {
                        androidx.compose.material3.Text(
                            text = "Peringatan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        androidx.compose.material3.Text(
                            text = alertMessage ?: "",
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        androidx.compose.material3.TextButton(
                            onClick = {
                                productDetailViewModel.showAlert.value = false
                                productDetailViewModel.alertMessage.value = null
                            }
                        ) {
                            androidx.compose.material3.Text(
                                "OK", 
                                color = com.example.projektbptb.ui.theme.BluePrimary
                            )
                        }
                    }
                )
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (productDetail != null) {
                ProductDetailScreen(
                    product = productDetail!!,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFavoriteClick = {
                        if (token != null) {
                            productDetailViewModel.toggleFavorite(productId, token)
                        }
                    },
                    onNotificationClick = {
                        navController.navigate(Screen.Notification.route) {
                            launchSingleTop = true
                        }
                    },
                    onWhatsAppClick = {
                        // Open WhatsApp with product's WhatsApp number
                        productDetail?.whatsappNumber?.let { phoneNumber ->
                            // Remove non-digit characters from phone number
                            val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
                            if (cleanNumber.isNotEmpty()) {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        data = android.net.Uri.parse("https://wa.me/$cleanNumber")
                                        setPackage("com.whatsapp")
                                    }
                                    context.startActivity(intent)
                                } catch (e: android.content.ActivityNotFoundException) {
                                    // If WhatsApp is not installed, try with browser
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                        data = android.net.Uri.parse("https://wa.me/$cleanNumber")
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        }
                    },
                    onSellerClick = { userId ->
                        if (userId > 0) {
                            navController.navigate(Screen.UserProfile.createRoute(userId)) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Product not found")
                }
            }
        }

        composable(Screen.Wishlist.route) {
            // Use the same ViewModel instance as HomeScreen by using the same key
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel(
                key = "HomeViewModel",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            WishlistScreen(
                viewModel = homeViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onViewProductClick = { product ->
                    product.id.toIntOrNull()?.let { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            SearchScreen(
                viewModel = searchViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSearchQuery = { query ->
                    navController.navigate("${Screen.SearchResults.route}/$query") {
                        launchSingleTop = true
                    }
                },
                onCategoryClick = { category ->
                    navController.navigate("${Screen.SearchResults.route}/$category") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${Screen.SearchResults.route}/{query}",
            arguments = listOf(navArgument("query") { type = NavType.StringType })
        ) { backStackEntry ->
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            val searchViewModel: SearchViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(
                searchQuery = query,
                products = homeViewModel.searchResults,
                isLoading = homeViewModel.isSearching.value,
                isProfileComplete = homeViewModel.isProfileComplete(),
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { product ->
                    product.id.toIntOrNull()?.let { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                            launchSingleTop = true
                        }
                    }
                },
                onFavoriteClick = { product ->
                    homeViewModel.toggleFavorite(product)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToSell = {
                    navController.navigate(Screen.AddProduct.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                },
                onLoadProducts = { searchQuery ->
                    homeViewModel.searchProducts(searchQuery)
                },
                onSearchQueryChange = { newQuery ->
                    navController.navigate("${Screen.SearchResults.route}/$newQuery") {
                        launchSingleTop = true
                    }
                },
                onSaveSearchQuery = { query ->
                    searchViewModel.saveSearchQuery(query)
                }
            )
        }
        
        composable(
            route = "user_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userIdArg = backStackEntry.arguments?.getInt("userId")
            val userId = userIdArg ?: 0
            val context = LocalContext.current

            if (userId > 0) {
                val userProfileViewModel: com.example.projektbptb.viewmodel.UserProfileViewModel = viewModel(
                    key = "user_profile_$userId",
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                        context.applicationContext as android.app.Application
                    )
                )
                
                LaunchedEffect(backStackEntry) {
                    if (userId > 0) {
                        userProfileViewModel.loadUserProfile(userId)
                    }
                }

                UserProfileScreen(
                    userId = userId,
                    viewModel = userProfileViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToProductDetail = { productId ->
                        if (productId > 0) {
                            navController.navigate(Screen.ProductDetail.createRoute(productId)) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "User ID tidak valid",
                            color = com.example.projektbptb.ui.theme.RedPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Kembali")
                        }
                    }
                }
            }
        }
    }
}

