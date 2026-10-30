package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple

@Composable
fun VehicleStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, dotColor, text) = when (status) {
        "AVAILABLE_NOW" -> Triple(Color(0xFF0F3628), BrightGreen, "Available Now")
        "AVAILABLE_LATER" -> Triple(Color(0xFF3B2A15), NeonOrange, "Available Later")
        "RENTED" -> Triple(Color(0xFF282C37), Color(0xFF94A3B8), "Currently Rented")
        else -> Triple(Color(0xFF1E293B), Color(0xFF94A3B8), status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = dotColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun BookingStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        "PENDING" -> Triple(Color(0xFF3B2D10), NeonOrange, "Pending Approval")
        "ACCEPTED" -> Triple(Color(0xFF1D294B), SoftPurple, "Accepted • Pay Now")
        "PAYMENT_CONFIRMED" -> Triple(Color(0xFF0C3833), ElectricTeal, "Paid & Confirmed")
        "READY_FOR_PICKUP" -> Triple(Color(0xFF0B3A27), BrightGreen, "Ready for Pickup")
        "IN_USE" -> Triple(Color(0xFF0C3833), ElectricTeal, "In Use")
        "RETURNED" -> Triple(Color(0xFF2C224E), SoftPurple, "Returned • Inspect")
        "COMPLETED" -> Triple(Color(0xFF0B3A27), BrightGreen, "Completed")
        "CANCELLED" -> Triple(Color(0xFF381515), DangerRed, "Cancelled")
        else -> Triple(Color(0xFF1E293B), Color.White, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
