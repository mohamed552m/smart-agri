package com.smartagri.app.core.model

data class WeatherSnapshot(
    val locationId: String,
    val observedAtEpochMs: Long,
    val fetchedAtEpochMs: Long,
    val temperatureC: Double?,
    val relativeHumidityPercent: Double?,
    val precipitationProbabilityPercent: Double?,
    val precipitationMm: Double?,
    val weatherCode: Long?,
    val source: WeatherSource,
) {
    init {
        require(locationId == ApprovedWeatherLocation.ZAGAZIG_EG)
        require(observedAtEpochMs > 0)
        require(fetchedAtEpochMs > 0)
        require(relativeHumidityPercent?.let { it in 0.0..100.0 } != false)
        require(precipitationProbabilityPercent?.let { it in 0.0..100.0 } != false)
        require(precipitationMm?.let { it >= 0.0 } != false)
    }
}

enum class WeatherSource {
    OPEN_METEO,
    CACHE,
}

object ApprovedWeatherLocation {
    const val ZAGAZIG_EG = "ZAGAZIG_EG"
}

enum class WeatherFreshness {
    FRESH,
    STALE,
    INVALID_FUTURE,
}

object WeatherFreshnessEvaluator {
    const val STALE_AFTER_MS: Long = 3 * 60 * 60 * 1_000L
    const val MAX_FUTURE_SKEW_MS: Long = 2 * 60 * 1_000L

    fun evaluate(snapshot: WeatherSnapshot, nowEpochMs: Long): WeatherFreshness {
        if (snapshot.fetchedAtEpochMs > nowEpochMs + MAX_FUTURE_SKEW_MS) {
            return WeatherFreshness.INVALID_FUTURE
        }
        if (nowEpochMs - snapshot.fetchedAtEpochMs > STALE_AFTER_MS) {
            return WeatherFreshness.STALE
        }
        return WeatherFreshness.FRESH
    }
}
