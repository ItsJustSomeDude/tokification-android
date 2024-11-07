package net.itsjustsomedude.tokens.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
	entities = [Coop::class, Event::class],
	version = 2,
	exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun coopDao(): CoopDao

	abstract fun eventDao(): EventDao

	companion object {
		fun createInstance(context: Context): AppDatabase = databaseBuilder(
			context.applicationContext,
			AppDatabase::class.java,
			"Coops.db"
		)
			// Migrates from the legacy Tokifiction to the Room version.
			.addMigrations(MIGRATION_10_1)
			// Adds boost order to Coop, and Receiver to Events.
			.addMigrations(MIGRATION_1_2)
//            .fallbackToDestructiveMigration()
			.build()
	}
}