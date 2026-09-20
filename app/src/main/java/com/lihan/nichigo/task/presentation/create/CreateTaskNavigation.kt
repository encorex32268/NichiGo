package com.lihan.nichigo.task.presentation.create

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lihan.nichigo.navigation.CreateTaskRoute

fun NavController.navigateToCreateTask(navOptions: NavOptions? = null) {
    navigate(route = CreateTaskRoute, navOptions = navOptions)
}

fun NavGraphBuilder.createTaskScreen(
    onDismiss: () -> Unit
) {
    composable<CreateTaskRoute> {
        CreateTaskRoot(
            onDismiss = onDismiss
        )
    }
}
