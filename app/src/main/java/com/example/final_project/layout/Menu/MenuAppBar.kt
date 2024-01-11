package com.example.final_project.layout.Menu

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.R
import com.example.final_project.data.CartItem
import com.example.final_project.layout.Cart.CartItemRow
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

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