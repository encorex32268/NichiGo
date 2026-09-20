package com.lihan.nichigo.task.presentation.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lihan.nichigo.navigation.TaskHomeRoute

fun NavController.navigateToTaskHome(navOptions: NavOptions? = null) {
    navigate(route = TaskHomeRoute, navOptions = navOptions)
}

fun NavGraphBuilder.taskScreen(
    onNavigateToCreate: () -> Unit
) {
    composable<TaskHomeRoute> {
        TaskRoot(
            onNavigateToCreate = onNavigateToCreate
        )
    }
}
