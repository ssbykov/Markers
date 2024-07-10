package ru.netology.markers.adapter

import ru.netology.markers.dto.LocalMapObject

interface SetupClickListeners {
    fun onItemListener(localMapObject: LocalMapObject)
    fun onRemoveListener(localMapObject: LocalMapObject)
    fun onEditListener(localMapObject: LocalMapObject)
}

