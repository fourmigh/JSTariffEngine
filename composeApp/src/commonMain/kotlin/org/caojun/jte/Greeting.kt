package org.caojun.jte

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
//        val jsonTariffEngine = JsonTariffEngine()
        return "Hello, ${platform.name}!"
    }
}