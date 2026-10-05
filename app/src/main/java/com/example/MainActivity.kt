package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.FirstTimeGuideDialog
import com.example.ui.components.TomatScanBottomBar
import com.example.ui.components.TomatScanHeader
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.DiseaseDetailScreen
import com.example.ui.screens.DiseaseListScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PermissionDeniedScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.TransparencyScreen
import com.example.ui.theme.Kertas
import com.example.ui.theme.TomatScanTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.NavTab
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TomatScanTheme {
                TomatScanApp()
            }
        }
    }
}

@Composable
fun TomatScanApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isFirstTimeUser by viewModel.isFirstTimeUser.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    // Handle Android system back button
    BackHandler(enabled = currentScreen != ScreenState.MAIN_TABS) {
        viewModel.navigateBack()
    }

    if (isFirstTimeUser) {
        FirstTimeGuideDialog(
            onDismiss = { viewModel.dismissFirstTime() }
        )
    }

    when (currentScreen) {
        ScreenState.CAMERA -> {
            CameraScanScreen(
                viewModel = viewModel,
                onClose = { viewModel.closeCamera() },
                onPermissionDenied = { viewModel.openPermissionDenied() }
            )
        }

        ScreenState.RESULT -> {
            ResultScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onOpenDiseaseGuide = { diseaseId -> viewModel.openDiseaseDetail(diseaseId) },
                onOpenTransparency = { record -> viewModel.openTransparency(record) }
            )
        }

        ScreenState.TRANSPARENCY -> {
            TransparencyScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.DISEASE_DETAIL -> {
            DiseaseDetailScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() },
                onScanClick = { viewModel.openCamera() }
            )
        }

        ScreenState.ABOUT -> {
            AboutScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.PERMISSION_DENIED -> {
            PermissionDeniedScreen(
                viewModel = viewModel,
                onBack = { viewModel.navigateBack() }
            )
        }

        ScreenState.MAIN_TABS -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Kertas),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    val subTitle = when (currentTab) {
                        NavTab.BERANDA -> "Beranda"
                        NavTab.RIWAYAT -> "Riwayat"
                        NavTab.INFO -> "Info Penyakit"
                    }
                    TomatScanHeader(
                        title = "TomatScan",
                        subtitle = subTitle,
                        showBack = false,
                        showAboutAction = true,
                        onAboutClick = { viewModel.openAbout() }
                    )
                },
                bottomBar = {
                    TomatScanBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { tab -> viewModel.setTab(tab) },
                        onScanClick = { viewModel.openCamera() }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Kertas)
                ) {
                    when (currentTab) {
                        NavTab.BERANDA -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToCamera = { viewModel.openCamera() },
                                onOpenRecord = { record -> viewModel.openRecord(record) }
                            )
                        }

                        NavTab.RIWAYAT -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onOpenRecord = { record -> viewModel.openRecord(record) },
                                onScanClick = { viewModel.openCamera() }
                            )
                        }

                        NavTab.INFO -> {
                            DiseaseListScreen(
                                onSelectDisease = { diseaseId -> viewModel.openDiseaseDetail(diseaseId) }
                            )
                        }
                    }
                }
            }
        }
    }
}
