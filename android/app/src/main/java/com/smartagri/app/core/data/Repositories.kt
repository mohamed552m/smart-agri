package com.smartagri.app.core.data

import com.smartagri.app.core.model.DataState
import com.smartagri.app.core.model.PlantProfile
import com.smartagri.app.core.model.SensorReading
import com.smartagri.app.core.model.WeatherSnapshot
import kotlinx.coroutines.flow.Flow

interface SensorReadingRepository {
    fun observeLatest(deviceId: String): Flow<DataState<SensorReading>>
    suspend fun getRecentHistory(deviceId: String, limit: Int = 50): DataState<List<SensorReading>>
}

interface WeatherRepository {
    fun observeWeather(locationId: String): Flow<DataState<WeatherSnapshot>>
    suspend fun refresh(locationId: String): DataState<WeatherSnapshot>
}

interface PlantProfileRepository {
    suspend fun getProfile(profileId: String): DataState<PlantProfile>
}
