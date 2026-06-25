package dev.apercorn.koin.core.util

import kotlinx.datetime.*

object DateUtils {
	fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

	fun formatDisplay(date: LocalDate): String {
		val today = today()
		return when (date) {
			today -> "Today"
			today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
			today.plus(1, DateTimeUnit.DAY) -> "Tomorrow"
			else -> "${
				date.month.name.lowercase().replaceFirstChar { it.uppercase() }
			} ${date.dayOfMonth}, ${date.year}"
		}
	}

	fun formatShort(date: LocalDate): String {
		return "${date.month.name.take(3)} ${date.dayOfMonth}"
	}

	fun currentYearMonth(): String {
		val today = today()
		return "${today.year}-${today.monthNumber.toString().padStart(2, '0')}"
	}

	fun startOfMonth(year: Int, month: Int): LocalDate {
		return LocalDate(year, month, 1)
	}

	fun startOfMonth(date: LocalDate): LocalDate =
		LocalDate(date.year, date.month, 1)

	fun startOfWeek(date: LocalDate): LocalDate {
		val dayOfWeek = date.dayOfWeek.ordinal // Monday=0 … Sunday=6
		return date.minus(dayOfWeek, DateTimeUnit.DAY)
	}

	fun startOfQuarter(date: LocalDate): LocalDate {
		val quarterStartMonth = ((date.month.ordinal) / 3) * 3 + 1
		return LocalDate(date.year, Month.entries[quarterStartMonth - 1], 1)
	}

	fun endOfMonth(year: Int, month: Int): LocalDate {
		val lastDay = when (month) {
			1, 3, 5, 7, 8, 10, 12 -> 31
			4, 6, 9, 11 -> 30
			2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
			else -> 30
		}
		return LocalDate(year, month, lastDay)
	}

	fun daysBetween(from: LocalDate, to: LocalDate): Int {
		return (to.toEpochDays() - from.toEpochDays()).toInt()
	}
}