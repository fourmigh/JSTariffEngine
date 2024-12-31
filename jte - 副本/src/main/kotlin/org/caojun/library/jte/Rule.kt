package org.caojun.library.jte

/**
 * 规则
 */
class Rule(
    val name: String,//规则名称
    val type: String,//规则类型RuleType
    val rate: Int? = null,//单价
    val rateTiers: List<RateTier>? = null,//阶梯价格
    val startTime: String? = null,//开始时间HH:MM
    val endTime: String? = null,//结束时间HH:MM
    val days: List<String>? = null,//有效的星期名，Monday、Tuesday、Wednesday、Thursday、Friday、Saturday、Sunday；节假日日期YYYY-MM-DD
    val periods: List<FreePeriod>? = null,
    val unit: String? = null,//价格时间单位TimeUnit
    val countUnit: Int = 1,//价格时间单位的数量
    val dailyCap: Int? = null,//每天金额上限
    val dailySettlement: Boolean? = null,//每天结算
) {

    fun isSameFlat(rule: Rule): Boolean {
        if (RuleType.FLAT.name.lowercase() != rule.type.lowercase()) {
            return false
        }
        if (rule.rate == null || rule.unit == null) {
            return false
        }
        return this.rate == rule.rate && this.unit == rule.unit
    }
}