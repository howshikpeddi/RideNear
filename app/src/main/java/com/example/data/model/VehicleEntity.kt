package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VehicleType(val displayName: String, val iconName: String) {
    BIKE("City Bike", "pedal_bike"),
    E_BIKE("Electric Bike", "electric_bike"),
    SCOOTER("E-Scooter", "electric_scooter"),
    CAR("Car", "directions_car")
}

enum class VehicleStatus {
    AVAILABLE_NOW,
    AVAILABLE_LATER,
    RENTED
}

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ownerId: String,
    val ownerName: String,
    val ownerAvatar: String = "",
    val ownerRating: Float = 4.9f,
    val ownerVerification: String = "Verified ID & Phone",
    val type: String, // BIKE, E_BIKE, SCOOTER, CAR
    val brand: String,
    val model: String,
    val year: Int = 2024,
    val description: String,
    val photoDrawableName: String = "", // e.g. "hero_vehicles" or custom
    val locationArea: String,
    val distanceMiles: Double = 0.5,
    val lat: Double = 37.7749,
    val lng: Double = -122.4194,
    val condition: String = "Excellent", // "Like New", "Excellent", "Good"
    val pricePerHour: Double,
    val pricePerDay: Double,
    val securityDeposit: Double,
    val status: String = "AVAILABLE_NOW", // AVAILABLE_NOW, AVAILABLE_LATER, RENTED
    val availableUntil: String = "10:00 PM",
    val rules: String = "Helmets required. Lock when unattended. Return with full charge.",
    val isCarLicenseRequired: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
