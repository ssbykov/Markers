package ru.netology.markers.model

import com.yandex.mapkit.geometry.Point

data class CurrentLocation(
    val point: Point,
    val name: String?
)