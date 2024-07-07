package ru.netology.markers.model

import ru.netology.markers.dto.MapObject

data class FeedModel(
    val posts: List<MapObject> = emptyList(),
)