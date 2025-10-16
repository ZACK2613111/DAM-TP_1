package com.example.dam_tp_1.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun StepHeader(
    stepNumber: Int,
    totalSteps: Int,
    title: String,
    subtitle: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // === PROGRESS BAR MODERNE ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { step ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .shadow(
                            if (step < stepNumber) 4.dp else 0.dp,
                            RoundedCornerShape(3.dp)
                        )
                        .background(
                            if (step < stepNumber) {
                                Brush.horizontalGradient(
                                    colors = listOf(Primary, PrimaryContainer)
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Gray.copy(alpha = 0.2f),
                                        Color.Gray.copy(alpha = 0.2f)
                                    )
                                )
                            },
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // === STEP INFO ===
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Step number badge avec gradient
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary, PrimaryContainer)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stepNumber.toString(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }

            Spacer(Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFF1C1B1F)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }

            // Step indicator
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Primary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "$stepNumber/$totalSteps",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
