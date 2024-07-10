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


class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    private val CURRENTLOCATION =
        CurrentLocation(Point(55.704473, 37.624700), null)

    private val repository = MapObjectRepoImpl(AppDb.getInstance(application).mapsDao())
    val data = repository.data.map { objects ->
        objects.map { it.copy() }
    }.asLiveData(Dispatchers.Default)

    private val _edited = MutableLiveData<LocalMapObject>(empty)
    val edited: LiveData<LocalMapObject>
        get() = _edited

    private val _currtntLocation = MutableLiveData<CurrentLocation>(CURRENTLOCATION)

    val currtntLocation: LiveData<CurrentLocation>
        get() = _currtntLocation

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
    }

}