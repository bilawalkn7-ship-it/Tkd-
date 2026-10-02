package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.BeltLevel

class BeltConverters {
  @TypeConverter
  fun fromBelt(belt: BeltLevel): String = belt.name

  @TypeConverter
  fun toBelt(value: String): BeltLevel = try {
    BeltLevel.valueOf(value)
  } catch (e: Exception) {
    BeltLevel.WHITE
  }
}

@Database(
  entities = [
    PlayerProfile::class,
    PracticeRecord::class,
    SparringRecord::class,
    ChallengeQuest::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(BeltConverters::class)
abstract class TkdDatabase : RoomDatabase() {
  abstract fun tkdDao(): TkdDao

  companion object {
    @Volatile
    private var INSTANCE: TkdDatabase? = null

    fun getDatabase(context: Context): TkdDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TkdDatabase::class.java,
          "tkd_master_database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
