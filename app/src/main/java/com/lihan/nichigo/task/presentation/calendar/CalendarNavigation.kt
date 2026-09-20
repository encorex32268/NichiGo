package com.lihan.nichigo.task.presentation.calendar

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.lihan.nichigo.navigation.CalendarRoute

fun NavController.navigateToCalendar(navOptions: NavOptions? = null) {
    navigate(route = CalendarRoute, navOptions = navOptions)
}

fun NavGraphBuilder.calendarScreen() {
    composable<CalendarRoute> {
        CalendarRoot()
    }
}
