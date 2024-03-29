package com.example.final_project.layout.AddData

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.final_project.data.MenuData
import com.example.final_project.data.updateAndSaveImage
import com.example.final_project.data.uploadAndSaveImage


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddData(selectedMenu: MenuData?, onAddOrUpdate: (MenuData) -> Unit) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var namaMakanan by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var mExpanded by remember { mutableStateOf(false) }

    val mCategory = listOf("Makanan", "Minuman", "Snack")

    var selectedCategory by remember { mutableStateOf("") }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val context = LocalContext.current

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
            imageUri?.let {
                bitmap = context.getBitmapFromUri(it)
            }
        }
    )

    if (selectedMenu != null) {
        // Load existing data into the fields for update
        if (imageUri == null) {
            imageUri = Uri.parse(selectedMenu.imageUrl)
            bitmap = context.getBitmapFromUri(imageUri!!)
        }
        if (namaMakanan.isEmpty()) {
            namaMakanan = selectedMenu.nama
        }
        if (harga.isEmpty()) {
            harga = selectedMenu.harga.toString()
        }
        if (selectedCategory.isEmpty()) {
            selectedCategory = selectedMenu.kategori
        }
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 130.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.outlinedCardColors(
            contentColor = Color.Black,
            containerColor = Color(0x3EF44336)
        ),
        border = BorderStroke(1.dp, Color.Black),
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(10))
                    .background(color = Color(0xFFDDDDDD), shape = RoundedCornerShape(5.dp))
                    .clickable {
                        launcherGallery.launch("image/*")
                    }
            ) {
                if (bitmap != null) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                else{
                    Text(text = "Click To Add Image", modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            OutlinedTextField(
                value = namaMakanan,
                onValueChange = { namaMakanan = it },
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(70.dp)
                    .align(Alignment.CenterHorizontally),
                label = { Text("Nama Makanan") },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                ),
            )
            OutlinedTextField(
                value = harga,
                onValueChange = { harga = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(70.dp)
                    .align(Alignment.CenterHorizontally),
                label = { Text("Harga") },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                readOnly = true,
                value = selectedCategory,
                onValueChange = { selectedCategory = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(70.dp)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Kategori") },
                trailingIcon = {
                    Icon(
                        icon,
                        "contentDescription",
                        Modifier
                            .size(30.dp)
                            .clickable { mExpanded = !mExpanded },
                        tint = Color.Black
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                ),
            )
            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .background(
                        color = Color.White,
                    )
                    .clip(shape = RoundedCornerShape(10.dp))

                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                mCategory.forEach { label ->
                    DropdownMenuItem(onClick = {
                        selectedCategory = label
                        mExpanded = false
                    },
                        text = { Text(text = label) }
                    )

                }
            }
            OutlinedButton(
                onClick = {
                    if (selectedMenu != null && selectedMenu.documentId.isNotEmpty()) {
                        updateAndSaveImage(
                            selectedMenu.documentId,
                            bitmap!!,
                            namaMakanan,
                            selectedCategory,
                            harga.toDouble()
                        )

                        onAddOrUpdate(
                            MenuData(
                                harga.toDouble(),
                                imageUri.toString(),
                                namaMakanan,
                                selectedCategory,
                                selectedMenu.documentId,
                            )
                        )
                        Toast.makeText(context, "Data Updated", Toast.LENGTH_SHORT).show()
                    } else {
                        uploadAndSaveImage(
                            bitmap!!,
                            namaMakanan,
                            selectedCategory,
                            harga.toDouble(),
                        )
                        // Clear fields
                        imageUri = null
                        bitmap = null
                        namaMakanan = ""
                        selectedCategory = ""
                        harga = ""
                        Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
                    .height(70.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black,
                    containerColor = Color(0xFFADD6B8),
                ),
                border = BorderStroke(1.dp, Color.Black),
            ) {
                Text(
                    if (selectedMenu != null) "Update Data" else "Add Data",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}