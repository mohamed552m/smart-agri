# Smart Agri Architecture

## Components

```text
ESP32 + sensors
  -> verified TLS + Firebase Authentication
  -> Firebase RTDB latest + periodic history
  -> Android sensor repository

Open-Meteo
  -> Android weather repository + timestamped DataStore cache

Sensor reading + weather + plant profile
  -> pure deterministic Recommendation Engine
  -> advisory UI only
```

## Ownership Boundaries

- `hardware/`: firmware, simulation, calibration, TLS, authentication, writes.
- `android/`: single Android app and integration foundation.
- `android/**/feature/weather/` and `weather/`: weather owner implementation.
- `android/**/core/domain/recommendation/`: recommendation owner implementation.
- `contracts/` and `docs/`: shared and changed only with leader approval.

## Android Layers

```text
feature UI
  -> repository interfaces in core/data
  -> implementations (Firebase or Open-Meteo)
  -> shared models in core/model
```

The app uses one `app` module. Feature packages must not duplicate shared
models, repositories, Firebase clients, or recommendation rules.

## Data Flow Rules

- Firebase wire values are mapped and validated before becoming domain models.
- RTDB integers are treated as `Long` at the wire boundary.
- UI consumes typed `DataState` values, never raw exceptions.
- Freshness comes from `measuredAtEpochMs`, not Firebase arrival time.
- Simulated data stays distinguishable through the entire flow.
- The Recommendation Engine is pure and performs no network, Firebase, UI, or
  AI calls.

## Security Boundary

The ESP32 writer and Android viewer use separate authenticated identities.
Rules restrict each writer to its mapped device and each viewer to explicitly
granted devices. Secrets remain outside Git.
