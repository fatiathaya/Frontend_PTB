package com.example.projektbptb.navigation

import androidx.compose.runtime.Composable
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
            LoginScreen(
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
            RegisterScreen(
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
        
        composable(Screen.Home.route) {
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel()
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
                }
            )
        }
        
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(key = "profile")
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
            val profileViewModel: ProfileViewModel = viewModel(key = "profile")
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
            val profileViewModel: ProfileViewModel = viewModel(key = "profile")
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            val product = profileViewModel.myProducts.find { it.name == productName }
                ?: com.example.projektbptb.model.Product("", "", "", com.example.projektbptb.R.drawable.logo)
            
            EditProductScreen(
                product = product,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditClick = { updatedProduct ->
                    profileViewModel.updateProduct(product, updatedProduct)
                    navController.popBackStack()
                },
                onDeleteClick = {
                    profileViewModel.deleteProduct(product)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddProduct.route) {
            AddProductScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitClick = {
                    // TODO: Handle submit product
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
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel()
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
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel()
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
            val homeViewModel: com.example.projektbptb.viewmodel.HomeViewModel = viewModel()
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

