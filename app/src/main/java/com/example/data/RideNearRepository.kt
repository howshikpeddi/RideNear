package com.example.data

import com.example.data.dao.RideNearDao
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.InspectionEntity
import com.example.data.model.VehicleEntity
import kotlinx.coroutines.flow.Flow

class RideNearRepository(private val dao: RideNearDao) {

    val allVehicles: Flow<List<VehicleEntity>> = dao.getAllVehicles()
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()

    fun getVehicleById(id: Long): Flow<VehicleEntity?> = dao.getVehicleById(id)
    suspend fun getVehicleByIdOnce(id: Long): VehicleEntity? = dao.getVehicleByIdOnce(id)
    fun getVehiclesByOwner(ownerId: String): Flow<List<VehicleEntity>> = dao.getVehiclesByOwner(ownerId)

    suspend fun insertVehicle(vehicle: VehicleEntity): Long = dao.insertVehicle(vehicle)
    suspend fun updateVehicle(vehicle: VehicleEntity) = dao.updateVehicle(vehicle)
    suspend fun updateVehicleStatus(id: Long, status: String) = dao.updateVehicleStatus(id, status)

    fun getBookingsByRenter(renterId: String): Flow<List<BookingEntity>> = dao.getBookingsByRenter(renterId)
    fun getBookingsByOwner(ownerId: String): Flow<List<BookingEntity>> = dao.getBookingsByOwner(ownerId)
    fun getBookingById(id: Long): Flow<BookingEntity?> = dao.getBookingById(id)

    suspend fun insertBooking(booking: BookingEntity): Long = dao.insertBooking(booking)
    suspend fun updateBooking(booking: BookingEntity) = dao.updateBooking(booking)
    suspend fun updateBookingStatus(bookingId: Long, status: String) = dao.updateBookingStatus(bookingId, status)

    fun getMessagesForVehicle(vehicleId: Long): Flow<List<ChatMessageEntity>> = dao.getMessagesForVehicle(vehicleId)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long = dao.insertChatMessage(msg)

    fun getInspectionsForBooking(bookingId: Long): Flow<List<InspectionEntity>> = dao.getInspectionsForBooking(bookingId)
    suspend fun insertInspection(inspection: InspectionEntity): Long = dao.insertInspection(inspection)

