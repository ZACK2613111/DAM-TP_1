package com.example.dam_tp_1.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_tp_1.ui.theme.*

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
        expanded, onDismiss, Modifier
            .width(280.dp)
            .shadow(16.dp, RoundedCornerShape(20.dp))) {
        // === HEADER PREMIUM ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Primary.copy(0.1f), Color.White)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            Brush.verticalGradient(listOf(Primary, PrimaryContainer)),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        userName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color(0xFF1C1B1F),
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Collection Premium ✨",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = Color(0xFFE65100)
                    )
                }
            }
        }

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

        Spacer(Modifier.height(8.dp))

        // === MENU ITEMS ===
        MenuItemPremium(
            icon = Icons.Default.AccountCircle,
            text = "Mon profil",
            onClick = { onDismiss(); onProfileClick() }
        )

        MenuItemPremium(
            icon = Icons.Default.Settings,
            text = "Paramètres",
            onClick = { onDismiss(); onSettingsClick() }
        )

        MenuItemPremium(
            icon = Icons.Default.Analytics,
            text = "Mes statistiques",
            onClick = { onDismiss(); onStatsClick() }
        )

        MenuItemPremium(
            icon = Icons.Default.Help,
            text = "Aide & Support",
            onClick = { onDismiss(); onHelpClick() }
        )

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

        Spacer(Modifier.height(8.dp))

        // === LOGOUT ===
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.errorContainer.copy(0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        "Se déconnecter",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            onClick = { onDismiss(); onLogout() }
        )

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MenuItemPremium(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Primary.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        null,
                        tint = Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1C1B1F)
                )
            }
        },
        onClick = onClick
    )
}
