package com.example.final_project.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.R
import com.example.final_project.data.CartItem
import java.text.NumberFormat
import java.util.Locale

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

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onRemoveItem: (CartItem) -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit // Update the function signature
) {
    val quantityState = remember { mutableIntStateOf(cartItem.quantity) }
    val harga = cartItem.menuItem.harga
    val formatedHarga: String = NumberFormat.getNumberInstance(Locale("id", "ID")).format(harga)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = cartItem.menuItem.nama)
            Text(text = "Rp $formatedHarga", fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (quantityState.intValue > 1) {
                        quantityState.intValue -= 1
                        onQuantityChange(cartItem, quantityState.intValue) // Pass the CartItem and Int
                    }
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_remove_circle_outline_24),
                    contentDescription = "Decrease"
                )
            }

            Text(text = quantityState.intValue.toString())

            IconButton(
                onClick = {
                    quantityState.intValue += 1
                    onQuantityChange(cartItem, quantityState.intValue) // Pass the CartItem and Int
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_add_circle_outline_24),
                    contentDescription = "Increase"
                )
            }
        }

        IconButton(
            onClick = { onRemoveItem(cartItem) },
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}
