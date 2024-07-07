package ru.netology.markers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.markers.BuildConfig
import ru.netology.markers.db.AppDb
import ru.netology.markers.dto.MapObject
import ru.netology.markers.model.FeedModel
import ru.netology.markers.repository.MapObjectRepoImpl


class MapsVeiwModel(application: Application) : AndroidViewModel(application) {

    private val repository = MapObjectRepoImpl(AppDb.getInstance(application).mapsDao())
    val data = repository.data.map { objects ->
        objects.map { it.copy() }
    }.asLiveData(Dispatchers.Default)

    fun save(mapObject: MapObject) {
        viewModelScope.launch {
            repository.save(mapObject)
        }
    }


}