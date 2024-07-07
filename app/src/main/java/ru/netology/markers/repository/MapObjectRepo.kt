package ru.netology.markers.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.markers.dto.MapObject

interface MapObjectRepo {
    val data: Flow<List<MapObject>>
    suspend fun getById(id: Long): MapObject?
    suspend fun removeById(id: Long)
    suspend fun save(mapObject: MapObject)
}