package ru.netology.markers.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.markers.dao.MapsDao
import ru.netology.markers.dto.MapObject
import ru.netology.markers.dto.MapObjectMapperImpl
import ru.netology.markers.dto.toDto
import ru.netology.markers.entity.MapObjectEntity

class MapObjectRepoImpl(private val dao: MapsDao) : MapObjectRepo {
    override val data = dao.getAll()
        .map(List<MapObjectEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getById(id: Long): MapObject? {
        val mapObject = dao.getById(id)
        return if (mapObject != null) {
            return MapObjectMapperImpl.toDto(mapObject)
        } else null
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }

    override suspend fun save(mapObject: MapObject) {
        dao.insert(MapObjectMapperImpl.fromDto(mapObject))
    }
}