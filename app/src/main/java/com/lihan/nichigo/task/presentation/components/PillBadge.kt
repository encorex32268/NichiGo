package com.lihan.nichigo.task.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.nichigo.ui.theme.NichiGoTheme

@Composable
fun PillBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF1F5F9),
    contentColor: Color = Color(0xFF475569),
    isFullRounded: Boolean = true
) {
    val shape = if (isFullRounded) CircleShape else RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = contentColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PillBadgePreview() {
    NichiGoTheme {
        PillBadge(text = "每日")
    }
}
