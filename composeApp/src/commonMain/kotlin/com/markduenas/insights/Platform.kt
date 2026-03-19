package com.markduenas.insights

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform