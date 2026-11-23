# Smart Agri MVP Scope

## Objective

Build a university prototype that reads agricultural sensor data, stores it
securely, displays it in an Android app, and produces explainable advisory
irrigation recommendations. It is not a production farming or automatic
irrigation system.

## Must Have

- ESP32-WROOM-32 DevKit reads a capacitive soil sensor and DHT22.
- Firmware validates readings and identifies physical versus simulated data.
- Authenticated, TLS-verified writes update Firebase RTDB latest and history.
- Android shows reading values, source, freshness, errors, weather, and device
  status.
- Open-Meteo weather for Zagazig is cached with its fetch time.
- A deterministic recommendation uses only validated sensor, weather,
  calibration, and plant-profile data.
- Authentication, authorization, error/offline states, fixtures, and tests.

## Deferred Until the First End-to-End Milestone

- Recent-history chart.
- AI-generated advisory explanation.

## Out of Scope

- Pump, relay, valve, mains-voltage, or automatic irrigation control.
- Notifications or a custom backend.
- Scientific soil-water-content claims or guaranteed crop/water-saving claims.

## Completion Boundary

The MVP is complete only when a physical or visibly simulated ESP32 reading
passes through authenticated Firebase RTDB storage and is displayed correctly
by Android with freshness and safety states. Each component must also pass its
gate in `TEST_PLAN.md`.
