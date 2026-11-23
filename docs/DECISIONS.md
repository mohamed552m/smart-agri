# Architecture Decision Log

## Accepted

| ID | Decision |
| --- | --- |
| D-001 | Android is a single Kotlin/Compose app module under `android/app`. |
| D-002 | Shared contract version starts at 1; timestamps are epoch milliseconds. |
| D-003 | Firebase RTDB stores latest plus periodic history at approved paths. |
| D-004 | Physical and simulated data use different device IDs and visible labels. |
| D-005 | Freshness uses measurement time; arrival time cannot refresh old data. |
| D-006 | Persistent ESP32 queue and full offline history cache are outside MVP. |
| D-007 | Normalized moisture is a unitless calibrated indicator, not scientific water content. |
| D-008 | Missing calibration/plant data prevents an irrigation recommendation. |
| D-009 | Missing/stale weather while soil is dry produces manual-check behavior, not unconditional irrigation. |
| D-010 | Weather location is Zagazig, Egypt, identified in code as `ZAGAZIG_EG`. |
| D-011 | Hardware owner confirms the exact ESP32-WROOM-32 DevKit variant and parts. |
| D-012 | Plant/soil owner supplies researched profile values; no universal thresholds. |
| D-013 | Firebase Android BoM 34.14.1 is pinned because BoM 35.2.0 resolves Auth 25.0.0, whose Kotlin 2.4 metadata is incompatible with the approved Kotlin 2.2.21 compiler. The tested resolution is Auth 24.1.0 and RTDB 22.0.1. |

Firebase version mappings are checked against the official
[Firebase Android release notes](https://firebase.google.com/support/release-notes/android)
and then verified with Gradle `dependencyInsight`; availability alone is not a
reason to upgrade the shared stack.

## Requires External Approval or Evidence

- Firebase project creation, users, RTDB instance, rules deployment, and private
  `google-services.json` distribution.
- Exact physical hardware purchase and calibration values.
- Plant/soil profile values and their cited basis.
- Optional AI explanation after the first end-to-end milestone.
