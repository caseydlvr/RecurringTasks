package caseydlvr.recurringtasks.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import androidx.room.Transaction;
import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskTag;
import caseydlvr.recurringtasks.model.TaskWithTagIds;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface TaskTagDao {

    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "JOIN tags ON tags.id = :tagId " +
            "WHERE tasks_tags.tag_id = :tagId " +
            "AND NOT tags.deleted " +
            "AND NOT tasks.deleted " +
            "AND NOT tasks_tags.deleted")
    LiveData<List<Task>> loadTasksForTag(int tagId);

    @Transaction
    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "JOIN tags ON tags.id = :tagId " +
            "WHERE tasks_tags.tag_id = :tagId " +
            "AND NOT tags.deleted " +
            "AND NOT tasks.deleted " +
            "AND NOT tasks_tags.deleted")
    LiveData<List<TaskWithTagIds>> loadTasksAsTasksWithTagForTag(int tagId);

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId " +
            "AND NOT tasks_tags.deleted " +
            "AND NOT tags.deleted " +
            "ORDER BY tags.name")
    LiveData<List<Tag>> loadTagsForTask(long taskId);

    @Query("SELECT * FROM tasks_tags WHERE task_id = :taskId AND NOT deleted")
    List<TaskTag> loadTaskTagsForTask(long taskId);

    @Insert(onConflict = IGNORE)
    void insert(TaskTag... taskTag);

    @Delete
    void delete(TaskTag... taskTags);
}
