package ru.netology.markers.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.markers.dao.MapsDao
import ru.netology.markers.entity.MapObjectEntity

@Database(entities = [MapObjectEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun mapsDao(): MapsDao
}