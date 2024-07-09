package ru.netology.markers.dto

import org.mapstruct.Mapper
import ru.netology.markers.entity.MapObjectEntity


fun List<MapObjectEntity>.toDto(): List<LocalMapObject> = map(MapObjectMapperImpl::toDto)

fun List<LocalMapObject>.toEntity(): List<MapObjectEntity> = map(MapObjectMapperImpl::fromDto)

@Mapper
interface MapObjectMapper {
    fun fromDto(localMapObject: LocalMapObject): MapObjectEntity
    fun toDto(mapObjectEntity: MapObjectEntity): LocalMapObject
}

object MapObjectMapperImpl : MapObjectMapper {
    override fun fromDto(localMapObject: LocalMapObject) = MapObjectEntity(
        id = localMapObject.id,
        latitude = localMapObject.latitude,
        longitude = localMapObject.longitude,
        name = localMapObject.name,
        description = localMapObject.description,
    )

    override fun toDto(mapObjectEntity: MapObjectEntity) = LocalMapObject(
        id = mapObjectEntity.id,
        latitude = mapObjectEntity.latitude,
        longitude = mapObjectEntity.longitude,
        name = mapObjectEntity.name,
        description = mapObjectEntity.description,
    )

}