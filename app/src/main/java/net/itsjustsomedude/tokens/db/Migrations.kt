package net.itsjustsomedude.tokens.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// This should migrate SQLite to room!
val MIGRATION_10_1 = object : Migration(10, 1) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create the new Coops table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Coop (
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                contract TEXT NOT NULL,
                startTime INTEGER,
                endTime INTEGER,
                sinkMode INTEGER NOT NULL,
                players TEXT NOT NULL
            )
            """
        )

        // Create the new Events table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS Event (
                id INTEGER NOT NULL PRIMARY KEY,
                coop TEXT NOT NULL,
                kevId TEXT NOT NULL,
                time INTEGER NOT NULL,
                count INTEGER NOT NULL,
                person TEXT NOT NULL,
                direction INTEGER NOT NULL,
                notification INTEGER NOT NULL
            )
            """
        )

        // Step 2: Copy data from old table into the new table
        db.execSQL(
            """
            INSERT INTO Coop (id, name, contract, startTime, endTime, sinkMode, players)
            SELECT
                _id,
                IFNULL(CoopName, ''),
                IFNULL(Contract, ''),
                StartTime * 1000,
                EndTime * 1000,
                IFNULL(SinkMode, 0), '[]'
            FROM Coops
            """
        )

        db.execSQL(
            """
            INSERT INTO Event (id, coop, kevId, time, count, person, direction, notification)
            SELECT 
                _id,
                IFNULL(Coop, ''),
                IFNULL(Contract, ''),
                IFNULL(Time, 0) * 1000,
                IFNULL(Count, 0),
                IFNULL(Person, ''),
                CASE WHEN Direction = 'sent' THEN 1 ELSE 2 END,
                IFNULL(NoteID, 0)
            FROM Events
            """
        )

        // Step 3: Drop the old table
        db.execSQL("DROP TABLE Coops")
        db.execSQL("DROP TABLE Events")
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Event ADD COLUMN receiver INTEGER NOT NULL DEFAULT 0")

        db.execSQL("ALTER TABLE Coop ADD COLUMN boostOrder INTEGER NOT NULL DEFAULT ${Coop.BOOST_ORDER_UNKNOWN}")
        db.execSQL("ALTER TABLE Coop ADD COLUMN sink TEXT NOT NULL DEFAULT \"\"")
        db.execSQL("ALTER TABLE Coop ADD COLUMN playerPositionOverrides TEXT NOT NULL DEFAULT \"{}\"")
        db.execSQL("ALTER TABLE Coop ADD COLUMN playerOrderOverrides TEXT NOT NULL DEFAULT \"{}\"")
        db.execSQL("ALTER TABLE Coop ADD COLUMN playerTokenAmounts TEXT NOT NULL DEFAULT \"{}\"")
    }
}
