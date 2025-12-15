package com.example.projektbptb.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.projektbptb.screen.*
import com.example.projektbptb.viewmodel.ProfileViewModel

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
    object ProductDetail : Screen("product_detail")
    object Address : Screen("address")
    object Language : Screen("language")
    object ChangePassword : Screen("change_password")
    object HelpCenter : Screen("help_center")
    object EditProduct : Screen("edit_product")
    object Wishlist : Screen("wishlist")
    object AddressDetail : Screen("address_detail")
    object Search : Screen("search")
    object SearchResults : Screen("search_results")
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
                    // Setelah register berhasil, navigasi ke Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
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
                onNavigateToProductDetail = {
                    navController.navigate(Screen.ProductDetail.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToWishlist = {
                    navController.navigate(Screen.Wishlist.route) {
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
                    // Store product temporarily in ViewModel for navigation
                    profileViewModel.updateProduct(product, product) // Temporary workaround
                    navController.navigate("${Screen.EditProduct.route}/${product.name}")
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

        composable(
            route = "${Screen.EditProduct.route}/{productName}",
            arguments = listOf(navArgument("productName") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileViewModel: ProfileViewModel = viewModel(
                key = "profile",
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            
            // Use remember to track the product and update when myProducts changes
            val product = remember(profileViewModel.myProducts.size, productName) {
                profileViewModel.myProducts.find { it.name == productName }
                    ?: com.example.projektbptb.data.model.Product(
                        name = productName,
                        category = "",
                        price = "Rp 0",
                        imageRes = com.example.projektbptb.R.drawable.logo
                    )
            }
            
            // Reload products to ensure fresh data - this will trigger recomposition
            LaunchedEffect(Unit) {
                profileViewModel.loadMyProducts()
            }
            
            // Debug log to check product data
            LaunchedEffect(product.id, product.description) {
                android.util.Log.d("EditProduct", "Product data: id=${product.id}, name=${product.name}, " +
                        "category=${product.category}, condition=${product.condition}, " +
                        "description=${product.description}, price=${product.price}, " +
                        "whatsapp=${product.whatsappNumber}")
            }
            
            // Only show EditProductScreen when we have valid product data
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
                // Show loading while data is being fetched
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
        
        composable(Screen.ProductDetail.route) {
            ProductDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFavoriteClick = {
                    // TODO: Handle favorite
                },
                onWhatsAppClick = {
                    // TODO: Handle WhatsApp contact
                }
            )
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
                    navController.navigate(Screen.ProductDetail.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Search.route) {
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                    LocalContext.current.applicationContext as android.app.Application
                )
            )
            SearchScreen(
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
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(
                searchQuery = query,
                products = homeViewModel.products,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { product ->
                    navController.navigate(Screen.ProductDetail.route) {
                        launchSingleTop = true
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
                }
            )
        }
    }
}

