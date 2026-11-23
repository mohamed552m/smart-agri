package com.smartagri.app.core.model

enum class ReadingFreshness {
    FRESH,
    STALE,
    INVALID_FUTURE,
    UNSYNCED,
}

object ReadingFreshnessEvaluator {
    const val STALE_AFTER_MS: Long = 5 * 60 * 1_000L
    const val MAX_FUTURE_SKEW_MS: Long = 2 * 60 * 1_000L

    fun evaluate(reading: SensorReading, nowEpochMs: Long): ReadingFreshness {
        if (reading.timeQuality != TimeQuality.SYNCED) {
            return ReadingFreshness.UNSYNCED
        }
        if (reading.measuredAtEpochMs > nowEpochMs + MAX_FUTURE_SKEW_MS) {
            return ReadingFreshness.INVALID_FUTURE
        }
        if (nowEpochMs - reading.measuredAtEpochMs > STALE_AFTER_MS) {
            return ReadingFreshness.STALE
        }
        return ReadingFreshness.FRESH
    }
}
