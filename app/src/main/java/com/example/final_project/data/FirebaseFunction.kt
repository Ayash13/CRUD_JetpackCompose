package com.example.final_project.data

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

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
                doc.getString("Kategori") ?: "",
                doc.getString("documentId") ?: ""
            )
        }
        menuList
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

fun updateAndSaveImage(
    documentId: String,
    bitmap: Bitmap,
    namaMakanan: String,
    selectedCategory: String,
    harga: Double
) {
    val imageName = "$namaMakanan-$selectedCategory"

    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val imagesRef: StorageReference = storageRef.child("images/$imageName.jpg")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    val uploadTask = imagesRef.putBytes(data)
    uploadTask.addOnSuccessListener { taskSnapshot ->
        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            updateDataInFirestore(documentId, uri.toString(), namaMakanan, selectedCategory, harga)
        }
    }
}

fun uploadAndSaveImage(
    bitmap: Bitmap,
    namaMakanan: String,
    selectedCategory: String,
    harga: Double
) {
    val imageName = "$namaMakanan-$selectedCategory" // Constructing image name

    val storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val imagesRef: StorageReference = storageRef.child("images/$imageName.jpg")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    val uploadTask = imagesRef.putBytes(data)
    uploadTask.addOnSuccessListener { taskSnapshot ->
        // Image uploaded successfully, get download URL
        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            saveDataToFirestore(uri.toString(), namaMakanan, selectedCategory, harga)
        }
    }
}

fun saveDataToFirestore(
    imageUrl: String,
    namaMakanan: String,
    selectedCategory: String,
    harga: Double
) {
    val firestore = FirebaseFirestore.getInstance()
    val menuCollection = firestore.collection("menu")
    val menuData = hashMapOf(
        "ImageUrl" to imageUrl,
        "Nama" to namaMakanan,
        "Kategori" to selectedCategory,
        "Harga" to harga,
        "documentId" to ""
    )

    val newDocRef = menuCollection.add(menuData)
    newDocRef.addOnSuccessListener { documentReference ->
        val documentId = documentReference.id
        // Update the 'documentId' field in Firestore with the generated document ID
        documentReference.update("documentId", documentId)
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
    }
}

fun updateDataInFirestore(
    documentId: String,
    imageUrl: String,
    namaMakanan: String,
    selectedCategory: String,
    harga: Double
) {
    val firestore = FirebaseFirestore.getInstance()
    val menuCollection = firestore.collection("menu")

    if (documentId.isNotEmpty()) {
        val menuDocument = menuCollection.document(documentId)
        val menuData = hashMapOf(
            "ImageUrl" to imageUrl,
            "Nama" to namaMakanan,
            "Kategori" to selectedCategory,
            "Harga" to harga,
            "documentId" to documentId
        )

        menuDocument.set(menuData)
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener { e ->
                // Handle failure if needed
            }
    } else {
        // Handle the case where the documentId is empty or invalid
    }
}

fun fetchOrders(firestore: FirebaseFirestore, onComplete: (List<Map<String, Any>>) -> Unit) {
    val orders = mutableListOf<Map<String, Any>>()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    firestore.collection("orders").document(userId)
        .collection("user_orders")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val orderData = document.data
                orders.add(orderData)
            }
            onComplete(orders)
        }
}