package com.lihan.nichigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import com.lihan.nichigo.navigation.AppBottomBar
import com.lihan.nichigo.navigation.TaskHomeRoute
import com.lihan.nichigo.navigation.rememberAppState
import com.lihan.nichigo.task.presentation.calendar.calendarScreen
import com.lihan.nichigo.task.presentation.create.createTaskScreen
import com.lihan.nichigo.task.presentation.create.navigateToCreateTask
import com.lihan.nichigo.task.presentation.home.taskScreen
import com.lihan.nichigo.ui.theme.NichiGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NichiGoTheme {
                NichiGoApp()
            }
        }
    }
}

@Composable
fun NichiGoApp() {
    val appState = rememberAppState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                AppBottomBar(
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = TaskHomeRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (appState.shouldShowBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            taskScreen(
                onNavigateToCreate = {
                    appState.navController.navigateToCreateTask()
                }
            )

            calendarScreen()

            createTaskScreen(
                onDismiss = {
                    appState.navController.popBackStack()
                }
            )
        }
    }
}