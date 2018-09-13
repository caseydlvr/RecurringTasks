package caseydlvr.recurringtasks.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskTag;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface TaskTagDao {

    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "WHERE tasks_tags.tag_id = :tagId")
    LiveData<List<Task>> getTasksForTag(int tagId);

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId " +
            "ORDER BY tags.name")
    LiveData<List<Tag>> getTagsForTask(long taskId);

    @Query("SELECT * FROM tasks_tags WHERE task_id = :taskId")
    List<TaskTag> getTaskTagsForTask(long taskId);

    @Insert(onConflict = IGNORE)
    void insert(TaskTag... taskTag);

    @Delete
    void delete(TaskTag... taskTags);
}
