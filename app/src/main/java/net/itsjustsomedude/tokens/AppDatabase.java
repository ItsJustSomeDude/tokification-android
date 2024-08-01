package net.itsjustsomedude.tokens;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Database;

import androidx.room.TypeConverters;
import java.util.List;
import net.itsjustsomedude.tokens.database.Converters;

@Database(entities = {}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {


}
