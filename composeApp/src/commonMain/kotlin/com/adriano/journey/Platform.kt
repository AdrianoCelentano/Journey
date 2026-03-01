package com.adriano.journey

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
