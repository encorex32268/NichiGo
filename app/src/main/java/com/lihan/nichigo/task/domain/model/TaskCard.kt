package com.lihan.nichigo.task.domain.model

data class TaskCard(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: Long = 0L,
    val colorTheme: Long,
    val period: Period,
    val hashTags: List<HashTag>
)
