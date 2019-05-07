package caseydlvr.recurringtasks.db.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_3_4 extends Migration {
    /**
     * Creates a new migration between DB version 3 and 4.
     */
    public Migration_3_4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.beginTransaction();

        database.execSQL("ALTER TABLE tasks ADD COLUMN server_id INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tasks ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tasks ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");

        database.execSQL("ALTER TABLE tags ADD COLUMN server_id INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tags ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tags ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");

        database.execSQL("ALTER TABLE tasks_tags ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tasks_tags ADD COLUMN deleted INTEGER NOT NULL DEFAULT 0");

        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
