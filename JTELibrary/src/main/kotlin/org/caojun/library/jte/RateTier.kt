package org.caojun.library.jte

/**
 * 阶梯价格
 * @param duration 时间，单位分钟
 * @param rate 加个，单位分
 */
data class RateTier(val duration: Int, val rate: Int)