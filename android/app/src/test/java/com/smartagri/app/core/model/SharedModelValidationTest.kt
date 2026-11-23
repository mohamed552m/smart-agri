package com.smartagri.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SharedModelValidationTest {
    private val now = 1_800_000_000_000L

    @Test
    fun `weather freshness uses the shared fetch-time rule`() {
        val fresh = weather(fetchedAt = now - 60_000L)
        val stale = weather(fetchedAt = now - WeatherFreshnessEvaluator.STALE_AFTER_MS - 1)

        assertEquals(WeatherFreshness.FRESH, WeatherFreshnessEvaluator.evaluate(fresh, now))
        assertEquals(WeatherFreshness.STALE, WeatherFreshnessEvaluator.evaluate(stale, now))
    }

    @Test
    fun `future weather fetch is invalid`() {
        val snapshot = weather(
            fetchedAt = now + WeatherFreshnessEvaluator.MAX_FUTURE_SKEW_MS + 1,
        )
        assertEquals(
            WeatherFreshness.INVALID_FUTURE,
            WeatherFreshnessEvaluator.evaluate(snapshot, now),
        )
    }

    @Test
    fun `weather rejects an unapproved location`() {
        assertThrows(IllegalArgumentException::class.java) {
            weather(fetchedAt = now).copy(locationId = "OTHER_LOCATION")
        }
    }

    @Test
    fun `plant profile rejects missing identity data`() {
        assertThrows(IllegalArgumentException::class.java) {
            plantProfile().copy(soilType = "")
        }
    }

    private fun weather(fetchedAt: Long) = WeatherSnapshot(
        locationId = ApprovedWeatherLocation.ZAGAZIG_EG,
        observedAtEpochMs = now,
        fetchedAtEpochMs = fetchedAt,
        temperatureC = 25.0,
        relativeHumidityPercent = 55.0,
        precipitationProbabilityPercent = 20.0,
        precipitationMm = 0.0,
        weatherCode = 1,
        source = WeatherSource.OPEN_METEO,
    )

    private fun plantProfile() = PlantProfile(
        profileId = "tomato-clay-v1",
        plantName = "Tomato",
        soilType = "Clay",
        calibrationVersion = "soil-cal-v1",
        dryThresholdPercent = 30.0,
        targetThresholdPercent = 60.0,
        significantRainThresholdMm = 5.0,
        updatedAtEpochMs = now,
    )
}
