package org.caojun.jte

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform