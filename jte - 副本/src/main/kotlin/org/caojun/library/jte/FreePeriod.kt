package org.caojun.library.jte

data class FreePeriod(
    val startTime: String,//开始时间HH:MM
    val endTime: String,//结束时间HH:MM
    val days: List<String>? = null//免费日，Monday、Tuesday、Wednesday、Thursday、Friday、Saturday、Sunday
)