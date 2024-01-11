package com.example.final_project.layout.Cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.data.CartItem

@Composable
fun Cart(
    cartItems: List<CartItem>,
    onRemoveItem: (CartItem) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit
) {
    Column {
        Text(text = "Your Cart", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (cartItems.isEmpty()) {
            Text(text = "Your cart is empty")
        } else {
            LazyColumn {
                items(cartItems) { cartItem ->
                    CartItemRow(cartItem, onRemoveItem, onQuantityChange)
                    Divider(color = Color.Gray, thickness = 1.dp)
                }
            }
        }
    }
}

