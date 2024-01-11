package com.example.final_project.layout.Menu

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.final_project.data.CartItem
import com.example.final_project.data.FetchMenusFromFirestore
import com.example.final_project.data.MenuData
import com.example.final_project.data.deleteMenuFromFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun MenuListScreen(onClickSignout: () -> Unit, onEditItem: (MenuData) -> Unit, navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    var menus by remember { mutableStateOf<List<MenuData>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val (cartItems, setCartItems) = remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }


    // Function to add items to the cart
    val addToCart: (MenuData, Int) -> Unit = { menu, quantity ->
        val existingCartItem = cartItems.find { it.menuItem == menu }
        if (existingCartItem != null) {
            existingCartItem.quantity += quantity
            setCartItems(cartItems.toMutableList()) // Trigger recomposition by updating the list reference
        } else {
            val newCartItem = CartItem(menu, quantity)
            setCartItems(cartItems + newCartItem)
        }
    }


    LaunchedEffect(Unit) {
        // Fetch menus from Firestore initially
        menus = FetchMenusFromFirestore()
    }

    Column {
        AppBar(
            onClickSignout = onClickSignout,
            searchQuery = searchQuery,
            onSearchQueryChanged = { query ->
                searchQuery = query
                scope.launch {
                    menus = if (query.isNotBlank()) {
                        val filtered = FetchMenusFromFirestore().filter { menu ->
                            menu.nama.contains(query, ignoreCase = true) ||
                                    menu.kategori.contains(query, ignoreCase = true)
                        }
                        filtered
                    } else {
                        FetchMenusFromFirestore() // Reset menus to initial state when query is empty
                    }
                }
            },
            cartItems = cartItems,
            onDismissDialog = { showDialog = false },
            onRemoveCartItem = { cartItem ->
                // Remove the item from the cart
                val updatedCartItems = cartItems.toMutableList()
                updatedCartItems.remove(cartItem)
                setCartItems(updatedCartItems)
            },
            onQuantityChanged = { updatedCartItems ->
                // Update the cart items with the new quantity
                setCartItems(updatedCartItems)
            },
            firestore = FirebaseFirestore.getInstance(), // Pass the instance
            userId = currentUser?.uid ?: "",
            setCartItems = setCartItems
        )

        LazyVerticalGrid(
            userScrollEnabled = true,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(menus.size) { index ->
                val menu = menus[index]
                MenuItem(
                    menu = menu, addToCart = addToCart,
                    onDeleteItem = {
                        scope.launch {
                            // Delete the menu item from Firestore and refresh the menu list
                            menus = deleteMenuFromFirestore(menu)
                            Toast.makeText(context, "Menu deleted", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEditItem = {
                        onEditItem(menu)
                    },
                    navController = navController
                )
            }
        }
    }
}
