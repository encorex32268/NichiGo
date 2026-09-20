package com.lihan.nichigo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextMuted

@Composable
fun AppBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.96f),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDivider.copy(alpha = 0.6f)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(58.dp)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            destinations.forEach { destination ->
                val isSelected = currentDestination?.hasRoute(destination.route) == true
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavigateToDestination(destination) }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label.asString(),
                        tint = if (isSelected) PrimaryDark else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(PrimaryDark)
                        )
                    }
                }
            }
        }
    }
}
