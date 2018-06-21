package caseydlvr.recurringtasks.db.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

public class Migration_1_2 extends Migration {

    public Migration_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.beginTransaction();

        // add new notification_option column
        database.execSQL("ALTER TABLE tasks ADD COLUMN notification_option TEXT");

        // set notification_option based on uses_notifications value
        database.execSQL("UPDATE tasks SET notification_option = " +
                "CASE WHEN uses_notifications == 0 THEN 'never' ELSE 'overdue_due' END");

        // drop uses_notifications column
        database.execSQL("ALTER TABLE tasks RENAME TO tasks_old");
        database.execSQL("CREATE TABLE tasks (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT, " +
                "`duration` INTEGER NOT NULL, " +
                "`duration_unit` TEXT, " +
                "`start_date` TEXT, " +
                "`end_date` TEXT, " +
                "`repeating` INTEGER NOT NULL, " +
                "`notification_option` TEXT)");
        database.execSQL("INSERT INTO tasks " +
                "(id, name, duration, duration_unit, start_date, end_date, repeating, notification_option) " +
                "SELECT id, name, duration, duration_unit, start_date, end_date, repeating, notification_option " +
                "FROM tasks_old");
        database.execSQL("DROP TABLE tasks_old");

        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
