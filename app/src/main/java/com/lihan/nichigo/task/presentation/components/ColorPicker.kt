package com.lihan.nichigo.task.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.ui.theme.CheckboxBorder
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    selectedColor: Long,
    availableColors: List<Long>,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.create_task_color_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availableColors.forEach { colorLong ->
                val isSelected = selectedColor == colorLong
                val borderWidth by animateDpAsState(
                    targetValue = if (isSelected) 3.dp else 1.dp,
                    label = "ColorBorderWidth"
                )
                val circleSize by animateDpAsState(
                    targetValue = if (isSelected) 46.dp else 42.dp,
                    label = "ColorCircleSize"
                )

                Box(
                    modifier = Modifier
                        .size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(Color(colorLong))
                            .border(
                                width = borderWidth,
                                color = if (isSelected) PrimaryDark else CheckboxBorder,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(colorLong) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = AppIcons.Check,
                                contentDescription = null,
                                tint = PrimaryDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun ColorPickerPreview() {
    com.lihan.nichigo.ui.theme.NichiGoTheme {
        ColorPicker(
            selectedColor = com.lihan.nichigo.ui.theme.MacaronPalette.first(),
            availableColors = com.lihan.nichigo.ui.theme.MacaronPalette,
            onColorSelected = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

