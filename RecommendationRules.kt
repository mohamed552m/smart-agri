object RecommendationRules {

    const val MAX_READING_AGE_MINUTES = 60

    fun isReadingTooOld(ageMinutes: Int?): Boolean {
        return ageMinutes == null ||
                ageMinutes < 0 ||
                ageMinutes > MAX_READING_AGE_MINUTES
    }

    fun hasMissingData(data: PlantData): Boolean {
        return data.soilMoisture == null ||
                data.temperatureC == null ||
                data.rainProbability == null ||
                data.readingAgeMinutes == null
    }

    fun isSoilTooDry(data: PlantData): Boolean {
        val moisture = data.soilMoisture ?: return false
        return moisture < data.soilMoistureMin
    }

    fun isSoilWetEnough(data: PlantData): Boolean {
        val moisture = data.soilMoisture ?: return false
        return moisture >= data.soilMoistureMax
    }

    fun rainIsLikely(data: PlantData): Boolean {
        val rain = data.rainProbability ?: return false
        return rain >= data.maxRainProbabilityForIrrigation
    }
}