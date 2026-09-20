package com.lihan.nichigo.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.core.presentation.util.UiText
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route: KClass<*>,
    val startRoute: Any,
    val icon: ImageVector,
    val label: UiText
) {
    TASK(
        route = TaskHomeRoute::class,
        startRoute = TaskHomeRoute,
        icon = AppIcons.Home,
        label = UiText.StringResource(R.string.nav_home)
    ),
    CALENDAR(
        route = CalendarRoute::class,
        startRoute = CalendarRoute,
        icon = AppIcons.Calendar,
        label = UiText.StringResource(R.string.nav_calendar)
    )
}
