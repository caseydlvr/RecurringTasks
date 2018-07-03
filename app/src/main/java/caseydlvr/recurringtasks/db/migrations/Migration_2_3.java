package caseydlvr.recurringtasks.db.migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;

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
                "`task_id` INTEGER NOT NULL, " +
                "`tag_id` INTEGER NOT NULL, " +
                "PRIMARY KEY(`task_id`, `tag_id`), " +
                "FOREIGN KEY(`task_id`) REFERENCES `tasks`(`id`) ON DELETE CASCADE, " +
                "FOREIGN KEY(`tag_id`) REFERENCES `tags`(`id`) ON DELETE CASCADE)");
        database.execSQL("CREATE INDEX `index_tasks_tags_tag_id` ON `tasks_tags`(`tag_id`)");

        database.setTransactionSuccessful();
        database.endTransaction();
    }
}
