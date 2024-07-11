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

class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    private val CURRENTLOCATION =
        CurrentLocation(POINT, null)

    val startPointDraft =
        application.getSharedPreferences("point", android.content.Context.MODE_PRIVATE)

    private val _currtntLocation = MutableLiveData<CurrentLocation>()

    val currtntLocation: LiveData<CurrentLocation>
        get() = _currtntLocation

    init {
        val startLatitude = startPointDraft.getFloat(KEY_LATITUDE, 1000.0F).toDouble()
        val startLongitude = startPointDraft.getFloat(KEY_LONGITUDE, 1000.0F).toDouble()
        if (startLatitude == 1000.0) {
            _currtntLocation.value = CURRENTLOCATION
        } else {
            val srartPoint = Point(startLatitude, startLongitude)
            _currtntLocation.value = CurrentLocation(srartPoint, "Последняя локация")
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

    fun setCurrtntLocation(currentLocation: CurrentLocation) {
        _currtntLocation.value = currentLocation
        startPointDraft.edit()
            .putFloat(KEY_LATITUDE, currentLocation.point.latitude.toFloat())
            .putFloat(KEY_LONGITUDE, currentLocation.point.longitude.toFloat())
            .apply()
    }

}