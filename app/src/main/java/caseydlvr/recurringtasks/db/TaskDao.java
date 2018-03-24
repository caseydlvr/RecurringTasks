package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.models.*;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks")
    List<caseydlvr.recurringtasks.models.Task> loadAll();

    @Query("SELECT * FROM tasks WHERE end_date IS NULL")
    List<caseydlvr.recurringtasks.models.Task> loadAllOutstanding();

    @Insert
    void insert(caseydlvr.recurringtasks.models.Task... tasks);

    @Update
    void update(caseydlvr.recurringtasks.models.Task... tasks);
}
