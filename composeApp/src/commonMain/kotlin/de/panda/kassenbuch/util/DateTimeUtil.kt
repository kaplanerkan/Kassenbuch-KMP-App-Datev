package de.panda.kassenbuch.util

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.*

fun currentDate(): LocalDate =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

fun currentTime(): LocalTime =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

fun LocalDate.firstDayOfMonth(): LocalDate =
    LocalDate(this.year, this.month, 1)

fun LocalDate.minusDays(days: Int): LocalDate =
    this.minus(days, DateTimeUnit.DAY)

fun LocalDate.toEpochMillis(): Long {
    val instant = this.atStartOfDayIn(TimeZone.currentSystemDefault())
    return instant.toEpochMilliseconds()
}

fun epochMillisToLocalDate(millis: Long): LocalDate {
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
}
