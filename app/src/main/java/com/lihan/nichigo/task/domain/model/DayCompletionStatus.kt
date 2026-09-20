package com.lihan.nichigo.task.domain.model

enum class DayCompletionStatus {
    NONE,           // 無安排任務
    ALL_COMPLETED,  // 全部完成 (綠點)
    PARTIAL,        // 部分完成 (藍點)
    NOT_COMPLETED   // 未完成 (灰點)
}
