package com.example.final_project.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun FetchMenusFromFirestore(): List<MenuData> {
    return withContext(Dispatchers.IO) {
        val querySnapshot = FirebaseFirestore.getInstance()
            .collection("menu")
            .get()
            .await()

        val menuList = querySnapshot.documents.mapNotNull { doc ->
            MenuData(
                doc.getDouble("Harga") ?: 0.0,
                doc.getString("ImageUrl") ?: "",
                doc.getString("Nama") ?: "",
                doc.getString("Kategori") ?: ""
            )
        }
        menuList
    }
}