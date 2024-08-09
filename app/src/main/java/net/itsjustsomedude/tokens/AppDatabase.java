package net.itsjustsomedude.tokens;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import android.content.Context;

import net.itsjustsomedude.tokens.db.Coop;
import net.itsjustsomedude.tokens.db.CoopDao;
import net.itsjustsomedude.tokens.db.Converters;
import net.itsjustsomedude.tokens.db.Event;

@Database(entities = {Coop.class, Event.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
	private static AppDatabase instance;

	public abstract CoopDao coopDao();

	public static synchronized AppDatabase getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context.getApplicationContext(),
							AppDatabase.class, "database")
					.fallbackToDestructiveMigration()
					.build();
		}
		return instance;
	}
}