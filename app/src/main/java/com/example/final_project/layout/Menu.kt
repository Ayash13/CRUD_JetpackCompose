package com.example.final_project.layout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.final_project.R
import com.example.final_project.data.CartItem
import com.example.final_project.data.FetchMenusFromFirestore
import com.example.final_project.data.MenuData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun MenuListScreen(onClickSignout: () -> Unit) {
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
                MenuItem(menu = menu, addToCart = addToCart, onDeleteItem = {
                    scope.launch {
                        // Delete the menu item from Firestore and refresh the menu list
                        menus = deleteMenuFromFirestore(menu)
                        Toast.makeText(context, "Menu deleted", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onClickSignout: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    cartItems: List<CartItem>,
    onDismissDialog: () -> Unit,
    onRemoveCartItem: (CartItem) -> Unit, // Add a new callback for removing items
    onQuantityChanged: (List<CartItem>) -> Unit,
    firestore: FirebaseFirestore, // Inject FirebaseFirestore instance
    userId: String,// Pass the user ID
    setCartItems: (List<CartItem>) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var total by remember { mutableStateOf(0) } // State to hold the total amount
    // Calculate total dynamically based on cart items
    val newTotal = cartItems.sumOf { it.menuItem.harga * it.quantity }
    val formattedTotal: String = NumberFormat.getNumberInstance(Locale("id", "ID")).format(total)

    if (total.toDouble() != newTotal) {
        total = newTotal.toInt() // Update the total when it changes
    }
    if (showDialog) {
        AlertDialog(
            modifier = Modifier.padding(bottom = 30.dp),
            onDismissRequest = {
                showDialog = false
                onDismissDialog()
            },
            content = {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(550.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.outlinedCardColors(
                        contentColor = Color.Black,
                        containerColor = Color(0xFFFFFCED)
                    ),
                    border = BorderStroke(1.dp, Color.Black),

                    ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cart",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Divider(
                            color = Color.Black,
                            thickness = 1.dp,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (cartItems.isEmpty()) {
                            Text(
                                text = "Cart Is Empty",
                                fontSize = 20.sp,
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                LazyVerticalGrid(
                                    userScrollEnabled = true,
                                    contentPadding = PaddingValues(8.dp),
                                    columns = GridCells.Fixed(1),
                                ) {
                                    items(cartItems) { cartItem ->
                                        CartItemRow(
                                            cartItem,
                                            onRemoveItem = {
                                                onRemoveCartItem(cartItem)
                                            },
                                            onQuantityChange = { updatedItem, newQuantity ->
                                                // Update the quantity for the item in the cart
                                                val updatedCartItems = cartItems.toMutableList()
                                                val index =
                                                    updatedCartItems.indexOfFirst { it == updatedItem }
                                                if (index != -1) {
                                                    updatedCartItems[index] =
                                                        updatedItem.copy(quantity = newQuantity)
                                                    // Update the cart items with the new quantity
                                                    onQuantityChanged(updatedCartItems)
                                                }
                                            }
                                        )
                                    }
                                }
                                Column {
                                    Divider(
                                        color = Color.Black,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(top = 20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Rp $formattedTotal",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val currentTime = Calendar.getInstance().time
                                            // Create a new document in Firestore with the user's ID as the collection
                                            val orderData = hashMapOf(
                                                "menu" to cartItems.map { it.menuItem.nama },
                                                "harga" to cartItems.map { it.menuItem.harga },
                                                "kategori" to cartItems.map { it.menuItem.kategori },
                                                "quantity" to cartItems.map { it.quantity },
                                                "total" to formattedTotal,
                                                "timestamp" to currentTime
                                            )

                                            // Add the order data to Firestore
                                            firestore.collection("orders").document(userId)
                                                .collection("user_orders")
                                                .add(orderData)
                                                .addOnSuccessListener {
                                                    // Clear the cart
                                                    setCartItems(emptyList())
                                                    // Dismiss the dialog
                                                    showDialog = false
                                                    onDismissDialog()
                                                    Toast.makeText(
                                                        context,
                                                        "Order Success",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener {
                                                    // Handle any errors that may occur
                                                }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(70.dp)
                                            .padding(vertical = 8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.Black,
                                            containerColor = Color(0xFFADD6B8),
                                        ),
                                        border = BorderStroke(1.dp, Color.Black),
                                    ) {
                                        Text("Order Now", fontWeight = FontWeight.Bold)
                                    }

                                }
                            }
                        }
                    }
                }
            },
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Order Menu",
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row {
            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.padding(end = 5.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_shopping_cart_24),
                    contentDescription = "Cart"
                )
            }
            IconButton(
                onClick = { onClickSignout() },
                modifier = Modifier.padding(end = 5.dp),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_logout_24),
                    contentDescription = "Logout"
                )
            }
        }
    }

    // Search bar using TextField
    OutlinedTextField(
        value = searchQuery,
        onValueChange = {
            onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .height(70.dp),
        label = { Text("Search by Name or Category") },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
        ),
    )
    Spacer(modifier = Modifier.height(10.dp))
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(menu: MenuData, addToCart: (MenuData, Int) -> Unit, onDeleteItem: () -> Unit) {
    var quantity by remember { mutableIntStateOf(0) }
    val formattedPrice: String = NumberFormat.getNumberInstance(Locale("id", "ID"))
        .format(menu.harga)

    val context = LocalContext.current

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.outlinedCardColors(
            contentColor = Color.Black,
            containerColor = Color(0x3EF44336)
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        Column {
            Box() {
                Image(
                    painter = rememberAsyncImagePainter(menu.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(30.dp)
                        .width(70.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(topEnd = 15.dp, bottomStart = 15.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(topEnd = 15.dp, bottomStart = 15.dp)
                        )
                        .background(Color(0xFFFFBCB7))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.outline_close_24),
                            contentDescription = "Delete",
                            modifier = Modifier
                                .size(25.dp)
                                .padding(start = 10.dp)
                                .clickable { onDeleteItem() },
                        )
                        Divider(
                            color = Color.Black,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.outline_edit_24),
                            contentDescription = "Edit",
                            modifier = Modifier
                                .size(25.dp)
                                .padding(end = 10.dp)
                                .clickable { },
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = menu.nama,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Rp $formattedPrice", // Display the formatted pricethe
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_remove_circle_outline_24),
                        contentDescription = "Decrement"
                    )
                }

                Text(
                    text = quantity.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_add_circle_outline_24),
                        contentDescription = "Increment"
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    // Add the selected item to the cart with its quantity
                    addToCart(menu, quantity)
                    // Toast
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    quantity = 0 // Reset the quantity after adding to the cart
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black,
                    containerColor = Color(0xFFADD6B8),
                ),
                border = BorderStroke(1.dp, Color.Black),
            ) {
                Text("Order", fontWeight = FontWeight.Bold)
            }
        }

    }
}

suspend fun deleteMenuFromFirestore(menu: MenuData): List<MenuData> {
    val firestore = FirebaseFirestore.getInstance()
    return withContext(Dispatchers.IO) {
        try {
            firestore.collection("menu")
                .whereEqualTo("Nama", menu.nama)
                .get()
                .await()
                .documents
                .forEach { document ->
                    document.reference.delete()
                }
            // Return the updated menu list after deletion
            FetchMenusFromFirestore()
        } catch (e: Exception) {
            // Handle exceptions or errors
            emptyList()
        }
    }
}