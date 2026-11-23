package com.smartagri.app.core.model

data class SensorReading(
    val schemaVersion: Int,
    val readingId: String,
    val deviceId: String,
    val dataSource: DataSource,
    val bootId: String,
    val sequence: Long,
    val measuredAtEpochMs: Long,
    val receivedAtEpochMs: Long,
    val timeQuality: TimeQuality,
    val soilMoistureRaw: Long,
    val normalizedMoisturePercent: Double?,
    val airTemperatureC: Double?,
    val airHumidityPercent: Double?,
    val calibrationVersion: String?,
    val sensorStatus: SensorStatus,
    val firmwareVersion: String,
    val errorCodes: Set<String>,
    val simulator: Simulator?,
)

enum class DataSource {
    PHYSICAL,
    SIMULATED,
}

enum class TimeQuality {
    SYNCED,
    UNSYNCED,
}

enum class SensorStatus {
    OK,
    PARTIAL,
    ERROR,
}

enum class Simulator {
    WOKWI,
}
