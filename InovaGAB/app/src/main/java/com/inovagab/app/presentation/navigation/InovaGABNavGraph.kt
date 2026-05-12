package com.inovagab.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inovagab.app.presentation.login.LoginScreen
import com.inovagab.app.presentation.home.OperatorHomeScreen
import com.inovagab.app.presentation.home.ManagerHomeScreen
import com.inovagab.app.presentation.home.LeaderHomeScreen

@Composable
fun InovaGABNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = com.inovagab.app.presentation.AppViewModelFactory),
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
