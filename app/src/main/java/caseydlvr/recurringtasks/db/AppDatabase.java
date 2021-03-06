package caseydlvr.recurringtasks.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import caseydlvr.recurringtasks.db.migrations.Migration_1_2;
import caseydlvr.recurringtasks.db.migrations.Migration_2_3;
import caseydlvr.recurringtasks.model.*;

@Database(entities = { Task.class, Tag.class, TaskTag.class }, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public abstract TaskDao taskDao();
    public abstract TagDao tagDao();
    public abstract TaskTagDao taskTagDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "task-database")
                    .addMigrations(new Migration_1_2(), new Migration_2_3())
                    .build();
        }

        return sInstance;
    }
}
