package com.example.final_project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.final_project.layout.LoginPage
import com.example.final_project.layout.MainPage
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavGraph(navController, isLoggedIn)
}

@Composable
fun NavGraph(navController: NavController, isLoggedIn: Boolean) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = if (isLoggedIn) "MainPage" else "LoginPage"
    ) {
        composable("LoginPage") {
            LoginPage(navController = navController)
        }
        composable("MainPage") {
            MainPage(onClickSignout = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("LoginPage")
            })
        }
    }
}