package caseydlvr.recurringtasks.db.migrations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import caseydlvr.recurringtasks.db.AppDatabase;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class Migration_3_4Test {
    private static final String TEST_DB = "migration_3_4-test";
    private static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    @Rule
    public MigrationTestHelper mHelper;

    public Migration_3_4Test() {
        mHelper = new MigrationTestHelper(
                InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate3To4() throws IOException {
        SupportSQLiteDatabase db = mHelper.createDatabase(TEST_DB, 3);

        populateTestTasks(db);
        db.close();

        // auto verifies the tags and tasks_tags tables were added
        db = mHelper.runMigrationsAndValidate(TEST_DB, 4, true, new Migration_3_4());

        Cursor tasks = db.query("SELECT * FROM tasks");
        Cursor tags = db.query("SELECT * FROM tags");
        Cursor tasksTags = db.query("SELECT * FROM tasks_tags");
        Cursor deletions = db.query("SELECT * FROM deletions");

        /* ---- verify task data ---- */

        // verify no rows are lost
        assertEquals("tasks row count should be 2", 2, tasks.getCount());

        // verify Task 1
        tasks.moveToFirst();
        String task1Id = tasks.getString(tasks.getColumnIndex("id"));
        assertTrue(task1Id.matches(UUID_REGEX));
        assertEquals("name not equal", "Test 1", tasks.getString(tasks.getColumnIndex("name")));
        assertEquals("duration not equal", 1, tasks.getInt(tasks.getColumnIndex("duration")));
        assertEquals("duration_unit not equal", "week", tasks.getString(tasks.getColumnIndex("duration_unit")));
        assertEquals("start_date not equal", "2018-01-05", tasks.getString(tasks.getColumnIndex("start_date")));
        assertEquals("end_date still exists", -1, tasks.getColumnIndex("end_date"));
        assertEquals("repeating not equal", 1, tasks.getInt(tasks.getColumnIndex("repeating")));
        assertEquals("notification_option not equal", "overdue", tasks.getString(tasks.getColumnIndex("notification_option")));
        // verify new columns
        assertEquals("synced not 0", 0, tasks.getInt(tasks.getColumnIndex("synced")));

        // verify Task 2
        tasks.moveToNext();
        String task2Id = tasks.getString(tasks.getColumnIndex("id"));
        assertTrue(task2Id.matches(UUID_REGEX));
        assertEquals("name not equal", "Test 2", tasks.getString(tasks.getColumnIndex("name")));
        assertEquals("duration not equal", 10, tasks.getInt(tasks.getColumnIndex("duration")));
        assertEquals("duration_unit not equal", "day", tasks.getString(tasks.getColumnIndex("duration_unit")));
        assertEquals("start_date not equal", "2018-05-30", tasks.getString(tasks.getColumnIndex("start_date")));
        assertEquals("end_date still exists", -1, tasks.getColumnIndex("end_date"));
        assertEquals("repeating not equal", 0, tasks.getInt(tasks.getColumnIndex("repeating")));
        assertEquals("notification_option not equal", "never", tasks.getString(tasks.getColumnIndex("notification_option")));
        // verify new columns
        assertEquals("synced not 0", 0, tasks.getInt(tasks.getColumnIndex("synced")));

        /* ---- verify tag data ---- */

        // verify no rows are lost
        assertEquals("tags row count should be 2", 2, tasks.getCount());

        // verify Tag 1
        tags.moveToFirst();
        String tag1Id = tags.getString(tags.getColumnIndex("id"));
        assertTrue(tag1Id.matches(UUID_REGEX));
        assertEquals("name not equal", "Tag 1", tags.getString(tags.getColumnIndex("name")));
        // verify new columns
        assertEquals("synced not 0", 0, tags.getInt(tags.getColumnIndex("synced")));

        // verify Tag 2
        tags.moveToNext();
        String tag2Id = tags.getString(tags.getColumnIndex("id"));
        assertTrue(tag2Id.matches(UUID_REGEX));
        assertEquals("name not equal", "Tag 2", tags.getString(tags.getColumnIndex("name")));
        // verify new columns
        assertEquals("synced not 0", 0, tags.getInt(tags.getColumnIndex("synced")));

        /* ---- verify task_tag data ---- */

        // verify no rows are lost
        assertEquals("tasks_tags row count should be 2", 2, tasksTags.getCount());

        // verify TaskTag 1
        tasksTags.moveToFirst();
        assertEquals("task_id not equal", task1Id, tasksTags.getString(tasksTags.getColumnIndex("task_id")));
        assertEquals("tag_id not equal", tag1Id, tasksTags.getString(tasksTags.getColumnIndex("tag_id")));
        // verify new columns
        assertEquals("synced not 0", 0, tasksTags.getInt(tasksTags.getColumnIndex("synced")));

        // verify TaskTag 2
        tasksTags.moveToNext();
        assertEquals("task_id not equal", task1Id, tasksTags.getString(tasksTags.getColumnIndex("task_id")));
        assertEquals("tag_id not equal", tag2Id, tasksTags.getString(tasksTags.getColumnIndex("tag_id")));
        // verify new columns
        assertEquals("synced not 0", 0, tasksTags.getInt(tasksTags.getColumnIndex("synced")));

        /* ---- verify deletions table created ---- */
        assertEquals("deletions should be empty", 0, deletions.getCount());
        assertNotEquals("task_id column missing", -1, deletions.getColumnIndex("task_id"));
        assertNotEquals("tag_id column missing", -1, deletions.getColumnIndex("tag_id"));
    }

    private void populateTestTasks(SupportSQLiteDatabase db) {
        ContentValues task1 = new ContentValues();
        task1.put("id", 1);
        task1.put("name", "Test 1");
        task1.put("duration", 1);
        task1.put("duration_unit", "week");
        task1.put("start_date", "2018-01-05");
        task1.put("end_date", "");
        task1.put("repeating", 1);
        task1.put("notification_option", "overdue");

        ContentValues task2 = new ContentValues();
        task2.put("id", 2);
        task2.put("name", "Test 2");
        task2.put("duration", 10);
        task2.put("duration_unit", "day");
        task2.put("start_date", "2018-05-30");
        task2.put("end_date", "");
        task2.put("repeating", 0);
        task2.put("notification_option", "never");

        ContentValues tag1 = new ContentValues();
        tag1.put("id", 1);
        tag1.put("name", "Tag 1");

        ContentValues tag2 = new ContentValues();
        tag2.put("id", 2);
        tag2.put("name", "Tag 2");

        ContentValues taskTag1 = new ContentValues();
        taskTag1.put("task_id", 1);
        taskTag1.put("tag_id", 1);

        ContentValues taskTag2 = new ContentValues();
        taskTag2.put("task_id", 1);
        taskTag2.put("tag_id", 2);

        db.insert("tasks", SQLiteDatabase.CONFLICT_REPLACE, task1);
        db.insert("tasks", SQLiteDatabase.CONFLICT_REPLACE, task2);

        db.insert("tags", SQLiteDatabase.CONFLICT_REPLACE, tag1);
        db.insert("tags", SQLiteDatabase.CONFLICT_REPLACE, tag2);

        db.insert("tasks_tags", SQLiteDatabase.CONFLICT_REPLACE, taskTag1);
        db.insert("tasks_tags", SQLiteDatabase.CONFLICT_REPLACE, taskTag2);
    }
}
