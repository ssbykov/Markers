package ru.netology.markers.activity

import android.annotation.SuppressLint
import android.content.Context
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectDragListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import ru.netology.markers.BuildConfig
import ru.netology.markers.R
import ru.netology.markers.db.AppDb
import ru.netology.markers.repository.MapObjectRepoImpl
import ru.netology.markers.utils.showToast
import ru.netology.markers.viewmodel.MapsVeiwModel
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ActivityModule {

    @Provides
    @Singleton
    fun provideMapKitFactory(@ApplicationContext context: Context): MapKit {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(context)
        return MapKitFactory.getInstance()
    }

    @SuppressLint("MissingPermission")
    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnFailureListener { _ ->
                context.showToast(context.getString(R.string.location_not_determined))
            }

        return fusedLocationClient
    }

}