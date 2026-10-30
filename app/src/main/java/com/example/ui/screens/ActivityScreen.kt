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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.BookingEntity
import com.example.data.model.BookingStatus
import com.example.ui.components.BookingStatusBadge
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
import com.example.ui.viewmodel.UserRole

@Composable
fun ActivityScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()

    var showRatingDialogForBookingId by remember { mutableStateOf<Long?>(null) }
    var ratingScore by remember { mutableFloatStateOf(5.0f) }
    var reviewText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Activity & Rentals",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Track your reservations, inspections & payouts",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // Role indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkNavyElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (userRole == UserRole.RENTER) "Mode: Renter" else "Mode: Owner",
                            fontSize = 12.sp,
                            color = if (userRole == UserRole.RENTER) ElectricTeal else SoftPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (allBookings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No booking activity yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Explore nearby bikes, scooters, or cars to request your first ride.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            } else {
                items(allBookings, key = { it.id }) { booking ->
                    BookingItemCard(
                        booking = booking,
                        isOwnerMode = (userRole == UserRole.OWNER),
                        onAccept = {
                            viewModel.updateBookingStatus(booking.id, BookingStatus.ACCEPTED.name)
                        },
                        onDecline = {
                            viewModel.updateBookingStatus(booking.id, BookingStatus.CANCELLED.name)
                        },
                        onPay = {
                            viewModel.updateBookingStatus(booking.id, BookingStatus.PAYMENT_CONFIRMED.name)
                        },
                        onStartInspection = { stage ->
                            viewModel.openInspection(booking.id, stage)
                        },
                        onOpenChat = {
                            viewModel.openChatForVehicle(booking.vehicleId)
                        },
                        onOpenReview = {
                            showRatingDialogForBookingId = booking.id
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Review Dialog
        if (showRatingDialogForBookingId != null) {
            AlertDialog(
                onDismissRequest = { showRatingDialogForBookingId = null },
                containerColor = DarkNavyCard,
                title = {
                    Text(
                        text = "Rate Your Experience",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "How was the ride condition and owner communication?",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )

                        // Star selector
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (star in 1..5) {
                                IconButton(onClick = { ratingScore = star.toFloat() }) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "$star stars",
                                        tint = if (star <= ratingScore) NeonOrange else Color(0xFF475569),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = { Text("Write a quick review for the community...", color = Color(0xFF64748B), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkNavyElevated,
                                unfocusedContainerColor = DarkNavyElevated,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showRatingDialogForBookingId?.let { id ->
                                viewModel.submitReview(
                                    bookingId = id,
                                    rating = ratingScore,
                                    review = reviewText.ifBlank { "Great experience!" }
                                )
                            }
                            showRatingDialogForBookingId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal, contentColor = DeepNavy)
                    ) {
                        Text("Submit Rating", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingDialogForBookingId = null }) {
                        Text("Cancel", color = Color(0xFF94A3B8))
                    }
                }
            )
        }
    }
}

@Composable
private fun BookingItemCard(
    booking: BookingEntity,
    isOwnerMode: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onPay: () -> Unit,
    onStartInspection: (String) -> Unit,
    onOpenChat: () -> Unit,
    onOpenReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.id}")
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF273556), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkNavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with vehicle & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getVehicleIcon(booking.vehicleType),
                            contentDescription = null,
                            tint = ElectricTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = booking.vehicleName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Booking #${booking.id} • ${booking.durationHours} hours",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                BookingStatusBadge(status = booking.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Time and counterpart info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkNavyElevated)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Times: ${booking.startDateTime} - ${booking.endDateTime}",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = "₹${booking.totalAmount.toInt()} total",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricTeal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isOwnerMode) "Renter: ${booking.renterName}" else "Owner: ${booking.ownerName}",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            if (booking.reviewText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = NeonOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Review: \"${booking.reviewText}\"",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF273556), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Contextual Action Buttons based on status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Always allow chat
                OutlinedButton(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricTeal),
                    modifier = Modifier.testTag("booking_chat_btn_${booking.id}").height(38.dp)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", fontSize = 12.sp)
                }

                when (booking.status) {
                    "PENDING" -> {
                        if (isOwnerMode) {
                            Button(
                                onClick = onAccept,
                                colors = ButtonDefaults.buttonColors(containerColor = BrightGreen, contentColor = DeepNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp).testTag("booking_approve_btn")
                            ) {
                                Text("Approve Booking", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onDecline,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("Decline", fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                text = "Awaiting owner response...",
                                color = NeonOrange,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    "ACCEPTED" -> {
                        Button(
                            onClick = onPay,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricTeal, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("booking_pay_btn")
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirm & Pay ₹${booking.totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    "PAYMENT_CONFIRMED", "READY_FOR_PICKUP" -> {
                        Button(
                            onClick = { onStartInspection("PICKUP") },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightGreen, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("booking_pickup_inspection_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pickup Inspection & Unlock", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    "IN_USE" -> {
                        Button(
                            onClick = { onStartInspection("RETURN") },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftPurple, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("booking_return_inspection_btn")
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Return Vehicle & Inspect", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    "RETURNED" -> {
                        Button(
                            onClick = onOpenReview,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("booking_rate_btn")
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rate & Review", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    "COMPLETED" -> {
                        Text(
                            text = "Rental completed • Deposit refunded ✓",
                            color = BrightGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    else -> {
                        Text(
                            text = "Status: ${booking.status}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
