package org.caojun.library.jte

data class RuleSet(
    val maxAmount: Int? = null,
    val rules: List<Rule> = emptyList<Rule>()
)