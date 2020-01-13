package caseydlvr.recurringtasks.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import caseydlvr.recurringtasks.model.TaskTag;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface TaskTagDao {

    @Query("SELECT * FROM tasks_tags WHERE task_id = :taskId")
    List<TaskTag> loadByTask(String taskId);

    @Insert(onConflict = IGNORE)
    void insert(TaskTag... taskTags);

    @Delete
    void delete(TaskTag... taskTags);
}
