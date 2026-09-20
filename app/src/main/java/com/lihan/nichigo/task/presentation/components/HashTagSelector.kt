package com.lihan.nichigo.task.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lihan.nichigo.R
import com.lihan.nichigo.core.presentation.designsystem.icon.AppIcons
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.ui.theme.BorderDivider
import com.lihan.nichigo.ui.theme.PrimaryDark
import com.lihan.nichigo.ui.theme.TextPrimary
import com.lihan.nichigo.ui.theme.TextSecondary
import com.lihan.nichigo.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashTagSelector(
    availableTags: List<HashTag>,
    selectedTags: Set<HashTag>,
    newTagInput: String,
    onToggleTag: (HashTag) -> Unit,
    onNewTagInputChange: (String) -> Unit,
    onAddNewTag: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val cleanInput = remember(newTagInput) {
        newTagInput.trim().removePrefix("#")
    }

    val matchingTags = remember(availableTags, cleanInput) {
        if (cleanInput.isBlank()) {
            emptyList()
        } else {
            availableTags.filter { it.title.contains(cleanInput, ignoreCase = true) }
        }
    }

    val hasExactMatch = remember(availableTags, cleanInput) {
        availableTags.any { it.title.equals(cleanInput, ignoreCase = true) }
    }

    val unselectedAvailableTags = remember(availableTags, selectedTags) {
        availableTags.filter { available -> selectedTags.none { it.id == available.id } }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.create_task_hashtag_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        // Currently Selected Tags
        if (selectedTags.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedTags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryDark)
                            .border(
                                width = 1.dp,
                                color = PrimaryDark,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onToggleTag(tag) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "#${tag.title}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = AppIcons.Close,
                                contentDescription = stringResource(R.string.common_clear),
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search / Add Tag Input Field
        OutlinedTextField(
            value = newTagInput,
            onValueChange = onNewTagInputChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.create_task_search_tag_placeholder),
                    color = TextTertiary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (cleanInput.isNotBlank()) {
                    IconButton(
                        onClick = { onNewTagInputChange("") },
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
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (cleanInput.isNotBlank()) {
                        onAddNewTag()
                    }
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryDark,
                unfocusedBorderColor = BorderDivider
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Tag Suggestions or Available Tags
        if (cleanInput.isNotBlank()) {
            // Filtered results while searching
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                matchingTags.forEach { tag ->
                    val isSelected = selectedTags.any { it.id == tag.id }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryDark else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryDark else BorderDivider,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onToggleTag(tag)
                                onNewTagInputChange("")
                                focusManager.clearFocus()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#${tag.title}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }

                // Show "+ 新增「#...」" if no exact match
                if (!hasExactMatch) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = BorderDivider,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onAddNewTag()
                                focusManager.clearFocus()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.create_task_add_tag_fmt, cleanInput),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                    }
                }
            }
        } else if (unselectedAvailableTags.isNotEmpty()) {
            // When input is blank: show available existing tags for fast 1-tap selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.create_task_available_tags),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextTertiary,
                    fontWeight = FontWeight.Medium
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unselectedAvailableTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(
                                    width = 1.dp,
                                    color = BorderDivider,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onToggleTag(tag)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "#${tag.title}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Normal,
                                color = TextPrimary
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
private fun HashTagSelectorPreview() {
    com.lihan.nichigo.ui.theme.NichiGoTheme {
        HashTagSelector(
            availableTags = listOf(
                HashTag(1, "語言學習", 0xFF2563EB),
                HashTag(2, "核心詞彙", 0xFF059669),
                HashTag(3, "健身運動", 0xFFD97706)
            ),
            selectedTags = setOf(
                HashTag(1, "語言學習", 0xFF2563EB)
            ),
            newTagInput = "",
            onToggleTag = {},
            onNewTagInputChange = {},
            onAddNewTag = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

