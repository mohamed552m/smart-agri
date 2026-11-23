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
)

enum class WeatherSource {
    OPEN_METEO,
    CACHE,
}

object ApprovedWeatherLocation {
    const val ZAGAZIG_EG = "ZAGAZIG_EG"
}
