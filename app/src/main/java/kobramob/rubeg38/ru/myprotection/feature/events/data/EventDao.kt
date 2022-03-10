package kobramob.rubeg38.ru.myprotection.feature.events.data

import androidx.room.*
import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME} WHERE facilityId = :facilityId")
    suspend fun getAll(facilityId: String): List<EventEntity>

    @Query("SELECT * FROM ${EventEntity.TABLE_NAME} WHERE facilityId = :facilityId AND id >= :leftBound AND id <= :rightBound")
    suspend fun getAllInRange(facilityId: String, leftBound: Int, rightBound: Int): List<EventEntity>

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

}