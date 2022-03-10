package kobramob.rubeg38.ru.myprotection.feature.events.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kobramob.rubeg38.ru.myprotection.feature.events.data.models.EventEntity
import kobramob.rubeg38.ru.myprotection.utils.TypeConverter

@Database(entities = [EventEntity::class], version = 1)
@TypeConverters(TypeConverter::class)
abstract  class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

}