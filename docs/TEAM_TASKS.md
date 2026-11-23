# Team Task Boundaries

## Mohamed Elshahat — `feature/android`

Owns foundation, Android build files, shared models/interfaces, Firebase paths,
authentication/rules integration, CI, review, and final integration.

## Moamen Ayman — `feature/hardware`

Owns `hardware/**`: confirmed board and parts, Wokwi, firmware, calibration,
contract JSON, TLS, Firebase writer/authentication, reconnect and firmware
tests. He consumes `DATA_CONTRACT.md` and fixtures without changing them.

## Mohamed Desouky — `feature/weather`

Owns `weather/**` and, after this foundation lands,
`android/**/feature/weather/**`. He implements Open-Meteo DTOs, mapping,
repository, cache, UI state, and tests against `WeatherSnapshot` and
`WeatherRepository`.

## Mohamed Elkomy — `feature/ai-agent`

Owns `ai-agent/**` and `android/**/core/domain/recommendation/**`. He implements
the deterministic engine and tests against the shared input/result models. AI
explanation remains blocked until separate approval.

## Mohamed Osama — `feature/docs-testing`

Owns approved plant/soil research, test evidence, inventory, APK testing, bug
reports, and demo/fallback checklists. Plant and soil choices become thresholds
only after leader review.

Shared contracts, models, dependencies, paths, rules, navigation, and build
files require Mohamed Elshahat's approval.
