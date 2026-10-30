package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CategoryFilterChip
import com.example.ui.components.VehicleCard
import com.example.ui.theme.BrightGreen
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavyElevated
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricTeal
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SoftPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.RideNearViewModel
import com.example.ui.viewmodel.UserRole

@Composable
fun HomeScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    val vehicles by viewModel.filteredVehicles.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()
    val availableNowOnly by viewModel.filterAvailableNowOnly.collectAsStateWithLifecycle()
    val currentRole by viewModel.userRole.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Greeting and Role Switcher
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good morning, Alex",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrightGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Mission District • San Francisco",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Action pills: AI Assistant & Role switch
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gemini AI Studio Button
                        Box(
                            modifier = Modifier
                                .testTag("home_gemini_ai_btn")
                                .clip(RoundedCornerShape(20.dp))
                                .background(DarkNavyElevated)
                                .border(1.dp, SoftPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.navigateTo(AppScreen.AI_ASSISTANT) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini AI",
                                    tint = SoftPurple,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Hub",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Role switch pill
                        Box(
                            modifier = Modifier
                                .testTag("role_switch_pill")
                                .clip(RoundedCornerShape(20.dp))
                                .background(DarkNavyElevated)
                                .border(1.dp, ElectricTeal.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.setRole(
                                        if (currentRole == UserRole.RENTER) UserRole.OWNER else UserRole.RENTER
                                    )
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Switch Role",
                                    tint = ElectricTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentRole == UserRole.RENTER) "Renter" else "Owner",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Auth / Login pill
                        Box(
                            modifier = Modifier
                                .testTag("home_auth_btn")
                                .clip(RoundedCornerShape(20.dp))
                                .background(DarkNavyElevated)
                                .border(1.dp, Color(0xFF4285F4).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .clickable { viewModel.navigateTo(AppScreen.LOGIN) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Account / Login",
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sign In",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar with Voice Transcribe Icon
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text("Search bikes, cars, or scooters", color = Color(0xFF64748B), fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = ElectricTeal
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                                modifier = Modifier.testTag("search_voice_mic_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Dictate with Gemini",
                                    tint = ElectricTeal
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavyCard,
                        unfocusedContainerColor = DarkNavyCard,
                        focusedBorderColor = ElectricTeal,
                        unfocusedBorderColor = Color(0xFF273556),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // Available near you & View Toggle [Map] [List]
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available near you",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Map / List toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavyCard)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .testTag("view_toggle_list")
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricTeal)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ViewList,
                                    contentDescription = "List",
                                    tint = DeepNavy,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("List", fontSize = 11.sp, color = DeepNavy, fontWeight = FontWeight.Bold)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .testTag("view_toggle_map")
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.setNavTab(NavTab.MAP) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Map",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Map", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }

            // Category Chips Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryFilterChip(
                        label = "All",
                        icon = null,
                        isSelected = selectedType == "ALL",
                        onClick = { viewModel.setTypeFilter("ALL") }
                    )
                    CategoryFilterChip(
                        label = "Bike",
                        icon = Icons.Default.DirectionsBike,
                        isSelected = selectedType.equals("BIKE", ignoreCase = true),
                        onClick = { viewModel.setTypeFilter("BIKE") }
                    )
                    CategoryFilterChip(
                        label = "E-Bike",
                        icon = Icons.Default.ElectricBike,
                        isSelected = selectedType.equals("E_BIKE", ignoreCase = true),
                        onClick = { viewModel.setTypeFilter("E_BIKE") }
                    )
                    CategoryFilterChip(
                        label = "Scooter",
                        icon = Icons.Default.ElectricScooter,
                        isSelected = selectedType.equals("SCOOTER", ignoreCase = true),
                        onClick = { viewModel.setTypeFilter("SCOOTER") }
                    )
                    CategoryFilterChip(
                        label = "Car",
                        icon = Icons.Default.DirectionsCar,
                        isSelected = selectedType.equals("CAR", ignoreCase = true),
                        onClick = { viewModel.setTypeFilter("CAR") }
                    )
                }
            }

            // Quick Filter Pills (Available Now, Under $15, Distance)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Available Now toggle
                    Box(
                        modifier = Modifier
                            .testTag("filter_available_now")
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (availableNowOnly) BrightGreen.copy(alpha = 0.2f) else DarkNavyElevated)
                            .border(
                                1.dp,
                                if (availableNowOnly) BrightGreen else Color.Transparent,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.toggleAvailableNowOnly() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (availableNowOnly) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = BrightGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = "Available Now",
                                fontSize = 12.sp,
                                color = if (availableNowOnly) BrightGreen else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Price Cap Filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkNavyElevated)
                            .clickable {
                                viewModel.setMaxHourlyPrice(if (viewModel.maxHourlyPrice.value > 500.0) 500.0 else 5000.0)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (viewModel.maxHourlyPrice.value <= 500.0) "Under ₹500/hr ✓" else "Price Filter",
                            fontSize = 12.sp,
                            color = if (viewModel.maxHourlyPrice.value <= 500.0) ElectricTeal else Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Distance indicator
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkNavyElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "< 2.0 mi radius",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            // Results counter
            item {
                Text(
                    text = "Showing ${vehicles.size} rides near your location",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Vehicle Cards List
            if (vehicles.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No vehicles found matching filters",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try clearing your search or switching categories",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(vehicles, key = { it.id }) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onClick = { viewModel.selectVehicleForDetails(vehicle.id) },
                        onBookClick = { viewModel.openBookingCheckout(vehicle.id) },
                        onChatClick = { viewModel.openChatForVehicle(vehicle.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to List Vehicle if in owner mode or shortcut
        FloatingActionButton(
            onClick = {
                viewModel.setRole(UserRole.OWNER)
                viewModel.navigateTo(AppScreen.CREATE_LISTING)
            },
            containerColor = ElectricTeal,
            contentColor = DeepNavy,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .testTag("fab_list_vehicle")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "List Ride")
                Spacer(modifier = Modifier.width(4.dp))
                Text("List Ride", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
