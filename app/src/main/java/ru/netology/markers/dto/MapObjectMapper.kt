package ru.netology.markers.dto

import org.mapstruct.Mapper
import ru.netology.markers.entity.MapObjectEntity


fun List<MapObjectEntity>.toDto(): List<MapObject> = map(MapObjectMapperImpl::toDto)

fun List<MapObject>.toEntity(): List<MapObjectEntity> = map(MapObjectMapperImpl::fromDto)

@Mapper
interface MapObjectMapper {
    fun fromDto(mapObject: MapObject): MapObjectEntity
    fun toDto(mapObjectEntity: MapObjectEntity): MapObject
}

object MapObjectMapperImpl : MapObjectMapper {
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