package com.example.dam_tp_1.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileMenu(
    userName: String = "Utilisateur",
    userEmail: String = "email@example.com",
    expanded: Boolean,
    onDismiss: () -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onHelpClick: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.width(220.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Card(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        userName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "Collection Premium",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        HorizontalDivider()

        // Menu items
        DropdownMenuItem(
            text = { Text("Mon profil") },
            onClick = { onDismiss(); onProfileClick() },
            leadingIcon = { Icon(Icons.Default.AccountCircle, null) }
        )

        DropdownMenuItem(
            text = { Text("Paramètres") },
            onClick = { onDismiss(); onSettingsClick() },
            leadingIcon = { Icon(Icons.Default.Settings, null) }
        )

        DropdownMenuItem(
            text = { Text("Mes statistiques") },
            onClick = { onDismiss(); onStatsClick() },
            leadingIcon = { Icon(Icons.Default.Analytics, null) }
        )

        DropdownMenuItem(
            text = { Text("Aide & Support") },
            onClick = { onDismiss(); onHelpClick() },
            leadingIcon = { Icon(Icons.Default.Help, null) }
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = {
                Text(
                    "Se déconnecter",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            },
            onClick = { onDismiss(); onLogout() },
            leadingIcon = {
                Icon(
                    Icons.Default.Logout,
                    null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}
