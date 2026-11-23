package com.smartagri.app.core.data

import com.smartagri.app.core.model.DataSource
import com.smartagri.app.core.model.SensorStatus
import com.smartagri.app.core.model.Simulator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SensorReadingJsonParserTest {
    @Test
    fun `physical fixture parses and omits empty errors`() {
        val result = SensorReadingJsonParser.parse(fixture("sensor-reading-physical-valid.json"))
        assertTrue(result is SensorReadingParseResult.Valid)
        val reading = (result as SensorReadingParseResult.Valid).reading
        assertEquals(DataSource.PHYSICAL, reading.dataSource)
        assertEquals(47.5, reading.normalizedMoisturePercent)
        assertTrue(reading.errorCodes.isEmpty())
        assertEquals(null, reading.simulator)
    }

    @Test
    fun `simulated fixture stays visibly distinguishable`() {
        val result = SensorReadingJsonParser.parse(fixture("sensor-reading-simulated-valid.json"))
        val reading = (result as SensorReadingParseResult.Valid).reading
        assertEquals(DataSource.SIMULATED, reading.dataSource)
        assertEquals(Simulator.WOKWI, reading.simulator)
    }

    @Test
    fun `partial fixture allows missing DHT values and keeps error code`() {
        val result = SensorReadingJsonParser.parse(fixture("sensor-reading-partial-valid.json"))
        val reading = (result as SensorReadingParseResult.Valid).reading
        assertEquals(SensorStatus.PARTIAL, reading.sensorStatus)
        assertEquals(null, reading.airTemperatureC)
        assertTrue("DHT_READ_FAILED" in reading.errorCodes)
    }

    @Test
    fun `missing required field is invalid`() {
        val result = SensorReadingJsonParser.parse(
            fixture("sensor-reading-missing-required-invalid.json"),
        )
        assertTrue(result is SensorReadingParseResult.Invalid)
        assertTrue(
            SensorReadingIssue.MALFORMED_OR_MISSING_REQUIRED_FIELD in
                (result as SensorReadingParseResult.Invalid).issues,
        )
    }

    @Test
    fun `unknown enum does not crash and is invalid`() {
        val result = SensorReadingJsonParser.parse(
            fixture("sensor-reading-unknown-enum-invalid.json"),
        )
        assertTrue(result is SensorReadingParseResult.Invalid)
        assertTrue(
            SensorReadingIssue.UNKNOWN_DATA_SOURCE in
                (result as SensorReadingParseResult.Invalid).issues,
        )
    }

    @Test
    fun `unresolved server timestamp placeholder is invalid`() {
        val payload = fixture("sensor-reading-physical-valid.json").replace(
            "\"receivedAtEpochMs\": 1774320001200",
            "\"receivedAtEpochMs\": { \".sv\": \"timestamp\" }",
        )
        assertTrue(SensorReadingJsonParser.parse(payload) is SensorReadingParseResult.Invalid)
    }

    @Test
    fun `explicit null optional values map to missing values`() {
        val payload = fixture("sensor-reading-physical-valid.json").replace(
            "\"airTemperatureC\": 26.4",
            "\"airTemperatureC\": null",
        )
        val result = SensorReadingJsonParser.parse(payload)
        val reading = (result as SensorReadingParseResult.Valid).reading
        assertEquals(null, reading.airTemperatureC)
    }

    @Test
    fun `empty error map is invalid because producers must omit it`() {
        val result = SensorReadingJsonParser.parse(
            fixture("sensor-reading-empty-errors-invalid.json"),
        )
        assertTrue(result is SensorReadingParseResult.Invalid)
        assertTrue(
            SensorReadingIssue.INVALID_ERROR_CODES in
                (result as SensorReadingParseResult.Invalid).issues,
        )
    }

    @Test
    fun `error code arrays are invalid`() {
        val result = SensorReadingJsonParser.parse(
            fixture("sensor-reading-errors-array-invalid.json"),
        )
        assertTrue(result is SensorReadingParseResult.Invalid)
    }

    private fun fixture(name: String): String =
        checkNotNull(javaClass.classLoader?.getResource(name)) { "Missing fixture $name" }.readText()
}
