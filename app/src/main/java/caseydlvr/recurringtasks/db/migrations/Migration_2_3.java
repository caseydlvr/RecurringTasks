package caseydlvr.recurringtasks.db.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migration_2_3 extends Migration {

    public Migration_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.beginTransaction();

        database.execSQL("CREATE TABLE tags (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT)");

        database.execSQL("CREATE TABLE tasks_tags (" +
                "`task_id` INTEGER PRIMARY KEY NOT NULL REFERENCES tasks(id) ON DELETE CASCADE, " +
                "`tag_id` INTEGER PRIMARY KEY NOT NULL REFERENCES tags(id) ON DELETE CASCADE)");

        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
