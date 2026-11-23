package com.smartagri.app.core.model

data class PlantProfile(
    val profileId: String,
    val plantName: String,
    val soilType: String,
    val calibrationVersion: String,
    val dryThresholdPercent: Double,
    val targetThresholdPercent: Double,
    val significantRainThresholdMm: Double,
    val updatedAtEpochMs: Long,
) {
    init {
        require(profileId.isNotBlank())
        require(plantName.isNotBlank())
        require(soilType.isNotBlank())
        require(calibrationVersion.isNotBlank())
        require(dryThresholdPercent in 0.0..100.0)
        require(targetThresholdPercent in 0.0..100.0)
        require(dryThresholdPercent < targetThresholdPercent)
        require(significantRainThresholdMm >= 0.0)
        require(updatedAtEpochMs > 0)
    }
}
