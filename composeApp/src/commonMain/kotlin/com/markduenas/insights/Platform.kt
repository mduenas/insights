package com.markduenas.insights

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/** Returns the current wall-clock time in milliseconds since the Unix epoch. */
expect fun currentTimeMillis(): Long