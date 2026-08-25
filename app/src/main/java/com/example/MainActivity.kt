package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.screens.CrossComparisonScreen
import com.example.ui.screens.CustomRulesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegislationBrowserScreen
import com.example.ui.screens.SingleDocumentAuditScreen
import com.example.ui.screens.TenderProjectAuditScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(statusMessage) {
                    statusMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.setStatusMessage(null)
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val screenModifier = Modifier.padding(innerPadding)

                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.HOME -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.SINGLE_DOC_AUDIT -> SingleDocumentAuditScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.TENDER_PROJECT_AUDIT -> TenderProjectAuditScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.CROSS_COMPARE -> CrossComparisonScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.CUSTOM_RULES -> CustomRulesScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.LEGISLATION_BROWSER -> LegislationBrowserScreen(viewModel = viewModel, modifier = screenModifier)
                            AppScreen.REPORTS_HISTORY -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
                        }
                    }
                }
            }
        }
    }
}
