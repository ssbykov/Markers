package ru.netology.markers.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.markers.entity.MapObjectEntity

@Dao
interface MapsDao {
    @Query("SELECT * FROM mapObjects")
    fun getAll(): Flow<List<MapObjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mapObjectEntity: MapObjectEntity)

    @Query("DELETE FROM mapObjects WHERE id =:id")
    suspend fun removeById(id: Long)

    @Query(
        """SELECT * FROM mapObjects WHERE id = :id;"""
    )
    suspend fun getById(id: Long): MapObjectEntity?
}