    suspend fun checkAndSeedInitialData() {
        val existingVehicles = dao.getAllVehiclesList()
        if (existingVehicles.isNotEmpty()) {
            // Migrate any existing old dollar prices to INR
            existingVehicles.forEach { vehicle ->
                if (vehicle.pricePerHour < 50.0) {
                    val updated = vehicle.copy(
                        pricePerHour = vehicle.pricePerHour * 20.0,
                        pricePerDay = vehicle.pricePerDay * 20.0,
                        securityDeposit = vehicle.securityDeposit * 25.0
                    )
                    dao.updateVehicle(updated)
                }
            }
        } else {
            val sampleVehicles = listOf(
                VehicleEntity(
                    id = 1,
                    ownerId = "owner_alex",
                    ownerName = "Alex Rivera",
                    ownerAvatar = "AR",
                    ownerRating = 4.9f,
                    ownerVerification = "ID & Phone Verified",
                    type = "BIKE",
                    brand = "Specialized",
                    model = "Sirrus X 3.0",
                    year = 2024,
                    description = "Lightweight hydraulic disc brake hybrid bike. Super smooth on city pavement and gentle trails. Equipped with Kryptonite U-lock, phone mount, and rear rack.",
                    photoDrawableName = "bike_sirrus",
                    locationArea = "Mission Dolores, 18th & Guerrero",
                    distanceMiles = 0.4,
                    lat = 37.7599,
                    lng = -122.4271,
                    condition = "Like New",
                    pricePerHour = 150.0,
                    pricePerDay = 750.0,
                    securityDeposit = 1000.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "8:00 PM",
                    rules = "Please lock to fixed bike racks only. Return cleaned. Helmet provided upon request."
                ),
                VehicleEntity(
                    id = 2,
                    ownerId = "owner_elena",
                    ownerName = "Elena Chen",
                    ownerAvatar = "EC",
                    ownerRating = 4.85f,
                    ownerVerification = "Verified ID, Phone & Bank",
                    type = "E_BIKE",
                    brand = "Rad Power",
                    model = "RadCity 5 Plus",
                    year = 2024,
                    description = "Powerful electric commuter bike with 45-mile battery range, pedal assist up to 20 mph, integrated front/rear LED lights, front suspension fork, and sturdy basket.",
                    photoDrawableName = "ebike_radcity",
                    locationArea = "Valencia Corridor, 21st St",
                    distanceMiles = 0.7,
                    lat = 37.7570,
                    lng = -122.4215,
                    condition = "Excellent",
                    pricePerHour = 250.0,
                    pricePerDay = 1400.0,
                    securityDeposit = 2000.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "10:00 PM",
                    rules = "Always lock with both frame and cable locks. Return battery at >=30% charge."
                ),
                VehicleEntity(
                    id = 3,
                    ownerId = "owner_marcus",
                    ownerName = "Marcus Vance",
                    ownerAvatar = "MV",
                    ownerRating = 5.0f,
                    ownerVerification = "Verified Superhost",
                    type = "SCOOTER",
                    brand = "Segway Ninebot",
                    model = "Max G2 Electric",
                    year = 2024,
                    description = "High-comfort electric scooter featuring hydraulic rear suspension, self-sealing tubeless tires, built-in turn signals, and Apple FindMy tracking.",
                    photoDrawableName = "scooter_ninebot",
                    locationArea = "16th & Mission Bart Plaza",
                    distanceMiles = 0.3,
                    lat = 37.7648,
                    lng = -122.4197,
                    condition = "Like New",
                    pricePerHour = 180.0,
                    pricePerDay = 900.0,
                    securityDeposit = 1500.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "11:30 PM",
                    rules = "No sidewalk riding where bike lanes exist. Max rider weight 265 lbs."
                ),
                VehicleEntity(
                    id = 4,
                    ownerId = "owner_sarah",
                    ownerName = "Sarah Jenkins",
                    ownerAvatar = "SJ",
                    ownerRating = 4.95f,
                    ownerVerification = "Verified Owner & Insurance",
                    type = "CAR",
                    brand = "Tesla",
                    model = "Model 3 Highland",
                    year = 2024,
                    description = "All-new refreshed Model 3 with ventilated seats, rear display, 300+ mile range, premium audio, and Autopilot. Valid driver's license required.",
                    photoDrawableName = "car_tesla",
                    locationArea = "Castro & Market St",
                    distanceMiles = 1.2,
                    lat = 37.7627,
                    lng = -122.4350,
                    condition = "Like New",
                    pricePerHour = 1200.0,
                    pricePerDay = 6500.0,
                    securityDeposit = 8000.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "9:00 PM",
                    rules = "Valid driver's license (21+). No smoking or pets. Return with at least 50% battery.",
                    isCarLicenseRequired = true
                ),
                VehicleEntity(
                    id = 5,
                    ownerId = "owner_david",
                    ownerName = "David Kim",
                    ownerAvatar = "DK",
                    ownerRating = 4.75f,
                    ownerVerification = "ID Verified",
                    type = "BIKE",
                    brand = "Trek",
                    model = "FX 3 Disc Road/City",
                    year = 2023,
                    description = "Fast, athletic city fitness bicycle with carbon fork and Shimano 1x10 drivetrain. Ideal for hill climbing and quick city commutes.",
                    photoDrawableName = "bike_trek",
                    locationArea = "Duboce Triangle & Noe",
                    distanceMiles = 0.6,
                    lat = 37.7680,
                    lng = -122.4330,
                    condition = "Good",
                    pricePerHour = 160.0,
                    pricePerDay = 800.0,
                    securityDeposit = 1200.0,
                    status = "AVAILABLE_LATER",
                    availableUntil = "Available from 2:00 PM",
                    rules = "Lock securely. Rain gear and helmet included on request."
                ),
                VehicleEntity(
                    id = 6,
                    ownerId = "owner_chloe",
                    ownerName = "Chloe Dubois",
                    ownerAvatar = "CD",
                    ownerRating = 4.9f,
                    ownerVerification = "ID & Phone Verified",
                    type = "E_BIKE",
                    brand = "VanMoof",
                    model = "S5 Smart E-Bike",
                    year = 2024,
                    description = "Futuristic connected electric bicycle with automatic electronic e-shifter, Halo ring handlebar display, boost button, and GSM anti-theft tracking.",
                    photoDrawableName = "ebike_vanmoof",
                    locationArea = "Hayes Valley, Octavia & Hayes",
                    distanceMiles = 1.0,
                    lat = 37.7765,
                    lng = -122.4243,
                    condition = "Like New",
                    pricePerHour = 300.0,
                    pricePerDay = 1800.0,
                    securityDeposit = 3000.0,
                    status = "RENTED",
                    availableUntil = "Available tomorrow 9:00 AM",
                    rules = "Keep unlocked only while riding. App unlocked via RideNear Bluetooth key."
                ),
                VehicleEntity(
                    id = 7,
                    ownerId = "owner_jordan",
                    ownerName = "Jordan Taylor",
                    ownerAvatar = "JT",
                    ownerRating = 4.88f,
                    ownerVerification = "Verified ID",
                    type = "SCOOTER",
                    brand = "Unagi",
                    model = "Model One Voyager",
                    year = 2024,
                    description = "Ultra-stylish dual motor 1000W peak e-scooter with carbon fiber tube and magnesium alloy deck. Easily climbs San Francisco hills.",
                    photoDrawableName = "scooter_unagi",
                    locationArea = "SoMa, 4th & Folsom",
                    distanceMiles = 0.9,
                    lat = 37.7816,
                    lng = -122.4005,
                    condition = "Excellent",
                    pricePerHour = 220.0,
                    pricePerDay = 1100.0,
                    securityDeposit = 1800.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "10:30 PM",
                    rules = "Do not ride in heavy rain. Helmet required."
                ),
                VehicleEntity(
                    id = 8,
                    ownerId = "owner_liam",
                    ownerName = "Liam Patel",
                    ownerAvatar = "LP",
                    ownerRating = 4.92f,
                    ownerVerification = "Verified Owner & Insurance",
                    type = "CAR",
                    brand = "Mini",
                    model = "Cooper SE Electric",
                    year = 2023,
                    description = "Compact, nimble electric car with iconic British racing styling, quick steering, Apple CarPlay, and panoramic moonroof. Easy to park in tight spots.",
                    photoDrawableName = "car_mini",
                    locationArea = "Potrero Hill, 18th & Connecticut",
                    distanceMiles = 1.4,
                    lat = 37.7635,
                    lng = -122.3980,
                    condition = "Like New",
                    pricePerHour = 950.0,
                    pricePerDay = 5200.0,
                    securityDeposit = 6000.0,
                    status = "AVAILABLE_NOW",
                    availableUntil = "11:00 PM",
                    rules = "Valid driver's license required. No smoking. Return with 70%+ battery or recharge fee applies.",
                    isCarLicenseRequired = true
                )
            )
            dao.insertVehicles(sampleVehicles)

            // Sample bookings
            val sampleBookings = listOf(
                BookingEntity(
                    id = 101,
                    vehicleId = 1,
                    vehicleName = "Specialized Sirrus X 3.0",
                    vehicleType = "BIKE",
                    vehiclePhoto = "bike_sirrus",
                    ownerId = "owner_alex",
                    ownerName = "Alex Rivera",
                    renterId = "user_me",
                    renterName = "You (Renter)",
                    startDateTime = "Today, 2:00 PM",
                    endDateTime = "Today, 5:00 PM",
                    durationHours = 3,
                    hourlyRate = 150.0,
                    platformFee = 50.0,
                    deposit = 1000.0,
                    totalAmount = 1500.0,
                    status = "READY_FOR_PICKUP",
                    pickupInspectionDone = false,
                    returnInspectionDone = false,
                    pickupNotes = "Meet at corner of 18th & Dolores. Lock combination will be sent in chat.",
                    createdAt = System.currentTimeMillis() - 3600000
                ),
                BookingEntity(
                    id = 102,
                    vehicleId = 3,
                    vehicleName = "Segway Ninebot Max G2",
                    vehicleType = "SCOOTER",
                    vehiclePhoto = "scooter_ninebot",
                    ownerId = "owner_marcus",
                    ownerName = "Marcus Vance",
                    renterId = "user_me",
                    renterName = "You (Renter)",
                    startDateTime = "Yesterday, 10:00 AM",
                    endDateTime = "Yesterday, 1:00 PM",
                    durationHours = 3,
                    hourlyRate = 180.0,
                    platformFee = 50.0,
                    deposit = 1500.0,
                    totalAmount = 2090.0,
                    status = "COMPLETED",
                    pickupInspectionDone = true,
                    returnInspectionDone = true,
                    rating = 5.0f,
                    reviewText = "Amazing scooter, battery was at 100% and Marcus was super responsive! Smooth ride all around the city.",
                    createdAt = System.currentTimeMillis() - 86400000
                )
            )
            dao.insertBookings(sampleBookings)

            // Sample chat messages for vehicle #1
            val sampleChats = listOf(
                ChatMessageEntity(
                    vehicleId = 1,
                    senderId = "user_me",
                    senderName = "You",
                    isFromOwner = false,
                    messageText = "Hi Alex! Is the Specialized Sirrus bike still available for this afternoon?",
                    timestamp = System.currentTimeMillis() - 3400000
                ),
                ChatMessageEntity(
                    vehicleId = 1,
                    senderId = "owner_alex",
                    senderName = "Alex Rivera",
                    isFromOwner = true,
                    messageText = "Hey! Yes absolutely, just tuned the chain and brakes this morning. Ready whenever you are!",
                    timestamp = System.currentTimeMillis() - 3200000
                ),
                ChatMessageEntity(
                    vehicleId = 1,
                    senderId = "user_me",
                    senderName = "You",
                    isFromOwner = false,
                    messageText = "Where is the pickup point?",
                    timestamp = System.currentTimeMillis() - 3000000
                ),
                ChatMessageEntity(
                    vehicleId = 1,
                    senderId = "owner_alex",
                    senderName = "Alex Rivera",
                    isFromOwner = true,
                    messageText = "Right next to Dolores Park by 18th & Guerrero. Once booking is ready, we'll do the quick 2-minute photo checklist!",
                    timestamp = System.currentTimeMillis() - 2800000
                )
            )
            dao.insertChatMessages(sampleChats)

            // Seed sample completed inspection
            val sampleInspection = InspectionEntity(
                bookingId = 102,
                vehicleId = 3,
                stage = "RETURN",
                completedByRole = "RENTER",
                checklistSummary = "Tires OK, Brakes OK, Battery 68%, Lights tested, Zero scratches",
                issuesFound = "None. Clean return.",
                photoCount = 3,
                signatureConfirmed = true,
                timestamp = System.currentTimeMillis() - 82000000
            )
            dao.insertInspection(sampleInspection)
        }
    }
}
