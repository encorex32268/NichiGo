package com.lihan.nichigo.ui.theme

import androidx.compose.ui.graphics.Color
import com.lihan.nichigo.task.domain.model.PeriodType

// Stitch Timeless Habit Tracker Palette
val AppBg = Color(0xFFF8F9FC)
val CardBg = Color(0xFFFFFFFF)
val TextDark = Color(0xFF18181B)
val TextPrimary = Color(0xFF1E293B)
val TextSub = Color(0xFF71717A)
val TextSecondary = Color(0xFF64748B)
val TextTertiary = Color(0xFF94A3B8)
val TextMuted = Color(0xFFA1A1AA)
val BorderSubtle = Color(0xFFF4F4F5)
val BorderDivider = Color(0xFFE4E4E7)
val CheckboxBorder = Color(0xFFCBD5E1)

// Brand Accents
val BrandEmerald = Color(0xFF059669)
val BrandEmeraldLight = Color(0xFF10B981)
val BrandEmeraldBg = Color(0xFFD1FAE5)
val BrandBlue = Color(0xFF3B82F6)
val BrandBlueBg = Color(0xFFEFF4FF)
val ProgressBlue = Color(0xFF2563EB)
val ProgressTrack = Color(0xFFE2E8F0)

// Hashtags & Tags
val TagGray = Color(0xFF64748B)

// Period Pill Themed Colors
val PeriodDailyBg = Color(0xFFD1FAE5)
val PeriodDailyText = Color(0xFF065F46)
val PeriodIntervalBg = Color(0xFFDBEAFE)
val PeriodIntervalText = Color(0xFF1E40AF)
val PeriodWeeklyBg = Color(0xFFF3E8FF)
val PeriodWeeklyText = Color(0xFF6B21A8)
val PeriodDefaultBg = Color(0xFFF1F5F9)
val PeriodDefaultText = Color(0xFF334155)

fun periodPillColors(periodType: PeriodType): Pair<Color, Color> = when (periodType) {
    PeriodType.DAILY -> PeriodDailyBg to PeriodDailyText
    PeriodType.INTERVAL -> PeriodIntervalBg to PeriodIntervalText
    PeriodType.WEEKLY -> PeriodWeeklyBg to PeriodWeeklyText
}

// Pastel Card Themes & Macaron Palette (馬卡龍色調)
val PastelLavender = Color(0xFFF3E8FF)
val PastelYellow = Color(0xFFFEF3C7)
val PastelMint = Color(0xFFD1FAE5)
val PastelSky = Color(0xFFE0F2FE)
val PastelBlue = Color(0xFFEFF4FF)

val MacaronPalette: List<Long> = listOf(
    0xFFE6F8F3, // 薄荷綠 (Mint)
    0xFFE0F2FE, // 澄空藍 (Sky)
    0xFFEFF6FF, // 冰河藍 (Ice)
    0xFFFEF3C7, // 奶油黃 (Cream)
    0xFFFFEDD5, // 杏桃橘 (Peach)
    0xFFFFE4E6, // 櫻花粉 (Rose)
    0xFFFCE7F3, // 甜心粉 (Candy)
    0xFFF3E8FF, // 薰衣草紫 (Lavender)
    0xFFF5F3FF, // 丁香紫 (Lilac)
    0xFFECFCCB  // 抹茶綠 (Matcha)
)

// Material Theme Mappings
val PrimaryDark = Color(0xFF18181B)
val SecondaryDark = Color(0xFF27272A)
val TertiaryAccent = Color(0xFF059669)