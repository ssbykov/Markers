package ru.netology.markers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.MapKitFactory
import ru.netology.markers.BuildConfig


class MapsVeiwModel(application: Application) : AndroidViewModel(application) {
    var mapKitFactory: Unit

    init {
        MapKitFactory.setApiKey("YOUR_API_KEY")
        mapKitFactory = MapKitFactory.initialize(application)
    }
}