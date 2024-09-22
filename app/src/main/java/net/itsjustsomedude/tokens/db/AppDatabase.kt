package net.itsjustsomedude.tokens.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Coop::class, Event::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coopDao(): CoopDao

    abstract fun eventDao(): EventDao

    companion object {
        fun createInstance(context: Context): AppDatabase = databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "database"
        )
            .fallbackToDestructiveMigration()
            .build()

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = createInstance(context)

                INSTANCE = instance
                instance
            }
        }
    }
}