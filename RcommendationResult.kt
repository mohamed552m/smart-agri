enum class RecommendationStatus {
    IRRIGATE,
    NO_IRRIGATION,
    INSUFFICIENT_DATA
}

data class RecommendationResult(
    val status: RecommendationStatus,
    val reason: String
)