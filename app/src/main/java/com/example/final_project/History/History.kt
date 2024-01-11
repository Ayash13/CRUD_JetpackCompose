package com.example.final_project.History

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_project.data.fetchOrders
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun History(firestore: FirebaseFirestore) {
    var expanded by remember { mutableStateOf(false) }
    var orders by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    LaunchedEffect(Unit) {
        fetchOrders(firestore) { fetchedOrders ->
            orders = fetchedOrders
        }
    }

    OutlinedCard ( modifier = Modifier
        .fillMaxSize()
        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 130.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.outlinedCardColors(
            contentColor = Color.Black,
            containerColor = Color(0x3EF44336)
        ),
        border = BorderStroke(1.dp, Color.Black),) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "History",
                modifier = Modifier.padding(20.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (orders.isNotEmpty()) {
                    orders.forEach { order ->
                        HistoryItem(order = order)
                    }
                } else {
                    Text(
                        text = "No Orders Yet",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}