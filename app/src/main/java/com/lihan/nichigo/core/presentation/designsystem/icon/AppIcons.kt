package com.lihan.nichigo.core.presentation.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized Design System Icon Object.
 * All application icons should be referenced from AppIcons rather than direct Material/Asset calls.
 */
object AppIcons {
    val Home: ImageVector = Icons.Default.Home
    val Calendar: ImageVector = Icons.Default.DateRange
    val Add: ImageVector = Icons.Default.Add
    val Check: ImageVector = Icons.Default.Check
    val Close: ImageVector = Icons.Default.Close
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowLeft: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft
    val ArrowRight: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val Search: ImageVector = Icons.Default.Search
}
