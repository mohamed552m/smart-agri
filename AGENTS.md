# Smart Agri — Instructions for Team Members and AI Coding Assistants

This file is the project-wide source of truth for how humans and AI coding
assistants must work in this repository. Read it completely before planning,
editing, generating code, installing dependencies, or opening a pull request.

## 1. Current Status and Approval Gate

- The project is a university prototype for monitoring agricultural conditions
  and producing explainable irrigation recommendations.
- The repository is currently in the shared-foundation stage.
- Do not claim that Android, Firebase, ESP32, sensors, or end-to-end integration
  work unless the relevant smoke test has actually passed and evidence exists.
- Do not add optional features until all Must Have features pass end-to-end.
- AI assistant and history chart work must wait until the first successful
  end-to-end milestone.
- Pump control, automatic irrigation, notifications, and a custom backend are
  outside the MVP.

## 2. Instruction Precedence

When instructions conflict, follow this order:

1. Security and safety requirements in this file.
2. `docs/DATA_CONTRACT.md`.
3. `docs/ARCHITECTURE.md`.
4. `docs/PROJECT_SCOPE.md`.
5. `docs/DECISIONS.md`.
6. `docs/TEAM_TASKS.md` and `docs/TEST_PLAN.md`.
7. The current task or pull request description.

If a required document does not exist yet, stop at the affected boundary and
report the missing foundation. Do not invent a competing model, interface,
Firebase path, dependency version, or business rule.

## 3. Approved MVP

### Must Have

- ESP32 reads a capacitive soil sensor and DHT22.
- Firmware validates and calibrates sensor readings.
- ESP32 authenticates and writes latest and historical readings to Firebase
  Realtime Database over verified TLS.
- Android displays the latest reading, data source, freshness, sensor errors,
  weather snapshot, and device status.
- Open-Meteo supplies weather data with caching and visible data age.
- A deterministic Recommendation Engine produces an explainable advisory
  result from validated sensor, weather, calibration, and plant-profile data.
- Authentication, authorization, offline/error states, fixtures, unit tests,
  and a clearly labelled simulated fallback are required.

### Should Have

- A chart for recent history.
- An advisory AI explanation after the core system is stable.

### Out of Scope

- Manual or automatic pump control.
- Relays, valves, and mains-voltage work.
- Notifications.
- A custom backend.
- Production farming claims, automatic crop decisions, or claims of proven
  water savings.

## 4. Approved Technology Stack

Do not upgrade, downgrade, replace, or add shared dependencies without a
documented decision approved by Mohamed Elshahat.

### Android

| Component | Approved version |
| --- | --- |
| JDK | 17 |
| Android Gradle Plugin | 8.13.2 |
| Gradle Wrapper | 8.13 |
| Kotlin and Compose Compiler plugin | 2.2.21 |
| compileSdk / targetSdk / minSdk | 36 / 36 / 26 |
| Jetpack Compose BOM | 2026.04.01 |
| AndroidX Core KTX | 1.17.0 |
| Activity Compose | 1.12.4 |
| Lifecycle | 2.9.4 |
| Navigation Compose | 2.9.8 |
| DataStore | 1.2.1 |
| kotlinx.coroutines | 1.10.2 |
| kotlinx.serialization | 1.9.0 |
| Retrofit and Kotlin serialization converter | 3.0.0 |
| Firebase BoM | 34.14.1 |
| Firebase Authentication, via BoM | 24.1.0 |
| Firebase Realtime Database, via BoM | 22.0.1 |
| Google Services Gradle plugin | 4.6.0 |

Use the main Firebase modules. Do not add deprecated separate Firebase `-ktx`
artifacts. Do not add Hilt, Room, WorkManager, or extra architecture libraries
without an approved decision.

### ESP32

| Component | Approved version or choice |
| --- | --- |
| Board | Classic ESP32-WROOM-32 DevKit |
| Framework | Arduino |
| Arduino-ESP32 core | 3.3.12 |
| FirebaseClient | 2.2.13 |
| DHT sensor library for ESPx | 1.19.0 |
| Soil sensor ADC pin | GPIO34 / ADC1 |
| DHT22 data pin | GPIO4 |
| Simulation | Wokwi with a potentiometer for soil ADC |

### External Services

- Firebase Authentication and Realtime Database on the Spark plan.
- Open-Meteo Forecast API with a fixed approved location and visible
  attribution.
- Firebase AI Logic is optional and must not be added before the core
  end-to-end checkpoint and a separate approval.

## 5. Repository Structure

The intended structure is:

