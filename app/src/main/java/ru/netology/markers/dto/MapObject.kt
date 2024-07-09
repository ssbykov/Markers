package ru.netology.markers.dto


data class MapObject(
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val description: String
)
