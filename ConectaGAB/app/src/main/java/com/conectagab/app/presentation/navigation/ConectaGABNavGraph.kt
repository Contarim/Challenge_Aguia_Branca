package com.conectagab.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.conectagab.app.presentation.login.LoginScreen
import com.conectagab.app.presentation.home.OperatorHomeScreen
import com.conectagab.app.presentation.home.ManagerHomeScreen
import com.conectagab.app.presentation.home.LeaderHomeScreen

@Composable
fun ConectaGABNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = com.conectagab.app.presentation.AppViewModelFactory),
                onNavigateToOperator = { navController.navigate("operator") { popUpTo("login") { inclusive = true } } },
                onNavigateToManager = { navController.navigate("manager") { popUpTo("login") { inclusive = true } } },
                onNavigateToLeader = { navController.navigate("leader") { popUpTo("login") { inclusive = true } } }
            )
        }
        composable("operator") {
            OperatorHomeScreen(onNavigateToLogin = { navController.navigate("login") { popUpTo(0) } })
        }
        composable("manager") {
            ManagerHomeScreen(onNavigateToLogin = { navController.navigate("login") { popUpTo(0) } })
        }
        composable("leader") {
            LeaderHomeScreen(onNavigateToLogin = { navController.navigate("login") { popUpTo(0) } })
        }
    }
}
