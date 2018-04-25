package caseydlvr.recurringtasks.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.model.*;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> loadAll();

    @Query("SELECT * FROM tasks WHERE end_date IS NULL")
    LiveData<List<Task>> loadAllOutstanding();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> loadById(long id);

    @Query("SELECT * FROM tasks WHERE end_date IS NULL AND uses_notifications = 1")
    LiveData<List<Task>> loadOutstandingWithNotifications();

    @Insert(onConflict = REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task... tasks);
}
