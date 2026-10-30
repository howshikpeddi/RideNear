package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.BookingCheckoutScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CreateListingScreen
import com.example.ui.screens.InspectionScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.VehicleDetailsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.RideNearViewModel
import com.example.ui.viewmodel.UserAuthViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RideNearViewModel by viewModels()
    private val authViewModel: UserAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val alertBanner by viewModel.alertBanner.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(alertBanner) {
                    alertBanner?.let { message ->
                        snackbarHostState.showSnackbar(message)
                        viewModel.dismissAlert()
                    }
                }

                // Handle back press
                BackHandler(enabled = currentScreen != AppScreen.WELCOME && currentScreen != AppScreen.MAIN_NAV) {
                    when (currentScreen) {
                        AppScreen.BOOKING_CHECKOUT -> viewModel.navigateTo(AppScreen.VEHICLE_DETAILS)
                        AppScreen.INSPECTION,
                        AppScreen.CHAT,
                        AppScreen.CREATE_LISTING,
                        AppScreen.VEHICLE_DETAILS,
                        AppScreen.AI_ASSISTANT,
                        AppScreen.VERIFICATION_CENTER -> viewModel.navigateTo(AppScreen.MAIN_NAV)
                        AppScreen.LOGIN -> viewModel.navigateTo(AppScreen.WELCOME)
                        else -> viewModel.navigateTo(AppScreen.WELCOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepNavy,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(DeepNavy)
                    ) {
                        when (currentScreen) {
                            AppScreen.WELCOME -> WelcomeScreen(viewModel = viewModel)
                            AppScreen.LOGIN -> LoginScreen(
                                authViewModel = authViewModel,
                                onNavigateBack = { viewModel.navigateTo(AppScreen.WELCOME) },
                                onLoginSuccess = { viewModel.navigateTo(AppScreen.MAIN_NAV) }
                            )
                            AppScreen.MAIN_NAV -> MainDashboardScreen(viewModel = viewModel)
                            AppScreen.VEHICLE_DETAILS -> VehicleDetailsScreen(viewModel = viewModel)
                            AppScreen.CREATE_LISTING -> CreateListingScreen(viewModel = viewModel)
                            AppScreen.BOOKING_CHECKOUT -> BookingCheckoutScreen(viewModel = viewModel)
                            AppScreen.INSPECTION -> InspectionScreen(viewModel = viewModel)
                            AppScreen.CHAT -> ChatScreen(viewModel = viewModel)
                            AppScreen.AI_ASSISTANT -> AiStudioScreen(viewModel = viewModel)
                            AppScreen.VERIFICATION_CENTER -> MainDashboardScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
