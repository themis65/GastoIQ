package com.gastoiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.gastoiq.app.ui.navigation.NavGraph
import com.gastoiq.app.ui.screens.addexpense.AddExpenseViewModel
import com.gastoiq.app.ui.screens.analytics.AnalyticsViewModel
import com.gastoiq.app.ui.screens.dashboard.DashboardViewModel
import com.gastoiq.app.ui.screens.history.HistoryViewModel
import com.gastoiq.app.ui.theme.GastoIQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GastoIQTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    dashboardViewModel = viewModel(),
                    addExpenseViewModel = viewModel(),
                    historyViewModel = viewModel(),
                    analyticsViewModel = viewModel(),
                )
            }
        }
    }
}
