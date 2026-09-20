package com.lihan.nichigo.task.data.database.mapper

import com.lihan.nichigo.task.data.database.entity.HashTagEntity
import com.lihan.nichigo.task.data.database.entity.PeriodEntity
import com.lihan.nichigo.task.data.database.entity.TaskWithDetails
import com.lihan.nichigo.task.domain.model.HashTag
import com.lihan.nichigo.task.domain.model.Period
import com.lihan.nichigo.task.domain.model.PeriodType
import com.lihan.nichigo.task.domain.model.TaskCard

fun PeriodEntity.toDomain(): Period {
    val days = if (daysOfWeekCsv.isBlank()) {
        emptyList()
    } else {
        daysOfWeekCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
    val pType = try {
        PeriodType.valueOf(type)
    } catch (_: Exception) {
        PeriodType.DAILY
    }
    return Period(
        id = id,
        type = pType,
        intervalDays = intervalDays,
        daysOfWeek = days,
        displayTitle = displayTitle
    )
}

fun Period.toEntity(): PeriodEntity {
    return PeriodEntity(
        id = id,
        type = type.name,
        intervalDays = intervalDays,
        daysOfWeekCsv = daysOfWeek.joinToString(","),
        displayTitle = displayTitle
    )
}

fun HashTagEntity.toDomain(): HashTag = HashTag(id = id, title = title, color = color)

fun HashTag.toEntity(): HashTagEntity = HashTagEntity(id = id, title = title, color = color)

fun TaskWithDetails.toTaskCard(isCompletedOnDate: Boolean): TaskCard {
    val domainPeriod = period?.toDomain() ?: Period(displayTitle = "每日")
    return TaskCard(
        id = task.id,
        title = task.title,
        isCompleted = isCompletedOnDate,
        createdAt = task.createdAt,
        colorTheme = task.colorTheme,
        period = domainPeriod,
        hashTags = hashTags.map { it.toDomain() }
    )
}