```text
AGENTS.md
contracts/
  fixtures/v1/
  schemas/
docs/
  PROJECT_SCOPE.md
  ARCHITECTURE.md
  DATA_CONTRACT.md
  TEAM_TASKS.md
  GIT_WORKFLOW.md
  TEST_PLAN.md
  DECISIONS.md
android/
hardware/
weather/
ai-agent/
```

The Android app remains a single app module. The intended package root is
`com.smartagri.app`, with these packages:

```text
core/model
core/data
core/data/firebase
core/domain/recommendation
core/ui
feature/dashboard
feature/weather
feature/assistant
navigation
```

Do not create duplicate models, repositories, recommendation engines, or
Firebase clients in feature packages.

## 6. File and Branch Ownership

### Mohamed Elshahat — `feature/android`

Owns:

- Project foundation and integration.
- Root and Android Gradle files, Gradle wrapper, and version catalog.
- Android Manifest, Application, MainActivity, and Navigation.
- Shared Android wire models, domain models, interfaces, and data-state types.
- Firebase Android setup, database rules, authentication, and CI workflows.
- Final review and approval of shared-file changes.

### Moamen Ayman — `feature/hardware`

Owns:

- `hardware/**`.
- ESP32 firmware, Wokwi, sensors, wiring, calibration, Firebase writer, TLS,
  authentication, reconnect behavior, and firmware tests.

Must not modify Android, shared contracts, or shared documentation without an
approved request.

### Mohamed Desouky — `feature/weather`

Owns:

- `weather/**`.
- `android/**/feature/weather/**` after the Android foundation exists.
- Open-Meteo DTOs, mapper, repository implementation, cache, UI state, weather
  UI, fixtures, and tests.

Must not modify Gradle, Manifest, Navigation, or shared models.

### Mohamed Elkomy — `feature/ai-agent`

Owns:

- `ai-agent/**`.
- `android/**/core/domain/recommendation/**`.
- `android/**/feature/assistant/**` after separate AI approval.
- Deterministic Recommendation Engine, reason codes, and unit tests.

Must not modify sensor/weather repositories, Firebase rules, Gradle,
Navigation, or shared models.

### Mohamed Osama — `feature/docs-testing`

Owns:

- Test evidence and approved research/testing documentation.
- Plant/soil research, hardware inventory, APK exploratory tests, regression
  evidence, bug reports, and demo/fallback checklists.

Must not modify Gradle, firmware, shared models, or thresholds without review.

### Shared Files

Only Mohamed Elshahat may merge changes to shared dependencies, contracts,
models, interfaces, Firebase paths, rules, navigation, or build files. Other
members must request the change and describe why it is required.

## 7. Git and Pull Request Rules

- Never push directly to `main`.
- Never force-push, rewrite shared history, or use destructive Git commands.
- Use the assigned feature branch only.
- Update a feature branch using merge, not rebase:

```text
git fetch origin
git switch feature/<assigned-branch>
git merge origin/main
```

- Commit messages use `type(scope): summary`, for example:

```text
feat(weather): map Open-Meteo forecast response
test(recommendation): cover stale sensor reading
fix(firebase): handle permission denied state
docs(hardware): document soil calibration
```

- Pull requests must state what changed, why, tests executed, evidence, known
  limitations, and whether any shared contract is affected.
- A pull request must have green required checks and leader review before
  merge.
- Do not combine unrelated foundation and feature work in one pull request.

## 8. Shared Data Contract Rules

`docs/DATA_CONTRACT.md` and `contracts/fixtures/v1/` become authoritative once
created. Until then, use these mandatory rules:

- Schema version starts at `1`.
- JSON names use `lowerCamelCase`.
- Timestamps use UTC Unix epoch milliseconds as `Long`.
- Persisted integer values are read as `Long` at the wire boundary.
- Decimal values are `Double`.
- Enum values are uppercase strings and must be parsed safely.
- Unknown enum values must not crash the app.
- Required missing fields make a reading invalid.
- Optional missing fields map to `null` or a documented safe default.
- Do not persist `null`; a Firebase RTDB null write deletes a node.
- Do not persist arrays for error codes. Use an optional map of code to `true`.
- Omit `errorCodes` when there are no errors.

Every sensor reading must include:

```text
schemaVersion
readingId
deviceId
dataSource            PHYSICAL or SIMULATED
bootId
sequence
measuredAtEpochMs
receivedAtEpochMs     Firebase server timestamp
timeQuality           SYNCED or UNSYNCED
soilMoistureRaw
normalizedMoisturePercent, when calibration is valid
airTemperatureC, when available
airHumidityPercent, when available
calibrationVersion, when calibrated
sensorStatus
firmwareVersion
errorCodes, omitted when empty
simulator, WOKWI only for simulated data
```

