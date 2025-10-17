package com.example.dam_tp_1.components

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
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
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // ✅ PHOTO PICKER - ANDROID 13+ (PAS DE PERMISSIONS!)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            println("✅ Photo Picker: $it")
            onImageSelected(it.toString())
        }
    }

    // ✅ GALLERY PICKER - ANDROID 12 ET MOINS
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            println("✅ Gallery: $it")
            onImageSelected(it.toString())
        }
    }

    // ✅ CAMERA LAUNCHER
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            println("✅ Camera success: $tempCameraUri")
            onImageSelected(tempCameraUri.toString())
        } else {
            println("❌ Camera failed")
        }
    }

    // ✅ PERMISSIONS LAUNCHER
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            println("✅ Permissions accordées")
            launchCamera(context) { uri ->
                tempCameraUri = uri
                cameraLauncher.launch(uri)
            }
        } else {
            println("❌ Permissions refusées")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // ✅ PREVIEW IMAGE
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
                    // Afficher l'image personnalisée
                    AsyncImage(
                        model = customImageUri,
                        contentDescription = "Image personnalisée",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay pour indiquer qu'une image est sélectionnée
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                selectedType.accentColor.copy(alpha = 0.1f),
                                RoundedCornerShape(14.dp)
                            )
                    )

                } else {
                    // Image par défaut + bouton add
                    Image(
                        painter = painterResource(id = selectedType.imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(8.dp)
                    )

                    // Bouton Add en overlay
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Card(
                            modifier = Modifier
                                .size(32.dp)
                                .offset((-4).dp, (-4).dp),
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

    // ✅ DIALOG DE SÉLECTION
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // ✅ PHOTO PICKER (ANDROID 13+) OU GALLERY (ANDROID 12-)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageDialog = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    // Android 13+ - Photo Picker
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                } else {
                                    // Android 12 et moins - Gallery
                                    galleryLauncher.launch("image/*")
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = selectedType.accentColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = selectedType.accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) "Galerie moderne" else "Galerie",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) "Sélection sécurisée (Android 13+)" else "Accès complet aux images",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // ✅ CAMÉRA AVEC PERMISSIONS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showImageDialog = false

                                // Vérifier permission caméra
                                val cameraPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )

                                if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
                                    // Permission déjà accordée
                                    launchCamera(context) { uri ->
                                        tempCameraUri = uri
                                        cameraLauncher.launch(uri)
                                    }
                                } else {
                                    // Demander permission
                                    permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = selectedType.accentColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = selectedType.accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Appareil photo",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Prendre une nouvelle photo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showImageDialog = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ✅ FONCTION HELPER POUR CAMÉRA
private fun launchCamera(
    context: Context,
    onUriCreated: (Uri) -> Unit
) {
    try {
        // Créer un nom unique pour la photo
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        // Créer le fichier dans Pictures/
        val picturesDir = File(context.getExternalFilesDir(null), "Pictures")
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val photoFile = File(picturesDir, fileName)

        println("📁 Fichier créé: ${photoFile.absolutePath}")

        // ✅ CRÉER L'URI AVEC FileProvider
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(
            context,
            authority,
            photoFile
        )

        println("✅ URI créé: $uri")
        println("✅ Autorité: $authority")

        onUriCreated(uri)

    } catch (e: Exception) {
        println("❌ Erreur création URI: ${e.message}")
        e.printStackTrace()
    }
}
