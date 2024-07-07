package ru.netology.markers.dto

import org.mapstruct.Mapper
import ru.netology.markers.entity.MapObjectEntity


data class MapObject(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val description: String?
)

@Mapper
interface MapObjectMapper {
    fun fromDto(mapObject: MapObject): MapObjectEntity
    fun toDto(mapObjectEntity: MapObjectEntity): MapObject
}

class MapObjectMapperImpl : MapObjectMapper {
    override fun fromDto(mapObject: MapObject) = MapObjectEntity(
        id = mapObject.id,
        latitude = mapObject.latitude,
        longitude = mapObject.longitude,
        name = mapObject.name,
        description = mapObject.description,
    )

    override fun toDto(mapObjectEntity: MapObjectEntity) = MapObject(
        id = mapObjectEntity.id,
        latitude = mapObjectEntity.latitude,
        longitude = mapObjectEntity.longitude,
        name = mapObjectEntity.name,
        description = mapObjectEntity.description,
    )

}