Use separate device IDs for physical and simulated sources, for example:

```text
smart-agri-physical-01
smart-agri-sim-01
```

Simulated data must always display a visible `SIMULATED DATA` label. A
recommendation based on simulated data must use `safetyState=DEMO_ONLY`.

## 9. Time, Freshness, and Reconnection

- `measuredAtEpochMs` is the time the sample was physically or virtually
  measured.
- `receivedAtEpochMs` is the Firebase server arrival time and must never be
  used to make an old measurement appear fresh.
- Android computes freshness from `measuredAtEpochMs`.
- A reading older than five minutes is stale for recommendation purposes.
- A reading more than two minutes in the future is invalid.
- `timeQuality=UNSYNCED` prevents the reading from entering the recommendation
  engine.
- The ESP32 must synchronize time before authenticated TLS and before creating
  recommendation-eligible readings.
- Persistent ESP32 offline buffering is outside the MVP.
- After Wi-Fi reconnection, firmware takes and uploads a new measurement.
- If queued history is added later, old samples may go to history only; they
  must not replace a newer `latest` reading.
- Firebase validation for `latest` must reject a measurement timestamp older
  than the existing latest timestamp.

## 10. Firebase Paths and Security

The approved paths are:

```text
/deviceOwners/{deviceId}
/access/{viewerUid}/{deviceId}
/devices/{deviceId}/metadata
/devices/{deviceId}/latest
/readings/{deviceId}/{readingId}
```

- An ESP32 uses a dedicated Firebase Email/Password account.
- A device may write only to the device ID mapped to its authenticated UID.
- Android signs in as a viewer and may read only explicitly granted devices.
- Android must not write sensor readings.
- Client applications cannot edit `deviceOwners` or `access` mappings.
- Do not use public read/write rules or rules that merely check `auth != null`.
- Required fields, ranges, source type, timestamps, and writer ownership must
  be validated by rules where possible and again by Android mappers.
- Use atomic multi-location writes for current `latest` and periodic history.
- Write history at approximately five-minute intervals and query only the last
  50 readings in the MVP.

## 11. Secrets and TLS

Never commit or paste into an AI assistant:

- Wi-Fi SSID or password.
- ESP32 Firebase email/password.
- Firebase service-account files or private keys.
- AI private keys.
- App Check debug tokens.
- Signing keystores or passwords.
- Real `secrets.h` or other local secret files.

Use ignored local configuration plus safe `.example` templates. For a public
student repository, keep `google-services.json` untracked and share it through
an approved private channel.

ESP32 requirements:

- Use `WiFiClientSecure` with CA verification.
- Never use `setInsecure()` in committed or demo firmware.
- Synchronize the clock before TLS validation.
- Keep `FirebaseApp::loop()` running without long blocking delays so tokens can
  refresh.
- Never print authentication tokens, passwords, or full credential objects.
- A wrong CA, invalid clock, wrong account, or unauthorized path must fail in
  tests.

## 12. Recommendation Engine Safety Rules

The Recommendation Engine is a pure, deterministic domain component. It must
not perform network, Firebase, UI, or AI calls.

Rules:

- Missing or mismatched calibration/plant profile: `INSUFFICIENT_DATA`.
- Missing, invalid, future, unsynchronized, or stale soil reading:
  `INSUFFICIENT_DATA`.
- Moisture at or above the target: `NO_ACTION`; missing weather is shown as a
  warning.
- Moisture between dry and target thresholds: `MONITOR`.
- Moisture below the dry threshold with missing or stale weather:
  `MANUAL_CHECK_REQUIRED`.
- Moisture below dry threshold with valid significant rain forecast:
  `WAIT_FOR_RAIN`.
- Moisture below dry threshold with valid weather and no significant rain:
  `IRRIGATE_SOON`.
- AI cannot change, suppress, or override the deterministic result.
- No recommendation can directly control hardware.

Weather freshness is evaluated only through the shared
`WeatherFreshnessEvaluator`: older than three hours is stale, and more than two
minutes in the future is invalid.

Do not use universal moisture thresholds. Plant and soil thresholds must come
from an approved profile and be interpreted on the same calibrated normalized
scale as the sensor.

`normalizedMoisturePercent` is a unitless normalized indicator derived from the
sensor's dry and wet references. It is not volumetric water content (`%VWC`),
gravimetric water content, or a laboratory measurement. UI and documentation
must not describe it as scientifically measured soil water content.

## 13. Offline and History Requirements

