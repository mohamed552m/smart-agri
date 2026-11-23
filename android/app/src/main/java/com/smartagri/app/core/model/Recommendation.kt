package com.smartagri.app.core.model

data class Recommendation(
    val action: RecommendationAction,
    val safetyState: RecommendationSafetyState,
    val reasonCodes: Set<String>,
    val generatedAtEpochMs: Long,
    val sensorMeasuredAtEpochMs: Long?,
    val weatherFetchedAtEpochMs: Long?,
    val plantProfileId: String?,
)

enum class RecommendationAction {
    INSUFFICIENT_DATA,
    NO_ACTION,
    MONITOR,
    MANUAL_CHECK_REQUIRED,
    WAIT_FOR_RAIN,
    IRRIGATE_SOON,
}

enum class RecommendationSafetyState {
    ADVISORY_ONLY,
    DEMO_ONLY,
}
