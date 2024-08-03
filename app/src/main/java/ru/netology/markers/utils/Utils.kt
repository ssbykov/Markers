package ru.netology.markers.utils

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.netology.markers.dto.LocalMapObject
import kotlin.math.round

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

interface DilogActions {
    fun edit(localMapObject: LocalMapObject)
    fun remove(id: Long)
}


fun compareLocations(cameraPosition: CameraPosition, point: Point): Boolean {
    return round(cameraPosition.target.latitude * 1000) / 1000 ==
            round(point.latitude * 1000) / 1000 &&
            round(cameraPosition.target.longitude * 1000) / 1000 ==
            round(point.longitude * 1000) / 1000
}

fun CameraPosition.copy(
    zoom: Float? = null,
    azimuth: Float? = null,
    tilt: Float? = null
): CameraPosition {
        return CameraPosition(
            this.target,
            if (zoom != null) zoom else this.zoom,
            if (azimuth != null) azimuth else this.azimuth,
            if (tilt != null) tilt else this.tilt,
        )
}