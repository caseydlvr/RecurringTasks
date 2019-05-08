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

        // remove unused end_date column from tasks
        database.execSQL("ALTER TABLE tasks RENAME TO tasks_old");
        database.execSQL("CREATE TABLE tasks (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT, " +
                "`duration` INTEGER NOT NULL, " +
                "`duration_unit` TEXT, " +
                "`start_date` TEXT, " +
                "`repeating` INTEGER NOT NULL, " +
                "`notification_option` TEXT)");
        database.execSQL("INSERT INTO tasks " +
                "(id, name, duration, duration_unit, start_date, repeating, notification_option) " +
                "SELECT id, name, duration, duration_unit, start_date, repeating, notification_option " +
                "FROM tasks_old");
        database.execSQL("DROP TABLE tasks_old");

        // add new columns
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
