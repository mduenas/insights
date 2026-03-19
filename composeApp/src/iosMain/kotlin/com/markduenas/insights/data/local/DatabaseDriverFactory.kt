package com.markduenas.insights.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.markduenas.insights.data.local.db.InsightsDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(InsightsDatabase.Schema, "insights.db")
}
