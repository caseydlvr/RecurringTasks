package caseydlvr.recurringtasks.db.migrations;

import android.database.Cursor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        int taskCount = getCount(database, "tasks");
        int tagCount = getCount(database, "tags");

        String[] taskUuids = generateUuids(taskCount);
        String[] tagUuids = generateUuids(tagCount);

        database.beginTransaction();

        // populate UUIDs
        database.execSQL("ALTER TABLE tasks ADD COLUMN uuid TEXT DEFAULT NULL");
        database.execSQL("ALTER TABLE tags ADD COLUMN uuid TEXT DEFAULT NULL");
        database.execSQL("ALTER TABLE tasks_tags ADD COLUMN task_uuid TEXT DEFAULT NULL");
        database.execSQL("ALTER TABLE tasks_tags ADD COLUMN tag_uuid TEXT DEFAULT NULL");
        populateUuids(database, "tasks", taskUuids);
        populateUuids(database, "tags", tagUuids);
        populateTasksTagsUuids(database);

        // rebuild tasks with uuid as PK
        // also remove unused end_date column from tasks
        database.execSQL("ALTER TABLE tasks RENAME TO tasks_old");
        database.execSQL("CREATE TABLE tasks (" +
                "`id` TEXT PRIMARY KEY NOT NULL, " +
                "`name` TEXT, " +
                "`duration` INTEGER NOT NULL, " +
                "`duration_unit` TEXT, " +
                "`start_date` TEXT, " +
                "`repeating` INTEGER NOT NULL, " +
                "`notification_option` TEXT)");
        database.execSQL("INSERT INTO tasks " +
                "(id, name, duration, duration_unit, start_date, repeating, notification_option) " +
                "SELECT uuid, name, duration, duration_unit, start_date, repeating, notification_option " +
                "FROM tasks_old");
        database.execSQL("DROP TABLE tasks_old");

        // rebuild tags with uuid as PK
        database.execSQL("ALTER TABLE tags RENAME TO tags_old");
        database.execSQL("CREATE TABLE tags (" +
                "`id` TEXT PRIMARY KEY NOT NULL, " +
                "`name` TEXT)");
        database.execSQL("INSERT INTO tags " +
                "(id, name) " +
                "SELECT uuid, name FROM tags_old");
        database.execSQL("DROP TABLE tags_old");

        // rebuild tasks_tags with uuids
        database.execSQL("ALTER TABLE tasks_tags RENAME TO tasks_tags_old");
        database.execSQL("CREATE TABLE tasks_tags (" +
                "`task_id` TEXT NOT NULL DEFAULT '', " +
                "`tag_id` TEXT NOT NULL DEFAULT '', " +
                "PRIMARY KEY(`task_id`, `tag_id`), " +
                "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`id`) ON DELETE CASCADE, " +
                "FOREIGN KEY(`tag_id`) REFERENCES `tags`(`id`) ON DELETE CASCADE)");
        database.execSQL("INSERT INTO tasks_tags " +
                "(task_id, tag_id) " +
                "SELECT task_uuid, tag_uuid " +
                "FROM tasks_tags_old");
        database.execSQL("DROP TABLE tasks_tags_old");
        database.execSQL("CREATE INDEX `index_tasks_tags_tag_id` ON `tasks_tags`(`tag_id`)");

        // add synced columns
        database.execSQL("ALTER TABLE tasks ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tags ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE tasks_tags ADD COLUMN synced INTEGER NOT NULL DEFAULT 0");

        // new table for syncing deletions
        database.execSQL("CREATE TABLE deletions (" +
                "`task_id` TEXT NOT NULL DEFAULT '', " +
                "`tag_id` TEXT NOT NULL DEFAULT ''," +
                "PRIMARY KEY (task_id, tag_id))");

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private int getCount(SupportSQLiteDatabase db, String tableName) {
        Cursor countQueryResult = db.query("SELECT count(*) FROM " + tableName);
        countQueryResult.moveToFirst();
        int count = countQueryResult.getInt(0);
        countQueryResult.close();

        return count;
    }

    private String[] generateUuids(int count) {
        Set<String> uuids = new HashSet<>();

        while(uuids.size() < count) {
            uuids.add(UUID.randomUUID().toString());
        }

        return uuids.toArray(new String[count]);
    }

    private void populateUuids(SupportSQLiteDatabase db, String tableName, String[] uuids) {
        Cursor ids = db.query("SELECT id FROM " + tableName);
        int uuidIndex = 0;

        while (ids.moveToNext()) {
            int id = ids.getInt(0);

            db.execSQL("UPDATE " + tableName + " SET uuid = '" + uuids[uuidIndex] + "' WHERE id = " + id);
            uuidIndex++;
        }
    }

    private void populateTasksTagsUuids(SupportSQLiteDatabase db) {
        db.execSQL("UPDATE tasks_tags " +
                "SET task_uuid = (SELECT uuid FROM tasks WHERE tasks.id = tasks_tags.task_id)");

        db.execSQL("UPDATE tasks_tags " +
                "SET tag_uuid = (SELECT uuid FROM tags WHERE tags.id = tasks_tags.tag_id)");
    }
}
