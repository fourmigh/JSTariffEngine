package org.caojun.library.jte

import org.caojun.library.jte.utils.JsonTimestamp

class CalculationResult(
    val ruleName: String,
    val totalFee: Int,
    val details: List<RuleCalculation>,
    startTimestamp: Long,
    endTimestamp: Long,
    val maxAmount: Int? = null
) {
    val startTime = JsonTimestamp(startTimestamp)
    val endTime = JsonTimestamp(endTimestamp)
}