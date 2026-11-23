package com.smartagri.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingFreshnessTest {
    private val now = 1_800_000_000_000L

    @Test
    fun `recent synced reading is fresh regardless of arrival time`() {
        val reading = reading(measuredAt = now - 60_000L, receivedAt = now + 10_000_000L)
        assertEquals(ReadingFreshness.FRESH, ReadingFreshnessEvaluator.evaluate(reading, now))
    }

    @Test
    fun `old reconnected reading stays stale even when received now`() {
        val reading = reading(measuredAt = now - 600_000L, receivedAt = now)
        assertEquals(ReadingFreshness.STALE, ReadingFreshnessEvaluator.evaluate(reading, now))
    }

    @Test
    fun `reading too far in future is invalid`() {
        val reading = reading(
            measuredAt = now + ReadingFreshnessEvaluator.MAX_FUTURE_SKEW_MS + 1,
            receivedAt = now,
        )
        assertEquals(
            ReadingFreshness.INVALID_FUTURE,
            ReadingFreshnessEvaluator.evaluate(reading, now),
        )
    }

    @Test
    fun `unsynced reading cannot become fresh`() {
        val reading = reading(now, now).copy(timeQuality = TimeQuality.UNSYNCED)
        assertEquals(ReadingFreshness.UNSYNCED, ReadingFreshnessEvaluator.evaluate(reading, now))
    }

    private fun reading(measuredAt: Long, receivedAt: Long) = SensorReading(
        schemaVersion = 1,
        readingId = "reading-1",
        deviceId = "smart-agri-physical-01",
        dataSource = DataSource.PHYSICAL,
        bootId = "boot-1",
        sequence = 1,
        measuredAtEpochMs = measuredAt,
        receivedAtEpochMs = receivedAt,
        timeQuality = TimeQuality.SYNCED,
        soilMoistureRaw = 2000,
        normalizedMoisturePercent = 50.0,
        airTemperatureC = 25.0,
        airHumidityPercent = 55.0,
        calibrationVersion = "cal-v1",
        sensorStatus = SensorStatus.OK,
        firmwareVersion = "0.1.0",
        errorCodes = emptySet(),
        simulator = null,
    )
}
