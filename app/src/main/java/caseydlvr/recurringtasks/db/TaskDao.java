package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.models.*;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    List<Task> loadAll();

    @Query("SELECT * FROM tasks WHERE end_date IS NULL")
    List<Task> loadAllOutstanding();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task loadById(long id);

    @Insert
    long insert(Task task);

    @Update
    void update(Task... tasks);

    @Delete
    void delete(Task... tasks);
}
