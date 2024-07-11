package ru.netology.markers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.markers.Constants.POINT
import ru.netology.markers.Constants.ZERO
import ru.netology.markers.R
import ru.netology.markers.db.AppDb
import ru.netology.markers.dto.LocalMapObject
import ru.netology.markers.model.CurrentLocation
import ru.netology.markers.repository.MapObjectRepoImpl

val empty = LocalMapObject(
    id = 0,
    name = "",
    latitude = 0.0,
    longitude = 0.0,
    description = ""
)

const val KEY_LATITUDE = "KEY_LATITUDE"
const val KEY_LONGITUDE = "KEY_LONGITUDE"
const val KEY_POINT = "point"

class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    private val CURRENTLOCATION =
        CurrentLocation(POINT, null)

    val startPointDraft =
        application.getSharedPreferences(KEY_POINT, android.content.Context.MODE_PRIVATE)

    private val _currtntLocation = MutableLiveData<CurrentLocation>()

    val currtntLocation: LiveData<CurrentLocation>
        get() = _currtntLocation

    init {
        val startLatitude = startPointDraft.getFloat(KEY_LATITUDE, ZERO).toDouble()
        val startLongitude = startPointDraft.getFloat(KEY_LONGITUDE, ZERO).toDouble()
        if (startLatitude == ZERO.toDouble()) {
            _currtntLocation.value = CURRENTLOCATION
        } else {
            val srartPoint = Point(startLatitude, startLongitude)
            val name = application.getString(R.string.last_location)
            _currtntLocation.value = CurrentLocation(srartPoint, name)
        }
    }

    private val repository = MapObjectRepoImpl(AppDb.getInstance(application).mapsDao())
    val data = repository.data.map { objects ->
        objects.map { it.copy() }
    }.asLiveData(Dispatchers.Default)

    private val _edited = MutableLiveData<LocalMapObject>(empty)
    val edited: LiveData<LocalMapObject>
        get() = _edited


    fun save(localMapObject: LocalMapObject) {
        viewModelScope.launch {
            repository.save(localMapObject)
        }
        _edited.value = empty
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun getById(id: Long) = repository.getById(id)


    fun edit(localMapObject: LocalMapObject) {
        _edited.value = localMapObject
    }


    fun setCurrtntLocation(latitude: Double, longitude: Double, name: String) {
        val point = Point(latitude, longitude)
        _currtntLocation.value = CurrentLocation(point, name)
        startPointDraft.edit()
            .putFloat(KEY_LATITUDE, latitude.toFloat())
            .putFloat(KEY_LONGITUDE, longitude.toFloat())
            .apply()
    }


}