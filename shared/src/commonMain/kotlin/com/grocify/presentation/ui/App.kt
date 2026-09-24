package com.grocify.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.grocify.presentation.theme.GrocifyTheme
import com.grocify.presentation.ui.screens.InsightsScreen
import com.grocify.presentation.ui.screens.ListScreen
import com.grocify.presentation.ui.screens.PlannerScreen
import com.grocify.presentation.ui.screens.SignInScreen
import com.grocify.presentation.viewmodel.GroceryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    GrocifyTheme {
        val viewModel: GroceryViewModel = koinViewModel()
        GrocifyRoot(viewModel = viewModel)
    }
}

@Composable
private fun GrocifyRoot(viewModel: GroceryViewModel) {
    val state by viewModel.uiState.collectAsState()

    if (!state.isSignedIn) {
        SignInScreen(
            onContinue = { viewModel.refreshAuthAndLoad() },
        )
        return
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "List") },
                    label = { Text("List") },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Planner") },
                    label = { Text("Planner") },
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Insights") },
                    label = { Text("Insights") },
                )
            }
        },
    ) { padding ->
        when (selectedTab) {
            0 -> ListScreen(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
            )
            1 -> PlannerScreen(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
            )
            else -> InsightsScreen(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
            )
        }
    }
}
