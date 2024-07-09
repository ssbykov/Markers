package ru.netology.markers.utils

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.netology.markers.R
import ru.netology.markers.dto.LocalMapObject
import kotlin.math.round

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

interface DilogActions {
    fun edit(localMapObject: LocalMapObject)
    fun remove(id: Long)
}

fun Context.showMapObjectDilog(localMapObject: LocalMapObject, dilogActions: DilogActions) {
    val description = if (localMapObject.description != "") {
        localMapObject.description
    } else "Описание отсутствует"
    MaterialAlertDialogBuilder(this)
        .setTitle(localMapObject.name)
        .setMessage(description)
        .setIcon(R.drawable.ic_dollar_pin)
        .setNegativeButtonIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_clear_24
            )
        )
        .setNegativeButton(null) { dialog, _ ->
            dilogActions.remove(localMapObject.id)
            dialog.dismiss()
        }
        .setPositiveButtonIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_edit_24
            )
        )
        .setPositiveButton(null) { dialog, _ ->
            dilogActions.edit(localMapObject)
            dialog.dismiss()
        }
        .setNeutralButtonIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_done_24
            )
        )
        .setNeutralButton(null) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

fun compareLocations(cameraPosition: CameraPosition, point: Point): Boolean {
    return round(cameraPosition.target.latitude * 1000) / 1000 ==
            round(point.latitude * 1000) / 1000 &&
            round(cameraPosition.target.longitude * 1000) / 1000 ==
            round(point.longitude * 1000) / 1000
}