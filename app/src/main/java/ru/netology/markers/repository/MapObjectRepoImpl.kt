package ru.netology.markers.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.markers.dao.MapsDao
import ru.netology.markers.dto.LocalMapObject
import ru.netology.markers.dto.MapObjectMapperImpl
import ru.netology.markers.dto.toDto
import ru.netology.markers.entity.MapObjectEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapObjectRepoImpl @Inject constructor (
    private val dao: MapsDao
) : MapObjectRepo {
    override val data = dao.getAll()
        .map(List<MapObjectEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getById(id: Long): LocalMapObject? {
        val mapObject = dao.getById(id)
        return if (mapObject != null) {
            return MapObjectMapperImpl.toDto(mapObject)
        } else null
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }

    override suspend fun save(localMapObject: LocalMapObject) {
        dao.insert(MapObjectMapperImpl.fromDto(localMapObject))
    }
}