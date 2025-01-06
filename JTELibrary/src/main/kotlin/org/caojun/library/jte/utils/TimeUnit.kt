package org.caojun.library.jte.utils

import java.util.Calendar

enum class TimeUnit(var milliseconds: Long, val field: Int) {
    MILLISECOND(1, Calendar.MILLISECOND),
    SECOND(1000 * MILLISECOND.milliseconds, Calendar.SECOND),
    MINUTE(60 * SECOND.milliseconds, Calendar.MINUTE),
    HOUR(60 * MINUTE.milliseconds, Calendar.HOUR),
    DAY(24 * HOUR.milliseconds, Calendar.DAY_OF_YEAR),
    WEEK(7 * DAY.milliseconds, Calendar.WEEK_OF_YEAR),
    MONTH(30 * DAY.milliseconds, Calendar.MONTH),
    YEAR(365 * DAY.milliseconds, Calendar.YEAR),
}