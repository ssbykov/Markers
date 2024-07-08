package ru.netology.markers.utils

import android.content.Context
import android.widget.Toast
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlin.math.round

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun compareLocations(cameraPosition: CameraPosition, point: Point): Boolean {
    return round(cameraPosition.target.latitude * 1000) / 1000 ==
            round(point.latitude * 1000) / 1000 &&
            round(cameraPosition.target.longitude * 1000) / 1000 ==
            round(point.longitude * 1000) / 1000
}