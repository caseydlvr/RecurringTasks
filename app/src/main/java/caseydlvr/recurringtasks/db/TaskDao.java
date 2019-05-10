package caseydlvr.recurringtasks.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.model.*;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> observeAll();

    @Transaction
    @Query("SELECT * FROM tasks")
    LiveData<List<TaskWithTagIds>> observeAllWithTagIds();

    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "WHERE tasks_tags.tag_id = :tagId")
    LiveData<List<Task>> observeByTag(int tagId);

    @Transaction
    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "WHERE tasks_tags.tag_id = :tagId ")
    LiveData<List<TaskWithTagIds>> observeByTagWithTagIds(int tagId);

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> observeById(long id);

    @Query("SELECT * FROM tasks WHERE notification_option != 'never'")
    List<Task> loadAllWithNotifications();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task loadById(long id);

    @Insert(onConflict = REPLACE)
    long[] insert(Task... tasks);

    @Update
    void update(Task... tasks);

    @Delete
    void delete(Task... tasks);
}
