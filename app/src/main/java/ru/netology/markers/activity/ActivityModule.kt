package ru.netology.markers.activity

import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ActivityModule {

    @Singleton
    @Provides
    fun providerMapKitFactory() = MapKitFactory.getInstance()

}