- Firebase latest-reading persistence on Android is mandatory.
- The latest node should be kept synchronized for the selected device.
- Weather's last successful snapshot is cached in DataStore and must retain its
  fetch timestamp.
- Cached data must display age and `OFFLINE` or `STALE` state.
- Stale cached sensor data cannot produce an active irrigation recommendation.
- Firebase history storage is mandatory at the agreed lower frequency.
- A history chart is optional.
- Full offline history caching and an ESP32 persistent upload queue are outside
  the MVP.
- Never replace missing live data with fake data unless the UI visibly labels
  the entire source as simulated.

## 14. Error and UI State Standards

Repositories expose typed states, not raw exceptions:

```text
Loading
Success
Empty
Error(Network | Authentication | PermissionDenied | InvalidData |
      StaleData | Unknown)
```

- UI must show loading, empty, permission, network, stale, partial-sensor, and
  invalid-data states.
- A DHT failure may produce a `PARTIAL` reading when the soil reading remains
  valid.
- An invalid soil reading cannot produce an irrigation recommendation.
- Never silently coerce invalid input into a plausible sensor value.

## 15. Required Tests and Definition of Done

### Android Foundation Gate

The following must pass:

```text
gradlew clean
gradlew lintDebug
gradlew testDebugUnitTest
gradlew assembleDebug
```

Also verify fixture parsing, missing required/optional fields, unknown enums,
stale/future timestamps, physical/simulated source identity, server timestamp
deserialization, shared path generation, and contract/model compilation.

### Android Integration Gate

After the relevant implementations exist, verify Firebase permission denial,
server timestamp mapping from a real `DataSnapshot`, process restart, offline
display, visible physical/simulated labels, and cached-data age. These checks do
not block merging the shared foundation before those features exist.

### ESP32 Gate

- Blink compile/upload with Arduino-ESP32 3.3.12.
- ADC and DHT valid/failure tests.
- Contract-compatible serial JSON.
- Verified TLS; wrong CA must fail.
- Firebase authentication and authorized/unauthorized path tests.
- Successful server timestamp and atomic RTDB write.
- At least 65 minutes of operation followed by another successful write to
  prove token refresh.
- Wi-Fi disconnect/reconnect test proving old data does not replace latest.
- Physical calibration evidence for dry and wet references.

### Feature Definition of Done

A task is not done until:

- It follows this file and the approved contracts.
- It remains inside assigned ownership boundaries.
- Relevant automated and manual tests pass.
- Test commands and results are reported.
- No credentials or generated local files are tracked.
- Loading/error/offline/stale cases are handled.
- Documentation and fixtures are updated when behavior changes.
- Known limitations and remaining blockers are stated honestly.

## 16. Automated Checks

Required pull-request checks should include:

- Android lint, unit tests, and debug assembly.
- Firmware compile without real credentials.
- Contract fixture validation.
- `git diff --check`.
- Checks for accidentally tracked credential files or obvious secret values.

Do not bypass, disable, or weaken a failing check to merge a feature. Fix the
problem or report the blocker.

## 17. Rules for AI Coding Assistants

Every AI assistant must:

1. Read this file and the relevant shared documentation completely.
2. Inspect the current repository, branch, and existing implementation before
   proposing edits.
3. State which files it intends to modify and verify they are inside the
   member's ownership.
4. Reuse approved models, interfaces, fixtures, paths, and versions.
5. Make the smallest scoped change required by the assigned task.
6. Preserve unrelated team changes and never overwrite another member's work.
7. Run the required tests before declaring completion.
8. Report exact commands, results, limitations, and blockers.

An AI assistant must not:

- Independently change dependencies, SDK levels, models, interfaces, Firebase
  paths, security rules, thresholds, or architecture.
- Create duplicate abstractions to avoid waiting for the shared foundation.
- Add optional features while a Must Have path is incomplete.
- Insert fabricated data without an explicit simulated-data label.
- Claim that code, hardware, calibration, security, or integration was tested
  when it was not.
- Work around a blocker with an incompatible local solution.

When blocked, stop at the boundary, preserve working progress, and send the
team leader a concise report containing the missing input, affected task,
evidence, and safest next action.

## 18. Final Demo Rules

- Prefer physical sensor data.
- If hardware or connectivity fails, use the approved Wokwi/cached fallback
  with a permanent visible `SIMULATED DATA` label.
- Do not present simulated data as a physical measurement.
- Do not claim production readiness, scientifically measured soil water
  content, proven crop benefit, or proven water savings.
- The final demo must show data source, measurement time, freshness, errors,
  recommendation reasons, and advisory-only safety status.
