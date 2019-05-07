package caseydlvr.recurringtasks.db.migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.room.testing.MigrationTestHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import caseydlvr.recurringtasks.db.AppDatabase;

import static org.junit.Assert.*;

/**
 * Test the migration from version 1 to version 2
 */
@RunWith(AndroidJUnit4.class)
public class Migration_1_2Test {
    private static final String TEST_DB = "migration_1_2-test";

    @Rule
    public MigrationTestHelper mHelper;

    public Migration_1_2Test() {
        mHelper = new MigrationTestHelper(
                InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    /**
     * Verify use_notifications (INT) is dropped, notification_option (TEXT) is added,
     * notification_option set to "never" if use_notifications was 0, "overdue_due" if
     * use_notifications was 1.
     *
     * @throws IOException
     */
    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = mHelper.createDatabase(TEST_DB, 1);

        // insert test data into version 1 DB
        populateTestTasks(db);
        db.close();

        db = mHelper.runMigrationsAndValidate(TEST_DB, 2, true, new Migration_1_2());

        Cursor tasks = db.query("SELECT * FROM tasks");

        // verify no tasks are lost
        assertEquals("table row count should be 2", 2, tasks.getCount());

        // verify Task 1
        tasks.moveToFirst();
        assertEquals("id not equal", 1, tasks.getInt(tasks.getColumnIndex("id")));
        assertEquals("name not equal", "Test 1", tasks.getString(tasks.getColumnIndex("name")));
        assertEquals("duration not equal", 1, tasks.getInt(tasks.getColumnIndex("duration")));
        assertEquals("duration_unit not equal", "week", tasks.getString(tasks.getColumnIndex("duration_unit")));
        assertEquals("start_date not equal", "2018-01-05", tasks.getString(tasks.getColumnIndex("start_date")));
        assertEquals("end_date not equal", "", tasks.getString(tasks.getColumnIndex("end_date")));
        assertEquals("repeating not equal", 1, tasks.getInt(tasks.getColumnIndex("repeating")));
        assertEquals("notification_option not equal", "overdue_due", tasks.getString(tasks.getColumnIndex("notification_option")));

        // verify Task 2
        tasks.moveToNext();
        assertEquals("id not equal", 2, tasks.getInt(tasks.getColumnIndex("id")));
        assertEquals("name not equal", "Test 2", tasks.getString(tasks.getColumnIndex("name")));
        assertEquals("duration not equal", 10, tasks.getInt(tasks.getColumnIndex("duration")));
        assertEquals("duration_unit not equal", "day", tasks.getString(tasks.getColumnIndex("duration_unit")));
        assertEquals("start_date not equal", "2018-05-30", tasks.getString(tasks.getColumnIndex("start_date")));
        assertEquals("end_date not equal", "", tasks.getString(tasks.getColumnIndex("end_date")));
        assertEquals("repeating not equal", 0, tasks.getInt(tasks.getColumnIndex("repeating")));
        assertEquals("notification_option not equal", "never", tasks.getString(tasks.getColumnIndex("notification_option")));
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
        task1.put("uses_notifications", 1);

        ContentValues task2 = new ContentValues();
        task2.put("id", 2);
        task2.put("name", "Test 2");
        task2.put("duration", 10);
        task2.put("duration_unit", "day");
        task2.put("start_date", "2018-05-30");
        task2.put("end_date", "");
        task2.put("repeating", 0);
        task2.put("uses_notifications", 0);

        db.insert("tasks", SQLiteDatabase.CONFLICT_REPLACE, task1);
        db.insert("tasks", SQLiteDatabase.CONFLICT_REPLACE, task2);
    }

}