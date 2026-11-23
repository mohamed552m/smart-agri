# Smart Agri Data Contract — Version 1

This document and `contracts/fixtures/v1/` are the authoritative shared data
contract. Producers and consumers must not change fields, paths, types, or
semantics independently.

## General Encoding Rules

- `schemaVersion` is `1`.
- JSON field names use `lowerCamelCase`.
- Timestamps are UTC Unix epoch milliseconds stored as integers and read as
  Kotlin `Long`.
- Other persisted integers are also read as `Long` at the wire boundary.
- Decimal values are `Double`.
- Enum values are uppercase strings. Unknown values must not crash a consumer;
  they make the affected object invalid or unsupported.
- Required missing fields make the object invalid.
- Optional missing fields map to `null` or the documented empty domain value.
- Producers omit optional values instead of writing JSON/RTDB `null`, because
  an RTDB null write deletes a node.
- Arrays are not used for persisted error codes. Use a map of code to `true`.
- Omit `errorCodes` when no errors exist; do not write an empty array.

## Firebase RTDB Paths

```text
/deviceOwners/{deviceId}
/access/{viewerUid}/{deviceId}
/devices/{deviceId}/metadata
/devices/{deviceId}/latest
/readings/{deviceId}/{readingId}
```

Path segments must be nonblank and must not contain `.`, `#`, `$`, `[`, `]`,
or `/`.

## SensorReading

| Field | Wire type | Required | Meaning |
| --- | --- | --- | --- |
| `schemaVersion` | integer | yes | Contract version; exactly `1` |
| `readingId` | string | yes | Unique immutable reading ID |
| `deviceId` | string | yes | Device identity and path owner |
| `dataSource` | string enum | yes | `PHYSICAL` or `SIMULATED` |
| `bootId` | string | yes | Changes after every device boot |
| `sequence` | integer | yes | Monotonic within one boot |
| `measuredAtEpochMs` | integer | yes | Time the sample was measured |
| `receivedAtEpochMs` | integer | yes | Firebase-resolved server arrival time |
| `timeQuality` | string enum | yes | `SYNCED` or `UNSYNCED` |
| `soilMoistureRaw` | integer | yes | Raw 12-bit ADC reading, `0..4095` |
| `normalizedMoisturePercent` | decimal | conditional | Unitless calibrated value, `0..100` |
| `airTemperatureC` | decimal | no | DHT22 temperature when available |
| `airHumidityPercent` | decimal | no | DHT22 relative humidity, `0..100` |
| `calibrationVersion` | string | conditional | Required when normalized value exists |
| `sensorStatus` | string enum | yes | `OK`, `PARTIAL`, or `ERROR` |
| `firmwareVersion` | string | yes | Firmware build/version identifier |
| `errorCodes` | map string->boolean | no | Every present value must be `true` |
| `simulator` | string enum | conditional | `WOKWI` only for simulated data |

Physical and simulated producers use separate IDs, for example
`smart-agri-physical-01` and `smart-agri-sim-01`. A simulated reading requires
`simulator=WOKWI`; a physical reading must omit `simulator`.

Example physical reading:

```json
{
  "schemaVersion": 1,
  "readingId": "boot-a-42",
  "deviceId": "smart-agri-physical-01",
  "dataSource": "PHYSICAL",
  "bootId": "boot-a",
  "sequence": 42,
  "measuredAtEpochMs": 1774320000000,
  "receivedAtEpochMs": 1774320001200,
  "timeQuality": "SYNCED",
  "soilMoistureRaw": 2380,
  "normalizedMoisturePercent": 47.5,
  "airTemperatureC": 26.4,
  "airHumidityPercent": 58.2,
  "calibrationVersion": "soil-cal-v1",
  "sensorStatus": "OK",
  "firmwareVersion": "0.1.0"
}
```

On write, `receivedAtEpochMs` uses the RTDB server-timestamp placeholder
`{ ".sv": "timestamp" }`. On read it must have resolved to an integer. A map
placeholder remaining in a read payload is invalid.

## Freshness and Reconnection

- Android calculates age from `measuredAtEpochMs`.
- More than five minutes old is `STALE` for recommendation purposes.
- More than two minutes in the future is `INVALID_FUTURE`.
- `timeQuality=UNSYNCED` is `UNSYNCED` and cannot enter recommendations.
- `receivedAtEpochMs` is display/debug metadata only; it never makes an old
  measurement fresh.
- After reconnection, firmware measures and uploads a new sample.
- Persistent firmware buffering is outside the MVP. If queued history is added
  later, it must not replace a newer `latest` sample.
- RTDB validation for `latest` must reject a measurement timestamp older than
  the current latest timestamp.

## WeatherSnapshot

| Field | Kotlin type | Meaning |
| --- | --- | --- |
| `locationId` | `String` | Fixed location identifier `ZAGAZIG_EG` |
| `observedAtEpochMs` | `Long` | Weather observation/forecast time |
| `fetchedAtEpochMs` | `Long` | Successful API fetch time |
| `temperatureC` | `Double?` | Temperature when supplied |
| `relativeHumidityPercent` | `Double?` | Relative humidity |
| `precipitationProbabilityPercent` | `Double?` | `0..100` |
| `precipitationMm` | `Double?` | Forecast precipitation |
| `weatherCode` | `Long?` | Open-Meteo/WMO code |
| `source` | enum | `OPEN_METEO` or `CACHE` |

The weather owner defines API DTOs separately and maps them to this model.
Cached weather retains `fetchedAtEpochMs`; missing or stale weather must remain
visible and cannot silently become a valid irrigation input.

## PlantProfile

A profile contains an ID, plant name, soil type, calibration version, dry and
target thresholds on the same normalized sensor scale, a significant-rain
threshold, and an update timestamp. The dry threshold must be below the target
threshold. No universal threshold is permitted.

`normalizedMoisturePercent` is a unitless sensor indicator derived from local
dry and wet calibration references. It is not volumetric water content, `%VWC`,
gravimetric water content, or a laboratory measurement.

## Recommendation

The shared result carries an action, safety state, reason-code set, generation
time, and input timestamps. The action values are:

```text
INSUFFICIENT_DATA
NO_ACTION
MONITOR
MANUAL_CHECK_REQUIRED
WAIT_FOR_RAIN
IRRIGATE_SOON
```

Safety is `ADVISORY_ONLY` for validated physical data and `DEMO_ONLY` for any
simulated result. This contract defines the result shape only; the assigned
recommendation owner implements the engine under the rules in `AGENTS.md`.

## History and Atomic Writes

Firmware performs one atomic multi-location update for `latest` and periodic
history. Latest is written for each accepted sample; history is approximately
every five minutes. Android history queries are limited to the newest 50
readings. History storage is required; a chart and full offline history cache
are optional.

## Compatibility

Version 1 consumers ignore unknown extra fields but never unknown required enum
semantics. Breaking field/type/meaning changes require a new schema version,
fixtures, migration notes, and leader approval.
