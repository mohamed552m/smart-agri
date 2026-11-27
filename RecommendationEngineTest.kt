import kotlin.test.Test
import kotlin.test.assertEquals

class RecommendationEngineTest {

    private val engine = RecommendationEngine()

    @Test
    fun drySoilAndLowRainShouldRecommendIrrigation() {

        val data = PlantData(
            plantType = "mint",
            soilMoisture = 20.0,
            temperatureC = 28.0,
            rainProbability = 10.0,
            readingAgeMinutes = 10,
            soilMoistureMin = 35.0,
            soilMoistureMax = 70.0,
            maxRainProbabilityForIrrigation = 50.0
        )

        val result = engine.recommend(data)

        assertEquals(
            RecommendationStatus.IRRIGATE,
            result.status
        )
    }

    @Test
    fun highRainProbabilityShouldNotRecommendIrrigation() {

        val data = PlantData(
            plantType = "mint",
            soilMoisture = 20.0,
            temperatureC = 28.0,
            rainProbability = 80.0,
            readingAgeMinutes = 10,
            soilMoistureMin = 35.0,
            soilMoistureMax = 70.0,
            maxRainProbabilityForIrrigation = 50.0
        )

        val result = engine.recommend(data)

        assertEquals(
            RecommendationStatus.NO_IRRIGATION,
            result.status
        )
    }

    @Test
    fun wetSoilShouldNotRecommendIrrigation() {

        val data = PlantData(
            plantType = "mint",
            soilMoisture = 60.0,
            temperatureC = 28.0,
            rainProbability = 10.0,
            readingAgeMinutes = 10,
            soilMoistureMin = 35.0,
            soilMoistureMax = 70.0,
            maxRainProbabilityForIrrigation = 50.0
        )

        val result = engine.recommend(data)

        assertEquals(
            RecommendationStatus.NO_IRRIGATION,
            result.status
        )
    }

    @Test
    fun missingSoilMoistureShouldReturnInsufficientData() {

        val data = PlantData(
            plantType = "mint",
            soilMoisture = null,
            temperatureC = 28.0,
            rainProbability = 10.0,
            readingAgeMinutes = 10,
            soilMoistureMin = 35.0,
            soilMoistureMax = 70.0,
            maxRainProbabilityForIrrigation = 50.0
        )

        val result = engine.recommend(data)

        assertEquals(
            RecommendationStatus.INSUFFICIENT_DATA,
            result.status
        )
    }

    @Test
    fun oldReadingShouldReturnInsufficientData() {

        val data = PlantData(
            plantType = "mint",
            soilMoisture = 20.0,
            temperatureC = 28.0,
            rainProbability = 10.0,
            readingAgeMinutes = 120,
            soilMoistureMin = 35.0,
            soilMoistureMax = 70.0,
            maxRainProbabilityForIrrigation = 50.0
        )

        val result = engine.recommend(data)

        assertEquals(
            RecommendationStatus.INSUFFICIENT_DATA,
            result.status
        )
    }
}