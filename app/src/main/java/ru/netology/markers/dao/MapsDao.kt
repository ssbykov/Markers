package ru.netology.markers.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.markers.entity.MapObjectEntity

@Dao
interface MapsDao {
    @Query("SELECT * FROM mapObjects")
    fun getAll(): Flow<List<MapObjectEntity>>
}