package com.example.final_project.layout.Menu

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.final_project.R
import com.example.final_project.data.MenuData
import com.example.final_project.layout.MainNavItem
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(
    menu: MenuData,
    addToCart: (MenuData, Int) -> Unit,
    onDeleteItem: () -> Unit,
    onEditItem: (MenuData) -> Unit,
    navController: NavController
) {
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
                                .clickable {
                                    onEditItem(menu)
                                    navController.navigate(MainNavItem.AddData.route)
                                },
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
