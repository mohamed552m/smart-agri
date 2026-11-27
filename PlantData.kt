data class PlantData(
    val plantType: String,
    val soilMoisture: Double?,
    val temperatureC: Double?,
    val rainProbability: Double?,
    val readingAgeMinutes: Int?,
    val soilMoistureMin: Double,
    val soilMoistureMax: Double,
    val maxRainProbabilityForIrrigation: Double
)