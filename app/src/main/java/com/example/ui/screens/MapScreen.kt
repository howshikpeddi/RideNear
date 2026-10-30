package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VehicleEntity
import com.example.ui.components.VehicleStatusBadge
import com.example.ui.components.getVehicleIcon
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.RideNearViewModel

@Composable
fun MapScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.filteredVehicles.collectAsStateWithLifecycle()
    var selectedVehicle by remember { mutableStateOf<VehicleEntity?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        // Futuristic Street Map & Vehicles Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(vehicles) {
                    detectTapGestures { tapOffset ->
                        // Calculate hit detection on vehicle pins
                        val width = size.width
                        val height = size.height
                        val centerX = width / 2f
                        val centerY = height / 2f

                        val tapped = vehicles.find { v ->
                            val dx = ((v.lng - (-122.4194)) * 7500).toFloat()
                            val dy = -((v.lat - 37.7680) * 8500).toFloat()
                            val pinX = centerX + dx
                            val pinY = centerY + dy
                            val dist = kotlin.math.hypot(tapOffset.x - pinX, tapOffset.y - pinY)
                            dist <= 40f
                        }
                        selectedVehicle = tapped
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH / 2f

            // Futuristic Grid lines
            val gridColor = Color(0xFF141F3B)
            val step = 70f
            for (x in 0..(canvasW / step).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, canvasH),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(canvasH / step).toInt()) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y * step),
                    end = Offset(canvasW, y * step),
                    strokeWidth = 1f
                )
            }

            // Simulated Roads / Transit corridors
            val roadColor = Color(0xFF1F2E52)
            val mainRoadColor = Color(0xFF2C3E6B)

            // Market Street diagonal
            drawLine(
                color = mainRoadColor,
                start = Offset(0f, canvasH * 0.7f),
                end = Offset(canvasW, canvasH * 0.2f),
                strokeWidth = 14f
            )
            // Mission St vertical
            drawLine(
                color = roadColor,
                start = Offset(centerX + 60f, 0f),
                end = Offset(centerX + 60f, canvasH),
                strokeWidth = 10f
            )
            // Valencia St vertical
            drawLine(
                color = roadColor,
                start = Offset(centerX - 80f, 0f),
                end = Offset(centerX - 80f, canvasH),
                strokeWidth = 10f
            )
            // 16th St horizontal
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY - 60f),
                end = Offset(canvasW, centerY - 60f),
                strokeWidth = 8f
            )
            // 18th St horizontal
            drawLine(
                color = roadColor,
                start = Offset(0f, centerY + 80f),
                end = Offset(canvasW, centerY + 80f),
                strokeWidth = 8f
            )

            // User Current Location Pulse
            drawCircle(
                color = ElectricTeal.copy(alpha = 0.2f),
                radius = 35f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = ElectricTeal,
                radius = 10f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(centerX, centerY)
            )

            // Draw Vehicle Pins
            vehicles.forEach { v ->
                val dx = ((v.lng - (-122.4194)) * 7500).toFloat()
                val dy = -((v.lat - 37.7680) * 8500).toFloat()
                val pinX = (centerX + dx).coerceIn(40f, canvasW - 40f)
                val pinY = (centerY + dy).coerceIn(120f, canvasH - 180f)

                val pinColor = when (v.status) {
                    "AVAILABLE_NOW" -> BrightGreen
                    "AVAILABLE_LATER" -> NeonOrange
                    else -> Color(0xFF64748B) // Rented
                }

                val isSelected = selectedVehicle?.id == v.id

                // Draw Approximate Location Privacy Halo
                drawCircle(
                    color = pinColor.copy(alpha = if (isSelected) 0.35f else 0.15f),
                    radius = if (isSelected) 48f else 32f,
                    center = Offset(pinX, pinY)
                )

                // Privacy dashed outer boundary ring
                drawCircle(
                    color = pinColor.copy(alpha = 0.5f),
                    radius = if (isSelected) 48f else 32f,
                    center = Offset(pinX, pinY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )

                // Inner Pin Base
                drawCircle(
                    color = DeepNavy,
                    radius = 16f,
                    center = Offset(pinX, pinY)
                )
                drawCircle(
                    color = pinColor,
                    radius = 12f,
                    center = Offset(pinX, pinY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(pinX, pinY)
                )
            }
        }

        // Top Overlay Bar: Privacy Notice & List Toggle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Approximate Privacy Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xEE0B1020))
                        .border(1.dp, Color(0xFF273556), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricTeal,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Approx. locations shown for safety",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Switch to List button
                Box(
                    modifier = Modifier
                        .testTag("map_switch_to_list")
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkNavyElevated)
                        .clickable { viewModel.setNavTab(NavTab.EXPLORE) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewList,
                            contentDescription = "List",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("List View", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend Row (Green: Available now, Orange: Available later, Gray: Rented)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xDD12192F))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(BrightGreen, "Available Now")
                LegendItem(NeonOrange, "Later Today")
                LegendItem(Color(0xFF64748B), "Rented")
            }
        }

        // Center crosshair / My Location re-center button
        IconButton(
            onClick = {
                viewModel.showAlert("Centered on Mission District (0.0 mi)")
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(CircleShape)
                .background(DarkNavyElevated)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                tint = ElectricTeal
            )
        }

        // Bottom Selected Vehicle Card
        AnimatedVisibility(
            visible = selectedVehicle != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 75.dp)
        ) {
            selectedVehicle?.let { vehicle ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("map_vehicle_preview_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getVehicleIcon(vehicle.type),
                                    contentDescription = null,
                                    tint = ElectricTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${vehicle.brand} ${vehicle.model}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { selectedVehicle = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${vehicle.locationArea} (approx. ~${vehicle.distanceMiles} mi)",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹${vehicle.pricePerHour.toInt()}/hour • ₹${vehicle.pricePerDay.toInt()}/day",
                                color = ElectricTeal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            VehicleStatusBadge(status = vehicle.status)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.openChatForVehicle(vehicle.id)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Chat Owner", color = ElectricTeal, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.openBookingCheckout(vehicle.id)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricTeal,
                                    contentColor = DeepNavy
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.Medium)
    }
}
