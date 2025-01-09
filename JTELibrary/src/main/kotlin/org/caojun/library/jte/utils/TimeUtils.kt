package org.caojun.library.jte.utils

import java.text.DateFormatSymbols
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object TimeUtils {

    private val LocalTimeZone = TimeZone.getDefault()

    private fun getSimpleDateFormat(dateFormat: String, timeZone: TimeZone): SimpleDateFormat {
        val df = SimpleDateFormat(dateFormat)
        df.timeZone = timeZone
        return df
    }

    fun getTime(dateFormat: String, timeZone: TimeZone, time: Long): String {
        if (dateFormat.isEmpty()) {
            return ""
        }
        val df = getSimpleDateFormat(dateFormat, timeZone)
        val date = Date(abs(time))
        return df.format(date)
    }

    fun getTime(dateFormat: String, time: Long): String {
        return getTime(dateFormat, LocalTimeZone, time)
    }

    fun getTime(dateFormat: String): String {
        return getTime(dateFormat, LocalTimeZone)
    }

    fun getTime(dateFormat: String, timeZone: TimeZone): String {
        return getTime(dateFormat, timeZone, getTime())
    }

    fun getTime(): Long {
        val date = Date()
        return date.time
    }

    fun getDays(timeStart: Long, timeEnd: Long): Long {
        val time = timeEnd - timeStart
        return time / (1000 * 60 * 60 * 24)
    }

    fun getCalendar(timeZone: TimeZone): Calendar {
        return Calendar.getInstance(timeZone)
    }

    fun getCalendar(): Calendar {
        return getCalendar(LocalTimeZone)
    }

    /**
     * 在当前日期前/后n天
     */
    fun getDate(n: Int): Date {
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, n)
        return c.time
    }

    /**
     * 当前日期是星期几
     * 0为星期日
     */
    fun getWeekdayInt(date: Date): Int {
        val cal = Calendar.getInstance()
        val isFirstSunday = cal.firstDayOfWeek == Calendar.SUNDAY
        cal.time = date
        val weekDay = cal.get(Calendar.DAY_OF_WEEK)
        if (isFirstSunday) {
            return weekDay - 1
        } else if (weekDay == 7) {
            return 0
        }
        return weekDay - 1
    }

    fun getWeekdayInt(time: Long): Int {
        val date = Date(abs(time))
        return getWeekdayInt(date)
    }

    fun getDayOfWeek(date: Date): Int {
        val cal = Calendar.getInstance()
        cal.time = date
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    fun getWeekdayString(): String {
        return getWeekdayString(System.currentTimeMillis())
    }

    fun getWeekdayString(time: Long): String {
        return getWeekdayString(Date(abs(time)))
    }

    fun getWeekdayString(date: Date): String {
        val weekDay = getDayOfWeek(date)
        return DateFormatSymbols.getInstance().shortWeekdays[weekDay]
    }

    fun getWeekday(time: Long, locale: Locale = Locale.ENGLISH): String {
        val date = Date(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale) ?: ""
    }

    fun getWeekday(): String {
        return getWeekday(System.currentTimeMillis())
    }

    fun getTime(dateFormat: String, date: Date): String {
        if (dateFormat.isEmpty()) {
            return ""
        }
        val df = getSimpleDateFormat(dateFormat, LocalTimeZone)
        return df.format(date)
    }

    fun isYesterday(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DATE, -1)

        return isOneDay(calendar, yesterday)
    }

    fun isTomorrow(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DATE, 1)

        return isOneDay(calendar, tomorrow)
    }

    fun isToday(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val today = Calendar.getInstance()

        return isOneDay(calendar, today)
    }

    fun isSameDay(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance()
        c1.time = d1
        val c2 = Calendar.getInstance()
        c2.time = d2
        return isOneDay(c1, c2)
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        val c1 = Calendar.getInstance()
        c1.timeInMillis = t1
        val c2 = Calendar.getInstance()
        c2.timeInMillis = t2
        return isOneDay(c1, c2)
    }

    /**
     * 年月日时分秒相同
     */
    fun isSameMoment(t1: Long, t2: Long): Boolean {
        if (!isSameDay(t1, t2)) {
            return false
        }
        if (getMinute(t1) != getMinute(t2)) {
            return false
        }
        if (getHour(t1) != getHour(t2)) {
            return false
        }
        return true
    }

    fun isOneDay(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(
            Calendar.DAY_OF_MONTH
        ) == c2.get(Calendar.DAY_OF_MONTH)
    }

    fun getDate(time: Long = System.currentTimeMillis()): Date {
        return Date(abs(time))
    }

    fun isSameMonth(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance()
        c1.time = d1
        val c2 = Calendar.getInstance()
        c2.time = d2
        return isSameMonth(c1, c2)
    }

    fun isSameMonth(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
    }

    fun isThisMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val today = Calendar.getInstance()

        return isSameMonth(calendar, today)
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    fun strToDateLong(strDate: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
        return try {
            val formatter = SimpleDateFormat(pattern)
            val pos = ParsePosition(0)
            formatter.parse(strDate, pos).time
        } catch (e: Exception) {
            0L
        }
    }

    fun isHourMinute(time: Long, hour: Int, minute: Int): Boolean {
        return getHour(time) == hour && getMinute(time) == minute
    }

    fun getCalendar(time: Long = System.currentTimeMillis()): Calendar {
        val c = Calendar.getInstance()
        c.time = Date(abs(time))
        return c
    }

    fun getHour(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinute(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.MINUTE)
    }

    fun getSecond(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.SECOND)
    }

    fun getDay(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.MONTH) + 1
    }

    fun getYear(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        return c.get(Calendar.YEAR)
    }

    fun getField(time: Long, fieldCalendar: Int): Int {
        val c = getCalendar(time)
        return c.get(fieldCalendar)
    }

    fun getTimeStamp(dateFormat: String, dateString: String): Long {
        val df = SimpleDateFormat(dateFormat)
        var date = Date()
        try {
            date = df.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date.time
    }

    fun timeInRange(compareTime: String, startTime: String, endTime: String, dateFormat: String): Boolean {
        val start = getTimeStamp(dateFormat, startTime)
        val end = getTimeStamp(dateFormat, endTime)
        val compare = getTimeStamp(dateFormat, compareTime)
        return compare > start && compare < end
    }

    private fun hhmm2intArray(hhmm: String): Array<Int>? {
        if (hhmm.isEmpty() || hhmm.length != 5 || hhmm.indexOf(":") != 2) {
            return null
        }
        val hm = hhmm.split(":")
        if (hm.isEmpty() || hm.size != 2) {
            return null
        }
        try {
            val hh = hm[0].toInt()
            val mm = hm[1].toInt()
            return arrayOf(hh, mm)
        } catch (e: Exception) {
            return null
        }
    }

    fun betweenTime(times: Array<String>, time: Long = System.currentTimeMillis()): Boolean {
        if (times.size != 2) {
            return false
        }
        return betweenTime(times[0], times[1], time)
    }

    fun betweenTime(beginTime: String, endTime: String, time: Long = System.currentTimeMillis()): Boolean {
        val hhmmBegin = hhmm2intArray(beginTime) ?: return false
        val hhmmEnd = hhmm2intArray(endTime) ?: return false
        return betweenTime(hhmmBegin[0], hhmmBegin[1], hhmmEnd[0], hhmmEnd[1], time)
    }

    fun betweenTime(
        beginHour: Int,
        beginMin: Int,
        endHour: Int,
        endMin: Int,
        time: Long = System.currentTimeMillis()
    ): Boolean {

        if (beginHour == endHour && beginMin == endMin) {
            //开始和结束时间一样
            return false
        }

        val now = Calendar.getInstance()
        now.timeInMillis = time
        val startTime = Calendar.getInstance()
        startTime.set(Calendar.HOUR_OF_DAY, beginHour)
        startTime.set(Calendar.MINUTE, beginMin)
        val endTime = Calendar.getInstance()
        endTime.set(Calendar.HOUR_OF_DAY, endHour)
        endTime.set(Calendar.MINUTE, endMin)
        /**跨天的特殊情况(比如23:00-2:00) */
        if (!startTime.before(endTime)) {
            if (startTime.before(now)) {
                startTime.add(Calendar.DATE, -1)
            }
            if (endTime.before(now)) {
                endTime.add(Calendar.DATE, 1)
            }
        }
        return !now.before(startTime) && !now.after(endTime)
    }

    fun parseTime(time: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Long {
        return try {
            val format = SimpleDateFormat(pattern)
            val date = format.parse(time)
            date.time
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    fun getEnTime(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH)
        format.timeZone = LocalTimeZone
        val date = Date(abs(time))
        return format.format(date)
    }

    fun parseEnTime(time: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH)
        format.timeZone = LocalTimeZone
        val datee = format.parse(time)
        return datee.time
    }

    fun translateTime(
        time: String,
        patternIn: String = "yyyyMMddHHmmss",
        patternOut: String = "yyyy-MM-dd HH:mm:ss"
    ): String {
        val longTime = parseTime(time, patternIn)
        return getTime(longTime, patternOut)
    }

    fun getDisplayName(): String {
        return LocalTimeZone.displayName
    }

    fun getTime(time: Long = System.currentTimeMillis(), dateFormat: String = "yyyy-MM-dd HH:mm:ss.SSS"): String {
        return getTime(dateFormat, LocalTimeZone, time)
    }

    fun showTimeFormat(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else {
            "$time"
        }
    }

    fun isLastDayOnMonth(time: Long = System.currentTimeMillis()): Boolean {
        val day = getDay(time)
        val lastDayOfMonth = getLastDayOfMonth(time)
        return day == lastDayOfMonth
    }

    fun getLastDayOfMonth(time: Long = System.currentTimeMillis()): Int {
        val c = getCalendar(time)
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE))
        return getDay(c.timeInMillis)
    }

    fun getTime(year: Int, month: Int, day: Int): Long {
        val c = getCalendar(year, month, day)
        return c.timeInMillis
    }

    fun getCalendar(year: Int, month: Int, day: Int): Calendar {
        val c = getCalendar()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1)
        c.set(Calendar.DAY_OF_MONTH, day)
        return c
    }

    fun getTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long {
        val c = getCalendar(year, month, day, hour, minute, second)
        return c.timeInMillis
    }

    fun getCalendar(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        millisecond: Int = 0
    ): Calendar {
        val c = getCalendar(year, month, day)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, second)
        c.set(Calendar.MILLISECOND, millisecond)
        return c
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun timeRangeToDays(startTime: Long, endTime: Long): List<Long> {
        val startDate = getCalendar(startTime.coerceAtMost(endTime))
        val endDate = getCalendar(startTime.coerceAtLeast(endTime))
        val list = ArrayList<Long>()
        while (endDate.after(startDate)) {
            list.add(startDate.timeInMillis)
            startDate.add(Calendar.DATE, 1)
        }
        if (!isSameDay(list[list.size - 1], endDate.timeInMillis)) {
            list.add(endDate.timeInMillis)
        }
        return list
    }

    fun timeUniteDays(startTime1: Long, endTime1: Long, startTime2: Long, endTime2: Long): List<Long> {
        val days1 = timeRangeToDays(startTime1, endTime1)
        val days2 = timeRangeToDays(startTime2, endTime2)
        val list = ArrayList<Long>()
        for (day1 in days1) {
            for (day2 in days2) {
                if (isSameDay(day1, day2)) {
                    list.add(day1)
                }
            }
        }
        return list
    }

    fun getLocalTimeZone(): TimeZone {
        return LocalTimeZone
    }

    fun getTimeZone(): String {
        val t = LocalTimeZone.getOffset(System.currentTimeMillis()) / (3600 * 1000)
        return "GMT$t"
    }

    fun getMonthLastDay(year: Int, month: Int): Int {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1)
        c.set(Calendar.DATE, 1)
        c.roll(Calendar.DATE, -1)
        return c.get(Calendar.DATE)
    }

    fun isOverHours(hours: Int, timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()

        // 将时间戳转换为秒
        val timestampInSeconds = timestamp / 1000
        val currentTimeInSeconds = currentTime / 1000

        // 计算时间戳对应的小时数
        val timestampHours = timestampInSeconds / 3600

        // 计算当前时间对应的小时数
        val currentHours = currentTimeInSeconds / 3600

        // 判断当前时间小时数是否大于时间戳小时数+N
        return currentHours > timestampHours + hours
    }

    fun getTimestampAgo(time: Int, unit: TimeUnit, currentTimestamp: Long = System.currentTimeMillis()): Long {
        val milliseconds = time * unit.milliseconds
        return currentTimestamp - milliseconds
    }

    fun getNextDayStartTime(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun calculateNumberUnitBetween(defTimeSecond: Long, unit: TimeUnit): Int {
        val unitTimeMinute = 60
        val unitTimeHour = 60 * unitTimeMinute
        val unitTimeDay = 24 * unitTimeHour
        val unitTime = when (unit) {
            TimeUnit.MINUTE -> unitTimeMinute
            TimeUnit.HOUR -> unitTimeHour
            else -> unitTimeDay
        }
        var count = defTimeSecond / unitTime
        if (unit != TimeUnit.DAY && defTimeSecond % unitTime > 0) {
            count++
        }
        return count.toInt()
    }

    fun calculateNumberUnitBetween(startTimestamp: Long, endTimestamp: Long, unit: TimeUnit, countUnit: Int = 1): Int {
        val defTime = (endTimestamp - startTimestamp) / 1000
        if (defTime <= 0) {
            return 0
        }
        val number = calculateNumberUnitBetween(defTime, unit)
        return if (countUnit > 1) {
            if (number % countUnit == 0) {
                number / countUnit
            } else {
                number / countUnit + 1
            }
        } else {
            number
        }
    }

    fun hasOverlap(startTime1: Long, endTime1: Long, startTime2: Long, endTime2: Long): Boolean {
        return startTime1 < endTime2 && startTime2 < endTime1
    }

    fun hasOverlap(startTime1: Long, endTime1: Long, startTimeHHMM: String, endTimeHHMM: String): Boolean {
        val startTime2 = formatTime(startTime1, startTimeHHMM)
        val endTime2 = formatTime(endTime1, endTimeHHMM)
        return startTime1 < endTime2 && startTime2 < endTime1
    }

    fun formatTime(timestamp: Long, hhmm: String?): Long {
        if (hhmm.isNullOrEmpty()) {
            return timestamp
        }
        return try {
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeHHMM = hhmm.let { timeFormatter.parse(it) } ?: Date(0)
            Calendar.getInstance().apply {
                time = Date(timestamp)
                set(Calendar.HOUR_OF_DAY, timeHHMM.hours)
                set(Calendar.MINUTE, timeHHMM.minutes)
            }.timeInMillis
        } catch (e: Exception) {
            timestamp
        }
    }
}