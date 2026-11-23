# Smart Agri Test Plan

## Foundation Gate

Run from the repository root:

```text
gradlew clean
gradlew lintDebug
gradlew testDebugUnitTest
gradlew assembleDebug
git diff --check
```

Unit tests cover valid physical/simulated/partial fixtures, missing required
fields, unknown enums, server timestamps as `Long`, path validation, and
fresh/stale/future/unsynchronized states.

## Android Integration Gate

- Authentication success/failure and permission-denied state.
- Latest reading survives process restart through Firebase persistence.
- Physical/simulated labels and `DEMO_ONLY` safety state.
- Offline/stale/empty/invalid/partial display states.
- Weather cache preserves fetch time and visible age.

## ESP32 Gate

- Blink compile/upload on the confirmed board using core 3.3.12.
- ADC and DHT success/failure; contract-compatible serial JSON.
- Verified TLS and explicit wrong-CA failure.
- Authorized authentication/write and unauthorized-path failure.
- Server timestamp and atomic latest/history write.
- A successful write after at least 65 minutes for token refresh.
- Disconnect/reconnect does not replace latest with old data.
- Physical dry/wet calibration evidence.

## End-to-End Gate

An authenticated physical reading, or permanently labelled Wokwi fallback,
travels through Firebase and is shown by Android with source, time, freshness,
errors, and advisory status. No component is marked complete without recorded
commands and evidence.
