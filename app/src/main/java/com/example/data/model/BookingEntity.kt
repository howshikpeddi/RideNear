package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus(val displayName: String) {
    PENDING("Pending Approval"),
    ACCEPTED("Accepted - Awaiting Payment"),
    PAYMENT_CONFIRMED("Payment Confirmed"),
    READY_FOR_PICKUP("Ready for Pickup"),
    IN_USE("Currently in Use"),
    RETURNED("Vehicle Returned"),
    COMPLETED("Completed"),
    DISPUTED("In Dispute"),
    CANCELLED("Cancelled")
}

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val vehicleName: String,
    val vehicleType: String,
    val vehiclePhoto: String = "",
    val ownerId: String,
    val ownerName: String,
    val renterId: String,
    val renterName: String,
    val startDateTime: String,
    val endDateTime: String,
    val durationHours: Int = 3,
    val hourlyRate: Double,
    val platformFee: Double = 5.0,
    val deposit: Double = 50.0,
    val totalAmount: Double,
    val status: String = "PENDING",
    val pickupInspectionDone: Boolean = false,
    val returnInspectionDone: Boolean = false,
    val pickupNotes: String = "",
    val returnNotes: String = "",
    val rating: Float = 0.0f,
    val reviewText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
