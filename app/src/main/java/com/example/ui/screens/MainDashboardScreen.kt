package com.example.ui.screens

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
import com.example.ui.components.RideNearBottomBar
import com.example.ui.theme.DeepNavy
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.RideNearViewModel

@Composable
fun MainDashboardScreen(
    viewModel: RideNearViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val alertBanner by viewModel.alertBanner.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(alertBanner) {
        alertBanner?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.dismissAlert()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            RideNearBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.setNavTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.EXPLORE -> HomeScreen(viewModel = viewModel)
                NavTab.MAP -> MapScreen(viewModel = viewModel)
                NavTab.ACTIVITY -> ActivityScreen(viewModel = viewModel)
                NavTab.MESSAGES -> ChatListScreen(viewModel = viewModel)
                NavTab.OWNER_HUB -> OwnerHubScreen(viewModel = viewModel)
            }
        }
    }
}
