package com.example.final_project.History

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryItem(order: Map<String, Any>) {
    var expanded by remember { mutableStateOf(false) }

    val timestamp = order["timestamp"] as? com.google.firebase.Timestamp
    val formattedTimestamp = timestamp?.toDate()?.let {
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it)
    } ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.outlinedCardColors(
            contentColor = Color.Black,
            containerColor = Color(0xFFFFFCED),
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedTimestamp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }

            if (expanded) {
                Column {
                    Divider(
                        color = Color.Black,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    LazyRow(
                        userScrollEnabled = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        order["menu"]?.let { menuList ->
                            item {
                                Column {
                                    Text(text = "Menu", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(7.dp))
                                    (menuList as? List<*>)?.forEach { menu ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "* ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(text = "$menu", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                        order["quantity"]?.let { quantityList ->
                            item {
                                Column {
                                    Text(
                                        text = "Quantity",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(7.dp))
                                    (quantityList as? List<*>)?.forEach { quantity ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "* ",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(text = "$quantity", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }

                        order["harga"]?.let { hargaList ->
                            item {
                                val formattedHargaList = hargaList as? List<*>
                                if (formattedHargaList != null && formattedHargaList.isNotEmpty()) {
                                    Column {
                                        Text(
                                            text = "Harga",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(7.dp))
                                        formattedHargaList.forEach { hargaItem ->
                                            val harga = hargaItem as? Double ?: 0.0
                                            val formattedHarga: String = NumberFormat.getNumberInstance(
                                                Locale("id", "ID")
                                            ).format(harga)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "* ",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(text = "Rp $formattedHarga", fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }
                    Divider(
                        color = Color.Black,
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    order["total"]?.let {
                        val total = order["total"] as? String ?: "00"
                        Row (modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Total :",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                            Text(
                                text = "Rp $total",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

        }
    }
}
