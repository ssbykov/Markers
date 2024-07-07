package ru.netology.markers.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mapObjects")
data class MapObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val description: String?
)
