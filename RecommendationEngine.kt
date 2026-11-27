class RecommendationEngine {

    fun recommend(data: PlantData): RecommendationResult {

        if (RecommendationRules.hasMissingData(data)) {
            return RecommendationResult(
                status = RecommendationStatus.INSUFFICIENT_DATA,
                reason = "Required sensor or weather data is missing."
            )
        }

        if (RecommendationRules.isReadingTooOld(data.readingAgeMinutes)) {
            return RecommendationResult(
                status = RecommendationStatus.INSUFFICIENT_DATA,
                reason = "The sensor reading is missing or too old."
            )
        }

        if (RecommendationRules.isSoilWetEnough(data)) {
            return RecommendationResult(
                status = RecommendationStatus.NO_IRRIGATION,
                reason = "Soil moisture is already high enough for this plant."
            )
        }

        if (RecommendationRules.rainIsLikely(data)) {
            return RecommendationResult(
                status = RecommendationStatus.NO_IRRIGATION,
                reason = "Rain probability is high, so irrigation is not recommended now."
            )
        }

        if (RecommendationRules.isSoilTooDry(data)) {
            return RecommendationResult(
                status = RecommendationStatus.IRRIGATE,
                reason = "Soil moisture is below the configured minimum and rain probability is low."
            )
        }

        return RecommendationResult(
            status = RecommendationStatus.NO_IRRIGATION,
            reason = "Soil moisture is within the configured range."
        )
    }
}