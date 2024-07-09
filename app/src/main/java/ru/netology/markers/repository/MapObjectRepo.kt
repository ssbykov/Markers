package ru.netology.markers.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.markers.dto.LocalMapObject

interface MapObjectRepo {
    val data: Flow<List<LocalMapObject>>
    fun getById(id: Long): LocalMapObject?
    suspend fun removeById(id: Long)
    suspend fun save(localMapObject: LocalMapObject)
}