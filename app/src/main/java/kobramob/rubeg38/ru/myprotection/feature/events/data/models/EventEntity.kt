package kobramob.rubeg38.ru.myprotection.feature.events.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = EventEntity.TABLE_NAME, primaryKeys = ["facilityId", "id"])
data class EventEntity(
    @ColumnInfo(name = "facilityId")
    val facilityId: String,
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "timestamp")
    val timestamp: Date,
    @ColumnInfo(name = "type")
    val type: Int,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "zone")
    val zone: String
) {
    companion object {
        const val TABLE_NAME = "events_table"
    }
}