package org.caojun.library.jte

import org.caojun.library.jte.utils.JsonUtils
import org.caojun.library.jte.utils.TimeUnit
import org.caojun.library.jte.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JsonTariffEngine(json: String, private val listener: Listener? = null) {
    private val listRuleSet: Map<String, RuleSet>? = JsonUtils.fromJson<Map<String, RuleSet>>(json)

    interface Listener {
        fun onLog(log: String)
    }

    fun getAllRuleNames(): Array<String> {
        return listRuleSet?.keys?.toTypedArray() ?: emptyArray()
    }

    fun getListRuleSet(): Map<String, RuleSet>? {
        return listRuleSet
    }

    fun isEnabled(): Boolean {
        val names = getAllRuleNames()
        if (names.isEmpty()) {
            return false
        }
        return try {
            for (name in names) {
                val ruleSet = getRuleSet(name) ?: return false
//                if (ruleSet.rules.isEmpty()) {
//                    return false
//                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getKeyValueOrFirstOrNull(map: Map<String, RuleSet>?, key: String): Pair<String, RuleSet>? {
        if (map == null || key.isEmpty()) {
            return null
        }
        return if (map.containsKey(key)) {
            key to map[key]!!
        } else {
            map.entries.firstOrNull()?.let { it.key to it.value }
        }
    }

    fun getRuleSet(key: String): RuleSet? {
        return listRuleSet?.get(key)
    }

    fun calculateFee(
        entryTimestamp: Long,
        time: Int,
        timeUnit: TimeUnit,
        ruleName: String = "default"
    ): CalculationResult? {
        val exitTimestamp = entryTimestamp + timeUnit.milliseconds * time
        return calculateFee(entryTimestamp, exitTimestamp, ruleName)
    }

    fun calculateFee(entryTimestamp: Long, exitTimestamp: Long, ruleName: String = "default"): CalculationResult? {
        val pair = getKeyValueOrFirstOrNull(listRuleSet, ruleName) ?: return null
        return calculateFee(pair.first, entryTimestamp, exitTimestamp, pair.second)
    }

    fun calculateFee(ruleName: String, startTimestamp: Long, endTimestamp: Long, ruleSet: RuleSet): CalculationResult {
        val calendar = Calendar.getInstance()
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val weekdayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        listener?.onLog("startTimestamp: ${TimeUtils.getTime(startTimestamp)}")
        listener?.onLog("endTimestamp: ${TimeUtils.getTime(endTimestamp)}")

        calendar.timeInMillis = startTimestamp
        val startDateTime = calendar.timeInMillis
        calendar.timeInMillis = endTimestamp
        val endDateTime = calendar.timeInMillis

        var totalFee = 0
        var currentTime = startDateTime
        val ruleCalculations = mutableListOf<RuleCalculation>()

        var lastPeriodEndTime = 0L
        while (currentTime < endDateTime) {
            val dayEnd = Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            var periodEndTime = if (dayEnd < endDateTime) dayEnd else endDateTime

            var freeMinutes = 0
            val applicableRules = ArrayList<Rule>()
            listener?.onLog("计算免费时间")
            for (rule in ruleSet.rules) {
                val type = RuleType.valueOf(rule.type.uppercase())
                val weekday = weekdayFormatter.format(currentTime)
                listener?.onLog("rule.type: $type")
                listener?.onLog("weekday: $weekday")
                val result = when (type) {
                    RuleType.FREE -> {
                        rule.days?.contains(dateFormatter.format(currentTime)) == true || weekdayInFreePeriods(
                            startDateTime,
                            endDateTime,
                            weekday,
                            rule.periods
                        )
                    }

                    else -> {
                        rule.days == null || (rule.days.contains(weekday) && TimeUtils.hasOverlap(
                            startDateTime,
                            endDateTime,
                            rule.startTime ?: "00:00",
                            rule.endTime ?: "23:59"
                        ))
                    }
                }
                listener?.onLog("result: $result")
                if (result) {
                    applicableRules.add(rule)

                    if (type == RuleType.FREE && !rule.periods.isNullOrEmpty()) {

                        for (freePeriod in rule.periods) {
                            val freeStart = freePeriod.startTime.let { timeFormatter.parse(it) } ?: Date(0)
                            val freeEnd = freePeriod.endTime.let { timeFormatter.parse(it) } ?: Date(Long.MAX_VALUE)
                            val freeStartTime = Calendar.getInstance().apply {
                                timeInMillis = currentTime
                                set(Calendar.HOUR_OF_DAY, freeStart.hours)
                                set(Calendar.MINUTE, freeStart.minutes)
                            }.timeInMillis
                            val freeEndTime = Calendar.getInstance().apply {
                                timeInMillis = currentTime
                                set(Calendar.HOUR_OF_DAY, freeEnd.hours)
                                set(Calendar.MINUTE, freeEnd.minutes)
                            }.timeInMillis

                            val hasOverlap =
                                TimeUtils.hasOverlap(currentTime, periodEndTime, freeStartTime, freeEndTime)
                            var calculatedTime = 0
                            if (hasOverlap) {
                                val startTime = maxOf(currentTime, freeStartTime)
                                val endTime = minOf(periodEndTime, freeEndTime)
                                calculatedTime = TimeUtils.calculateNumberUnitBetween(
                                    startTime,
                                    endTime,
                                    TimeUnit.MINUTE
                                )
                                ruleCalculations.add(
                                    RuleCalculation(
                                        rule,
                                        freeStartTime,
                                        freeEndTime,
                                        calculatedTime,
                                        0
                                    )
                                )
                            }
                            freeMinutes += calculatedTime
                        }
                    }
                }
            }

            listener?.onLog("计算收费时间")
            var calCurrentTime = true
            for (rule in applicableRules) {
                val type = RuleType.valueOf(rule.type.uppercase())
                listener?.onLog("rule.type: $type")
                when (type) {

                    RuleType.FREE -> {
                        if (rule.days?.contains(dateFormatter.format(currentTime)) == true) {
                            ruleCalculations.add(RuleCalculation(rule, currentTime, endDateTime, 0, 0))
                            lastPeriodEndTime = periodEndTime
                            break
                        }
                    }

                    RuleType.TIER, RuleType.FLAT -> {
                        val periodStartTime = TimeUtils.formatTime(currentTime, rule.startTime)
                        if (currentTime < periodStartTime) {
                            currentTime = periodStartTime
                        }
                        val ruleEndTime = TimeUtils.formatTime(periodEndTime, rule.endTime)
                        if (ruleEndTime < periodEndTime) {
                            periodEndTime = ruleEndTime
                        }
                        when (type) {
                            RuleType.TIER -> {
                                if (!rule.rateTiers.isNullOrEmpty()) {
                                    listener?.onLog("currentTime: ${TimeUtils.getTime(currentTime)}")
                                    listener?.onLog("periodEndTime: ${TimeUtils.getTime(periodEndTime)}")
                                    val remainingMinutes = TimeUtils.calculateNumberUnitBetween(
                                        currentTime,
                                        periodEndTime,
                                        TimeUnit.MINUTE
                                    ) - freeMinutes
                                    var rateTier =
                                        rule.rateTiers.find { tier -> tier.duration >= remainingMinutes }
                                    if (rateTier == null) {
                                        rateTier = rule.rateTiers.maxByOrNull { it.duration }!!
                                    }
                                    totalFee += rateTier.rate
                                    ruleCalculations.add(
                                        RuleCalculation(
                                            rule,
                                            currentTime,
                                            periodEndTime,
                                            remainingMinutes,
                                            rateTier.rate
                                        )
                                    )
                                    currentTime = periodEndTime
                                    lastPeriodEndTime = periodEndTime
                                }
                            }

                            RuleType.FLAT -> {
                                val flatRate = rule.rate ?: 0
                                val flatUnit = try {
                                    TimeUnit.valueOf(rule.unit!!.uppercase())
                                } catch (e: Exception) {
                                    TimeUnit.HOUR
                                }
                                var endTimestamp = periodEndTime
                                if (true != rule.dailySettlement) {
                                    calCurrentTime = false
                                    endTimestamp = endDateTime
                                }
                                listener?.onLog("currentTime: ${TimeUtils.getTime(currentTime)}")
                                listener?.onLog("periodEndTime: ${TimeUtils.getTime(endTimestamp)}")
                                val countUnit = rule.countUnit
                                var calculatedTime =
                                    TimeUtils.calculateNumberUnitBetween(currentTime, endTimestamp, flatUnit, countUnit)
                                listener?.onLog("calculatedTime: $calculatedTime")
                                if (freeMinutes > 0) {
                                    val freeTime = TimeUtils.calculateNumberUnitBetween(freeMinutes * 60L, flatUnit)
                                    calculatedTime -= freeTime
                                    listener?.onLog("freeMinutes: $freeMinutes")
                                }
                                if (calculatedTime < 0) {
                                    calculatedTime = 0
                                }
                                var calculatedFee = flatRate * calculatedTime
                                listener?.onLog("calculatedFee: $calculatedFee")
                                if (rule.dailyCap != null) {
                                    calculatedFee = minOf(calculatedFee, rule.dailyCap)
                                    listener?.onLog("rule.dailyCap: ${rule.dailyCap}")
                                }
                                totalFee += calculatedFee
                                listener?.onLog("totalFee: $totalFee")
                                calendar.timeInMillis = currentTime
//                                val before = TimeUtils.getTime(calendar.timeInMillis)
                                val field = flatUnit.field
                                calendar.add(field, calculatedTime)
//                                val after = TimeUtils.getTime(calendar.timeInMillis)
                                val flatEndTime = calendar.timeInMillis
                                ruleCalculations.add(
                                    RuleCalculation(
                                        rule,
                                        currentTime,
                                        endTimestamp,
                                        calculatedTime,
                                        calculatedFee
                                    )
                                )
                                currentTime = flatEndTime
                                if (true != rule.dailySettlement) {
                                    currentTime = endDateTime
                                }
                                lastPeriodEndTime = endTimestamp
                                listener?.onLog("lastPeriodEndTime: ${TimeUtils.getTime(lastPeriodEndTime)}")
                            }

                            else -> {}
                        }
                    }
                }
            }

            if (calCurrentTime) {
                // 处理完当天的费用计算后，推进到下一天的开始时间
                calendar.timeInMillis = periodEndTime
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                currentTime = calendar.timeInMillis
            }
        }

        val cappedFee = if (ruleSet.maxAmount != null && totalFee > ruleSet.maxAmount) ruleSet.maxAmount else totalFee

        return CalculationResult(
            ruleName = ruleName,
            totalFee = cappedFee,
            details = ruleCalculations,
            startTimestamp = startTimestamp,
            endTimestamp = if (lastPeriodEndTime == 0L) endTimestamp else lastPeriodEndTime,
            maxAmount = ruleSet.maxAmount
        )
    }

    private fun weekdayInFreePeriods(
        startDateTime: Long,
        endDateTime: Long,
        day: String,
        periods: List<FreePeriod>?
    ): Boolean {
        if (day.isEmpty() || periods.isNullOrEmpty()) {
            return false
        }
        for (freePeriod in periods) {
            if (TimeUtils.hasOverlap(
                    startDateTime,
                    endDateTime,
                    freePeriod.startTime,
                    freePeriod.endTime
                )
            ) {
                if (freePeriod.days.isNullOrEmpty() || freePeriod.days.contains(day)) {
                    return true
                }
            }
        }
        return false
    }

    private fun calculateNumberUnitBetween(startTimestamp: Long, endTimestamp: Long, rule: Rule): Int {
        val unit = TimeUnit.values().find { it.name.lowercase() == rule.unit?.lowercase() } ?: TimeUnit.HOUR
        return TimeUtils.calculateNumberUnitBetween(startTimestamp, endTimestamp, unit)
    }

    private fun formatDate(timestamp: Long): String {
        return TimeUtils.getTime(timestamp, "yyyy-MM-dd")
    }

    private fun parseTime(timestamp: Long, hhmm: String): Calendar {
        val parts = hhmm.split(":")
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun isHoliday(time: Long = System.currentTimeMillis()): Boolean {
        try {
            val names = getAllRuleNames()
            if (names.isEmpty()) {
                return false
            }
            val weekdayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
            val weekday = weekdayFormatter.format(time)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormatter.format(time)
            for (name in names) {
                val ruleSet = getRuleSet(name) ?: continue
                val rules = ruleSet.rules
                for (rule in rules) {
                    val contains = (rule.days?.contains(date) ?: false) || (rule.days?.contains(weekday) ?: false)
                    if (rule.type.uppercase() == RuleType.FREE.name && contains) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
        }
        return false
    }
}