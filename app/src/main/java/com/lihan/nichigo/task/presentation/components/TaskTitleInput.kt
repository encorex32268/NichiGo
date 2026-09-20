package com.lihan.nichigo.task.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.core.presentation.util.UiText
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.NichiGoTheme
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextSecondary
import com.lihan.nichigo.ui.theme.TextTertiary

@Composable
fun TaskTitleInput(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: UiText? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.create_task_name_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.create_task_name_placeholder),
                    color = TextTertiary
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            minLines = 2,
            maxLines = 3,
            isError = titleError != null,
            trailingIcon = {
                if (title.isNotBlank()) {
                    IconButton(
                        onClick = { onTitleChange("") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.Close,
                            contentDescription = stringResource(R.string.common_clear),
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            supportingText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (titleError != null) {
                        Text(
                            text = titleError.asString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Box(modifier = Modifier.size(0.dp))
                    }
                    Text(
                        text = stringResource(R.string.create_task_char_counter, title.length),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (title.length >= 30) MaterialTheme.colorScheme.error else TextTertiary,
                        textAlign = TextAlign.End
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryDark,
                unfocusedBorderColor = BorderDivider,
                errorContainerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskTitleInputEmptyPreview() {
    NichiGoTheme {
        TaskTitleInput(
            title = "",
            onTitleChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskTitleInputFilledPreview() {
    NichiGoTheme {
        TaskTitleInput(
            title = "記 100 個日文單詞",
            onTitleChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskTitleInputErrorPreview() {
    NichiGoTheme {
        TaskTitleInput(
            title = "",
            onTitleChange = {},
            titleError = UiText.StringResource(R.string.create_task_title_required),
            modifier = Modifier.padding(16.dp)
        )
    }
}
