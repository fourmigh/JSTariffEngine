package org.caojun.library.jte

import org.caojun.library.jte.utils.JsonTimestamp

class RuleCalculation(
    val rule: Rule,
    startTimestamp: Long,
    endTimestamp: Long,
    val calculatedTime: Int,
    val calculatedFee: Int
) {
    val startTime = JsonTimestamp(startTimestamp)
    val endTime = JsonTimestamp(endTimestamp)
}