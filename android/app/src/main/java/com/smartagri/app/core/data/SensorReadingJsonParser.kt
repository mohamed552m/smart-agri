package com.smartagri.app.core.data

import com.smartagri.app.core.model.DataSource
import com.smartagri.app.core.model.SensorReading
import com.smartagri.app.core.model.SensorStatus
import com.smartagri.app.core.model.Simulator
import com.smartagri.app.core.model.TimeQuality
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

sealed interface SensorReadingParseResult {
    data class Valid(val reading: SensorReading) : SensorReadingParseResult
    data class Invalid(val issues: Set<SensorReadingIssue>) : SensorReadingParseResult
}

enum class SensorReadingIssue {
    MALFORMED_OR_MISSING_REQUIRED_FIELD,
    UNSUPPORTED_SCHEMA_VERSION,
    UNKNOWN_DATA_SOURCE,
    UNKNOWN_TIME_QUALITY,
    UNKNOWN_SENSOR_STATUS,
    INVALID_SIMULATOR,
    INVALID_VALUE_RANGE,
    MISSING_CALIBRATION,
    INVALID_ERROR_CODES,
}

object SensorReadingJsonParser {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun parse(payload: String): SensorReadingParseResult {
        val wire = try {
            json.decodeFromString<SensorReadingWire>(payload)
        } catch (_: SerializationException) {
            return SensorReadingParseResult.Invalid(
                setOf(SensorReadingIssue.MALFORMED_OR_MISSING_REQUIRED_FIELD),
            )
        } catch (_: IllegalArgumentException) {
            return SensorReadingParseResult.Invalid(
                setOf(SensorReadingIssue.MALFORMED_OR_MISSING_REQUIRED_FIELD),
            )
        }

        val issues = linkedSetOf<SensorReadingIssue>()
        if (wire.schemaVersion != 1) issues += SensorReadingIssue.UNSUPPORTED_SCHEMA_VERSION

        val dataSource = wire.dataSource.toEnumOrNull<DataSource>()
            ?: run {
                issues += SensorReadingIssue.UNKNOWN_DATA_SOURCE
                null
            }
        val timeQuality = wire.timeQuality.toEnumOrNull<TimeQuality>()
            ?: run {
                issues += SensorReadingIssue.UNKNOWN_TIME_QUALITY
                null
            }
        val sensorStatus = wire.sensorStatus.toEnumOrNull<SensorStatus>()
            ?: run {
                issues += SensorReadingIssue.UNKNOWN_SENSOR_STATUS
                null
            }
        val simulator = wire.simulator?.toEnumOrNull<Simulator>()

        if (
            wire.readingId.isBlank() ||
            wire.deviceId.isBlank() ||
            wire.bootId.isBlank() ||
            wire.firmwareVersion.isBlank() ||
            wire.sequence < 0 ||
            wire.measuredAtEpochMs <= 0 ||
            wire.receivedAtEpochMs <= 0 ||
            wire.soilMoistureRaw !in 0L..4095L ||
            wire.normalizedMoisturePercent?.let { it !in 0.0..100.0 } == true ||
            wire.airTemperatureC?.let { it !in -40.0..80.0 } == true ||
            wire.airHumidityPercent?.let { it !in 0.0..100.0 } == true
        ) {
            issues += SensorReadingIssue.INVALID_VALUE_RANGE
        }

        if (wire.normalizedMoisturePercent != null && wire.calibrationVersion.isNullOrBlank()) {
            issues += SensorReadingIssue.MISSING_CALIBRATION
        }
        if (
            wire.errorCodes?.isEmpty() == true ||
            wire.errorCodes?.values?.any { !it } == true
        ) {
            issues += SensorReadingIssue.INVALID_ERROR_CODES
        }
        when (dataSource) {
            DataSource.SIMULATED -> if (simulator != Simulator.WOKWI) {
                issues += SensorReadingIssue.INVALID_SIMULATOR
            }
            DataSource.PHYSICAL -> if (wire.simulator != null) {
                issues += SensorReadingIssue.INVALID_SIMULATOR
            }
            null -> Unit
        }

        if (issues.isNotEmpty() || dataSource == null || timeQuality == null || sensorStatus == null) {
            return SensorReadingParseResult.Invalid(issues)
        }

        return SensorReadingParseResult.Valid(
            SensorReading(
                schemaVersion = wire.schemaVersion,
                readingId = wire.readingId,
                deviceId = wire.deviceId,
                dataSource = dataSource,
                bootId = wire.bootId,
                sequence = wire.sequence,
                measuredAtEpochMs = wire.measuredAtEpochMs,
                receivedAtEpochMs = wire.receivedAtEpochMs,
                timeQuality = timeQuality,
                soilMoistureRaw = wire.soilMoistureRaw,
                normalizedMoisturePercent = wire.normalizedMoisturePercent,
                airTemperatureC = wire.airTemperatureC,
                airHumidityPercent = wire.airHumidityPercent,
                calibrationVersion = wire.calibrationVersion,
                sensorStatus = sensorStatus,
                firmwareVersion = wire.firmwareVersion,
                errorCodes = wire.errorCodes.orEmpty().filterValues { it }.keys,
                simulator = simulator,
            ),
        )
    }
}

private inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? =
    enumValues<T>().firstOrNull { it.name == this }

@Serializable
private data class SensorReadingWire(
    val schemaVersion: Int,
    val readingId: String,
    val deviceId: String,
    val dataSource: String,
    val bootId: String,
    val sequence: Long,
    val measuredAtEpochMs: Long,
    val receivedAtEpochMs: Long,
    val timeQuality: String,
    val soilMoistureRaw: Long,
    val normalizedMoisturePercent: Double? = null,
    val airTemperatureC: Double? = null,
    val airHumidityPercent: Double? = null,
    val calibrationVersion: String? = null,
    val sensorStatus: String,
    val firmwareVersion: String,
    val errorCodes: Map<String, Boolean>? = null,
    val simulator: String? = null,
)
