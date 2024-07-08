package ru.netology.markers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.markers.BuildConfig
import ru.netology.markers.db.AppDb
import ru.netology.markers.dto.MapObject
import ru.netology.markers.model.FeedModel
import ru.netology.markers.repository.MapObjectRepoImpl


class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    private val POINT = Point(55.704473, 37.624700)

    private val repository = MapObjectRepoImpl(AppDb.getInstance(application).mapsDao())
    val data = repository.data.map { objects ->
        objects.map { it.copy() }
    }.asLiveData(Dispatchers.Default)


    private val _currtntLocation = MutableLiveData<Point>(POINT)

    val currtntLocation: LiveData<Point>
        get() = _currtntLocation

    fun save(mapObject: MapObject) {
        viewModelScope.launch {
            repository.save(mapObject)
        }
    }

    fun setCurrtntLocation(point: Point) {
        _currtntLocation.value = point
    }


}