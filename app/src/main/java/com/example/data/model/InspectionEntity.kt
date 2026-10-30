package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingId: Long,
    val vehicleId: Long,
    val stage: String, // "PICKUP" or "RETURN"
    val completedByRole: String, // "RENTER" or "OWNER"
    val checklistSummary: String, // e.g. "Tires: OK, Brakes: OK, Battery: 94%"
    val issuesFound: String = "",
    val photoCount: Int = 3,
    val signatureConfirmed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
