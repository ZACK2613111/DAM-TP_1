package com.example.dam_tp_1.components

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.dam_tp_1.model.ProductType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductImagePicker(
    selectedType: ProductType,
    customImageUri: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImageDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // ✅ PHOTO PICKER - Pas de permissions nécessaires !
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            onImageSelected(it.toString())
        }
    }

    // ✅ CAMÉRA - Avec permission
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            onImageSelected(imageUri.toString())
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = File(
                context.cacheDir,
                "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
            )
            imageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                photoFile
            )
            imageUri?.let { cameraLauncher.launch(it) }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Preview image (même code que avant)
        Card(
            modifier = Modifier
                .size(120.dp)
                .clickable { showImageDialog = true },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = selectedType.accentColor.copy(alpha = 0.1f)
            ),
            border = BorderStroke(2.dp, selectedType.accentColor)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (customImageUri != null) {
                    // Indicateur image sélectionnée
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                selectedType.accentColor.copy(alpha = 0.2f),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Image ajoutée",
                                tint = selectedType.accentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Photo ajoutée",
                                style = MaterialTheme.typography.bodySmall,
                                color = selectedType.accentColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // Image par défaut
                    Image(
                        painter = painterResource(id = selectedType.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).padding(8.dp)
                    )

                    // Icône add
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Card(
                            modifier = Modifier.size(32.dp).offset((-4).dp, (-4).dp),
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(
                                containerColor = selectedType.accentColor
                            )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Ajouter",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (customImageUri != null) "Modifier l'image" else "Ajouter une image",
            style = MaterialTheme.typography.bodySmall,
            color = selectedType.accentColor,
            fontWeight = FontWeight.Medium
        )

        if (customImageUri != null) {
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = { onImageSelected(null) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Supprimer", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // ✅ DIALOG avec Photo Picker moderne
    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = null,
                    tint = selectedType.accentColor
                )
            },
            title = { Text("Choisir une image") },
            text = {
                Column {
                    // ✅ PHOTO PICKER - Pas de permissions !
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                                showImageDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = selectedType.accentColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, null, tint = selectedType.accentColor)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Galerie", fontWeight = FontWeight.Medium)
                                Text(
                                    "Sélection sécurisée",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Caméra (inchangé)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                        val photoFile = File(
                                            context.cacheDir,
                                            "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
                                        )
                                        imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                                        imageUri?.let { cameraLauncher.launch(it) }
                                    }
                                    else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                                showImageDialog = false
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = selectedType.accentColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = selectedType.accentColor)
                            Spacer(Modifier.width(12.dp))
                            Text("Appareil photo", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
