package caseydlvr.recurringtasks.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.model.*;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> loadAll();

    @Query("SELECT * FROM tasks WHERE end_date IS NULL")
    LiveData<List<Task>> loadAllOutstanding();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> loadById(long id);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task loadByIdTask(long id);

    @Query("SELECT * FROM tasks WHERE end_date IS NULL AND notification_option != 'never'")
    LiveData<List<Task>> loadOutstandingWithNotifications();

    @Insert(onConflict = REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task... tasks);
}
