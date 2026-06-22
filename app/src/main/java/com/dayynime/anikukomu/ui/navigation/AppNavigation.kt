package com.dayynime.anikukomu.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dayynime.anikukomu.ui.screens.auth.LoginScreen
import com.dayynime.anikukomu.ui.screens.auth.RegisterScreen
import com.dayynime.anikukomu.ui.screens.create.CreatePostScreen
import com.dayynime.anikukomu.ui.screens.explore.ExploreScreen
import com.dayynime.anikukomu.ui.screens.home.HomeScreen
import com.dayynime.anikukomu.ui.screens.post.PostDetailScreen
import com.dayynime.anikukomu.ui.screens.profile.EditProfileScreen
import com.dayynime.anikukomu.ui.screens.profile.ProfileScreen
import com.dayynime.anikukomu.ui.screens.splash.SplashScreen
import com.dayynime.anikukomu.ui.theme.AnikuAccent
import com.dayynime.anikukomu.ui.theme.AnikuBackground
import com.dayynime.anikukomu.ui.theme.AnikuPink
import com.dayynime.anikukomu.ui.theme.AnikuSurface
import com.dayynime.anikukomu.ui.theme.AnikuSurfaceVar
import com.dayynime.anikukomu.ui.theme.AnikuTextSecondary
import com.dayynime.anikukomu.ui.theme.AnikuTypography
import com.dayynime.anikukomu.ui.theme.frostedGlassBackground

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.fillMaxSize()
    ) {
        // Splash page
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // Login page
        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateAsGuest = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Register page
        composable("register") {
            RegisterScreen(
                onNavigateToHome = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // Main Tab Host wrapper
        composable("main") {
            MainTabHost(
                onNavigateToPostDetail = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onNavigateToUserProfile = { userId ->
                    navController.navigate("profile_user/$userId")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onNavigateToEditProfile = { userId ->
                    navController.navigate("edit_profile/$userId")
                }
            )
        }

        // Post detailed comments thread
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { userId ->
                    navController.navigate("profile_user/$userId")
                },
                onNavigateToExploreWithSearch = { query ->
                    // Navigate to main with special query parameter
                    navController.navigate("main?search=$query") {
                        popUpTo("main") { inclusive = false }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Looking other profile details
        composable(
            route = "profile_user/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(
                profileUserId = userId,
                onNavigateToEditProfile = {
                    navController.navigate("edit_profile/$userId")
                },
                onNavigateToPostDetail = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // Edit profile details forms
        composable(
            route = "edit_profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Suppress("OPT_IN_IS_NOT_CONVERTED_TO_NOT_ANNOTATED")
@Composable
fun MainTabHost(
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToEditProfile: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var exploreSearchQuery by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .frostedGlassBackground(),
        containerColor = AnikuBackground,
        bottomBar = {
            // Elegant Frosted-styled Material 3 Bottom Nav bar
            NavigationBar(
                containerColor = AnikuSurface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .border(1.dp, Color(0x21FFFFFF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.Transparent)
            ) {
                // Feeds Tab
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda", modifier = Modifier.size(24.dp)) },
                    label = { Text("Beranda", style = AnikuTypography.labelSmall.copy(fontSize = 10.sp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnikuPink,
                        unselectedIconColor = AnikuTextSecondary,
                        selectedTextColor = AnikuPink,
                        unselectedTextColor = AnikuTextSecondary,
                        indicatorColor = AnikuSurfaceVar
                    )
                )

                // Explore Tab
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        exploreSearchQuery = null // clear query on regular tap
                        selectedTab = 1
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Eksplor", modifier = Modifier.size(24.dp)) },
                    label = { Text("Eksplor", style = AnikuTypography.labelSmall.copy(fontSize = 10.sp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnikuPink,
                        unselectedIconColor = AnikuTextSecondary,
                        selectedTextColor = AnikuPink,
                        unselectedTextColor = AnikuTextSecondary,
                        indicatorColor = AnikuSurfaceVar
                    )
                )

                // Share post Tab
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Posting", modifier = Modifier.size(24.dp)) },
                    label = { Text("Posting", style = AnikuTypography.labelSmall.copy(fontSize = 10.sp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnikuPink,
                        unselectedIconColor = AnikuTextSecondary,
                        selectedTextColor = AnikuPink,
                        unselectedTextColor = AnikuTextSecondary,
                        indicatorColor = AnikuSurfaceVar
                    )
                )

                // Profile Tab
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil", modifier = Modifier.size(24.dp)) },
                    label = { Text("Profil", style = AnikuTypography.labelSmall.copy(fontSize = 10.sp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AnikuPink,
                        unselectedIconColor = AnikuTextSecondary,
                        selectedTextColor = AnikuPink,
                        unselectedTextColor = AnikuTextSecondary,
                        indicatorColor = AnikuSurfaceVar
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToPostDetail = onNavigateToPostDetail,
                    onNavigateToUserProfile = onNavigateToUserProfile,
                    onNavigateToExploreWithSearch = { query ->
                        exploreSearchQuery = query
                        selectedTab = 1
                    },
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToRegister = onNavigateToRegister
                )
                1 -> ExploreScreen(
                    initialSearchQuery = exploreSearchQuery,
                    onNavigateToPostDetail = onNavigateToPostDetail,
                    onNavigateToUserProfile = onNavigateToUserProfile,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToRegister = onNavigateToRegister
                )
                2 -> CreatePostScreen(
                    onNavigateToHome = { selectedTab = 0 }
                )
                3 -> ProfileScreen(
                    profileUserId = null, // loading my own profile
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToPostDetail = onNavigateToPostDetail,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToRegister = onNavigateToRegister
                )
            }
        }
    }
}
