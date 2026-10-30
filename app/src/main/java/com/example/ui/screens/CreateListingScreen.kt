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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.ui.components.getVehicleIcon
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.RideNearViewModel

@Composable
fun CreateListingScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1: Type & Photos, 2: Specs & Price, 3: Safety & Publish

    var vehicleType by remember { mutableStateOf("BIKE") }
    var brand by remember { mutableStateOf("Cannondale") }
    var model by remember { mutableStateOf("Quick 4 Disc") }
    var year by remember { mutableStateOf("2024") }
    var condition by remember { mutableStateOf("Like New") }
    var description by remember { mutableStateOf("Fast and nimble city hybrid bike with puncture-resistant tires, front rack, and high-security U-lock included. Perfect for exploring the neighborhood!") }

    var hourlyPrice by remember { mutableStateOf("150") }
    var dailyPrice by remember { mutableStateOf("750") }
    var securityDeposit by remember { mutableStateOf("1000") }
    var locationArea by remember { mutableStateOf("Mission Dolores Park, 19th & Church") }
    var rules by remember { mutableStateOf("Please lock with included U-lock to designated bike racks. Helmet provided upon request.") }

    val safetyChecks = remember {
        mutableStateMapOf(
            "Tires pressurized & tread inspected" to true,
            "Brakes responsiveness tested" to true,
            "Chain / Battery / Motor checked" to true,
            "Safety bell / Horn audible" to true,
            "High-security lock included" to true
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (step > 1) step-- else viewModel.navigateTo(AppScreen.MAIN_NAV)
                    },
                    modifier = Modifier.testTag("create_listing_back_btn")
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
                        text = "List My Vehicle",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Step $step of 3 • ${if (step == 1) "Category & Photos" else if (step == 2) "Pricing & Location" else "Safety Checklist"}",
                        fontSize = 12.sp,
                        color = ElectricTeal
                    )
                }
            }

            // Progress indicator bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i <= step) ElectricTeal else DarkNavyElevated)
                    )
                }
            }

            // Form Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (step == 1) {
                    // Step 1: Vehicle Type & Photos & Specs
                    Text(
                        text = "What type of ride are you sharing?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("BIKE", "Bike", Icons.Default.DirectionsBike),
                            Triple("E_BIKE", "E-Bike", Icons.Default.ElectricBike),
                            Triple("SCOOTER", "Scooter", Icons.Default.ElectricScooter),
                            Triple("CAR", "Car", Icons.Default.DirectionsCar)
                        ).forEach { (typeKey, label, icon) ->
                            val isSel = vehicleType == typeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) ElectricTeal else DarkNavyCard)
                                    .clickable {
                                        vehicleType = typeKey
                                        if (typeKey == "CAR") {
                                            brand = "Tesla"
                                            model = "Model Y"
                                            hourlyPrice = "28"
                                            dailyPrice = "125"
                                            securityDeposit = "250"
                                        } else if (typeKey == "SCOOTER") {
                                            brand = "Segway"
                                            model = "Ninebot F2 Pro"
                                            hourlyPrice = "9"
                                            dailyPrice = "45"
                                            securityDeposit = "50"
                                        } else if (typeKey == "E_BIKE") {
                                            brand = "Specialized"
                                            model = "Turbo Vado 4.0"
                                            hourlyPrice = "15"
                                            dailyPrice = "70"
                                            securityDeposit = "85"
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSel) DeepNavy else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) DeepNavy else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Photo Upload Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Vehicle Photos",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkNavyElevated)
                                        .border(1.dp, ElectricTeal, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrightGreen)
                                        Text("Cover Photo ✓", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkNavyElevated)
                                        .border(1.dp, Color(0xFF273556), RoundedCornerShape(10.dp))
                                        .clickable { viewModel.showAlert("Side photo added!") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = ElectricTeal)
                                        Text("+ Add Side", fontSize = 11.sp, color = ElectricTeal)
                                    }
                                }
                            }
                        }
                    }

                    // Brand & Model Inputs
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand / Make", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().testTag("listing_brand_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )

                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        label = { Text("Model", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().testTag("listing_model_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = year,
                            onValueChange = { year = it },
                            label = { Text("Year", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                        OutlinedTextField(
                            value = condition,
                            onValueChange = { condition = it },
                            label = { Text("Condition", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Features", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                } else if (step == 2) {
                    // Step 2: Pricing & Location & Rules
                    Text(
                        text = "Set your rental pricing & location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = hourlyPrice,
                            onValueChange = { hourlyPrice = it },
                            label = { Text("Price/Hour (₹)", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f).testTag("listing_hourly_price_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                        OutlinedTextField(
                            value = dailyPrice,
                            onValueChange = { dailyPrice = it },
                            label = { Text("Price/Day (₹)", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                    }

                    OutlinedTextField(
                        value = securityDeposit,
                        onValueChange = { securityDeposit = it },
                        label = { Text("Security Deposit (₹ refundable)", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )

                    OutlinedTextField(
                        value = locationArea,
                        onValueChange = { locationArea = it },
                        label = { Text("Pickup Location Area / Neighborhood", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().testTag("listing_location_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )

                    OutlinedTextField(
                        value = rules,
                        onValueChange = { rules = it },
                        label = { Text("Renter Rules & Guidelines", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth().height(90.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                } else {
                    // Step 3: Safety Checklist & Publish
                    Text(
                        text = "Owner Pre-Publish Safety Verification",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyCard),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Please confirm the safety status of your vehicle:",
                                fontSize = 13.sp,
                                color = Color(0xFFCBD5E1)
                            )

                            safetyChecks.keys.forEach { checkItem ->
                                val isChecked = safetyChecks[checkItem] == true
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { safetyChecks[checkItem] = !isChecked }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { safetyChecks[checkItem] = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = BrightGreen,
                                            checkmarkColor = DeepNavy
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = checkItem,
                                        fontSize = 13.sp,
                                        color = if (isChecked) Color.White else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    // Summary Preview Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkNavyElevated),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Listing Preview", fontWeight = FontWeight.Bold, color = ElectricTeal, fontSize = 14.sp)
                            Text("$brand $model ($vehicleType)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("₹$hourlyPrice/hr • ₹$dailyPrice/day • ₹$securityDeposit deposit", color = BrightGreen, fontSize = 13.sp)
                            Text("Pickup: $locationArea", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Bottom Continue / Publish Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkNavySurface)
                    .border(1.dp, Color(0xFF1E2B4D))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            viewModel.createVehicleListing(
                                type = vehicleType,
                                brand = brand,
                                model = model,
                                year = year.toIntOrNull() ?: 2024,
                                description = description,
                                locationArea = locationArea,
                                pricePerHour = hourlyPrice.toDoubleOrNull() ?: 8.0,
                                pricePerDay = dailyPrice.toDoubleOrNull() ?: 40.0,
                                securityDeposit = securityDeposit.toDoubleOrNull() ?: 50.0,
                                rules = rules,
                                condition = condition
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("create_listing_next_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricTeal,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (step < 3) "Continue to Step ${step + 1}" else "Publish My Vehicle Listing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkNavyCard,
    unfocusedContainerColor = DarkNavyCard,
    focusedBorderColor = ElectricTeal,
    unfocusedBorderColor = Color(0xFF273556),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
