package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import caseydlvr.recurringtasks.model.Tag;
import caseydlvr.recurringtasks.model.Task;
import caseydlvr.recurringtasks.model.TaskTag;

@Dao
public interface TaskTagDao {

    @Query("SELECT tasks.* FROM tasks " +
            "JOIN tasks_tags ON tasks.id = tasks_tags.task_id " +
            "WHERE tasks_tags.task_id = :tagId")
    List<Task> getTasksForTag(int tagId);

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId")
    List<Tag> getTagsForTask(long taskId);

    @Insert
    void insert(TaskTag taskTag);
}
