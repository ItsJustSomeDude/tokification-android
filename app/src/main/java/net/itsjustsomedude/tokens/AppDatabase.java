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

import java.util.List;

@Database(entities = {net.itsjustsomedude.tokens.database.Coop.class, AppDatabase.Event.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {


}
