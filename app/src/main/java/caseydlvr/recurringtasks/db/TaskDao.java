package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    List<Task> loadAll();

    @Query("SELECT * FROM tasks WHERE end_date IS NULL")
    List<Task> loadAllOutstanding();

    @Insert
    void insert(Task... tasks);

    @Update
    void update(Task... tasks);
}
