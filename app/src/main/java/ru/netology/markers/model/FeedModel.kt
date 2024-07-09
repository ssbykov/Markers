package ru.netology.markers.model

import ru.netology.markers.dto.LocalMapObject

data class FeedModel(
    val posts: List<LocalMapObject> = emptyList(),
)