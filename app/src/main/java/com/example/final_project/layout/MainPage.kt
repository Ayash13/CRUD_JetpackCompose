package com.example.final_project.layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.final_project.R
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainPage(onClickSignout: () -> Unit) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(MainNavItem.Menu) }

    NavHost(navController = navController, startDestination = MainNavItem.Menu.route) {
        composable(MainNavItem.Menu.route) {
            MenuListScreen(onClickSignout = onClickSignout)
        }
        composable(MainNavItem.AddData.route) {
            AddData()
        }
        composable(MainNavItem.History.route) {
            History(
                firestore = FirebaseFirestore.getInstance()
            )
        }

    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            containerColor = Color(0xFFFFFCED),
            tonalElevation = 10.dp,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 20.dp, start = 70.dp, end = 70.dp)
                .height(80.dp)
                .clip(
                    shape = RoundedCornerShape(40.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(40.dp)
                ),

            ) {
            MainNavItem.entries.forEach { navItem ->
                NavigationBarItem(
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                        .fillMaxHeight()
                        .clip(
                            shape = RoundedCornerShape(10.dp)
                        ),
                    selected = selectedItem == navItem,
                    onClick = { selectedItem = navItem; navController.navigate(navItem.route) },
                    icon = {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xA6494949),
                        unselectedIconColor = Color.Black,
                        selectedIconColor = Color.White,
                    )
                )
            }
        }
    }
}

enum class MainNavItem(val iconResId: Int, val route: String) {
    Menu(R.drawable.baseline_home_24, "menu"),
    AddData(R.drawable.baseline_add_circle_outline_24, "add_data"),
    History(R.drawable.baseline_history_24, "history");

    val icon: ImageVector
        @Composable get() = ImageVector.vectorResource(id = iconResId)
}