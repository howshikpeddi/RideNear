package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.getVehicleIcon
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.RideNearViewModel

@Composable
fun InspectionScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    val bookingId by viewModel.selectedBookingId.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val booking = allBookings.find { it.id == bookingId }
    val stage by viewModel.inspectionStage.collectAsStateWithLifecycle()

    val isCar = booking?.vehicleType.equals("CAR", ignoreCase = true)

    val checklistItems = remember(isCar) {
        if (isCar) {
            listOf(
                "Tires & Wheel Rims",
                "Brakes & Brake Lights",
                "Headlights & Turn Signals",
                "Mirrors & Windows Condition",
                "Exterior Body (Scratches/Dents)",
                "Fuel / Battery Charge Level",
                "Odometer Reading Recorded",
                "Dashboard Warning Lights (None)",
                "Required Documents & Glovebox Registration"
            )
        } else {
            listOf(
                "Tires (Pressure & Tread)",
                "Brakes (Front & Rear Levers)",
                "Chain & Drivetrain Condition",
                "Pedals & Cranks",
                "Seat Adjustment & Post Clamps",
                "Front & Rear Safety Lights",
                "Bell / Horn Working",
                "Battery Level & Motor Assist",
                "Frame (No structural cracks)",
                "Security U-Lock & Cable Included"
            )
        }
    }

    val checkedMap = remember(checklistItems) {
        mutableStateMapOf<String, Boolean>().apply {
            checklistItems.forEach { put(it, true) }
        }
    }

    var photoCount by remember { mutableIntStateOf(3) }
    var inspectionNotes by remember { mutableStateOf("") }
    var coSignatureAgreed by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_NAV) },
                    modifier = Modifier.testTag("inspection_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (stage == "PICKUP") "Pickup Condition Inspection" else "Return Condition Inspection",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${booking?.vehicleName ?: "Vehicle"} • Booking #${booking?.id ?: ""}",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info banner explaining joint co-inspection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ElectricTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Both renter and owner verify vehicle condition before unlocking and upon return. Photos protect your deposit.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 17.sp
                        )
                    }
                }

                // Photo Evidence Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Inspection Photos ($photoCount taken)",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkNavyElevated)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (photoCount >= 3) "Required (3/3) ✓" else "Min 3 photos",
                                    fontSize = 11.sp,
                                    color = if (photoCount >= 3) BrightGreen else NeonOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Photo slot placeholders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Front & Controls", "Side Profile", "Rear & Lock").forEachIndexed { index, label ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkNavyElevated)
                                        .border(1.dp, ElectricTeal.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.showAlert("Photo verified: $label")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BrightGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Add additional photo button
                        Button(
                            onClick = {
                                photoCount++
                                viewModel.showAlert("Added new inspection angle photo! Total: $photoCount")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("add_inspection_photo_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ElectricTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Additional Photo (+)", color = ElectricTeal, fontSize = 12.sp)
                        }
                    }
                }

                // Checklist Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isCar) "Car Safety & Component Checklist" else "Bike & Mobility Checklist",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )

                        checklistItems.forEach { item ->
                            val isChecked = checkedMap[item] == true
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { checkedMap[item] = !isChecked }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedMap[item] = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = BrightGreen,
                                        checkmarkColor = DeepNavy,
                                        uncheckedColor = Color(0xFF64748B)
                                    ),
                                    modifier = Modifier.testTag("checklist_item_${item.take(6).lowercase()}")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    fontSize = 13.sp,
                                    color = if (isChecked) Color.White else Color(0xFF94A3B8),
                                    fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Notes / Discrepancies
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Condition Notes / Pre-existing Marks",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            // Quick Dictate button
                            Button(
                                onClick = {
                                    inspectionNotes = "Checked during pickup: tires pressurized, brakes responsive, chain clean, lock attached. (Dictated via gemini-3.5-transcribe)"
                                    viewModel.showAlert("Notes transcribed with gemini-3.5-transcribe!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyElevated),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("dictate_notes_btn")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = ElectricTeal, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Dictate (AI)", fontSize = 11.sp, color = ElectricTeal)
                            }
                        }
                        OutlinedTextField(
                            value = inspectionNotes,
                            onValueChange = { inspectionNotes = it },
                            placeholder = {
                                Text(
                                    "e.g. Tiny 1cm scratch on left handlebar, battery 92%, tires firm.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("inspection_notes_field"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyElevated,
                                unfocusedContainerColor = DarkNavyElevated,
                                focusedBorderColor = ElectricTeal,
                                unfocusedBorderColor = Color(0xFF273556),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }

                // Joint Confirmation Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavyCard)
                        .clickable { coSignatureAgreed = !coSignatureAgreed }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = coSignatureAgreed,
                        onCheckedChange = { coSignatureAgreed = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ElectricTeal,
                            checkmarkColor = DeepNavy
                        ),
                        modifier = Modifier.testTag("co_sign_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I verify that the vehicle condition described above matches reality. Digital sign-off timestamped.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Sticky Bottom Submit Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkNavySurface)
                .border(1.dp, Color(0xFF273556))
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    booking?.let { b ->
                        val summary = checkedMap.entries.joinToString(", ") { "${it.key}: ${if (it.value) "OK" else "Issue"}" }
                        viewModel.submitInspection(
                            bookingId = b.id,
                            vehicleId = b.vehicleId,
                            stage = stage,
                            summary = summary,
                            issues = inspectionNotes.ifBlank { "Clean condition confirmed" },
                            photoCount = photoCount
                        )
                    }
                },
                enabled = coSignatureAgreed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_inspection_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrightGreen,
                    contentColor = DeepNavy
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (stage == "PICKUP") "Confirm Pickup & Start Ride" else "Confirm Return & Complete Rental",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
