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
import ru.netology.markers.db.AppDb
import ru.netology.markers.dto.MapObject
import ru.netology.markers.repository.MapObjectRepoImpl

val empty = MapObject(
    id = 0,
    name = "",
    latitude = 0.0,
    longitude = 0.0,
    description = ""
)


class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    private val POINT = Point(55.704473, 37.624700)

    private val repository = MapObjectRepoImpl(AppDb.getInstance(application).mapsDao())
    val data = repository.data.map { objects ->
        objects.map { it.copy() }
    }.asLiveData(Dispatchers.Default)

    private val _edited = MutableLiveData<MapObject>(empty)
    val edited: LiveData<MapObject>
        get() = _edited

    private val _currtntLocation = MutableLiveData<Point>(POINT)

    val currtntLocation: LiveData<Point>
        get() = _currtntLocation

    fun save(mapObject: MapObject) {
        viewModelScope.launch {
            repository.save(mapObject)
        }
        _edited.value = empty
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun edit(mapObject: MapObject) {
        _edited.value = mapObject
    }

    fun setCurrtntLocation(point: Point) {
        _currtntLocation.value = point
    }


}