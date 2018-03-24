package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import caseydlvr.recurringtasks.models.*;

@Database(entities = {caseydlvr.recurringtasks.models.Task.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TaskDao taskDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "task-database")
                    .allowMainThreadQueries() // TODO remove this!
                    .build();
        }

        return INSTANCE;
    }
}
