package ru.netology.markers.InterfaceImpl

import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectDragListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import ru.netology.markers.viewmodel.MapsVeiwModel
import javax.inject.Inject
import javax.inject.Singleton


class MapObjectDragListenerImpl(
    private val viewModel: MapsVeiwModel
) : MapObjectDragListener {

    var point = Point()
    override fun onMapObjectDragStart(mapObject: MapObject) {}

    override fun onMapObjectDrag(mapObject: MapObject, p1: Point) {
        point = p1
    }

    override fun onMapObjectDragEnd(mapObject: MapObject) {
        viewModel.viewModelScope.launch {
            val selectedMapObject = viewModel.getById(mapObject.userData as Long)
            if (selectedMapObject != null && (point.latitude != 0.0 || point.longitude != 0.0)) {
                val localMapObject = selectedMapObject.copy(
                    id = selectedMapObject.id,
                    latitude = point.latitude,
                    longitude = point.longitude
                )
                viewModel.save(localMapObject)
                point = Point()
            }
        }
    }
}
