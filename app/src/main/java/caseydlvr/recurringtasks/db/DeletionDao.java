package caseydlvr.recurringtasks.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import caseydlvr.recurringtasks.model.Deletion;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DeletionDao {

    @Query("SELECT tasks.server_id AS task_id, 0 as tag_id " +
            "FROM deletions " +
            "JOIN tasks ON deletions.task_id = tasks.id " +
            "WHERE task_id > 0 AND tag_id = 0")
    List<Deletion> loadTaskDeletionsWithServerId();

    @Query("SELECT 0 as task_id, tags.server_id as tag_id " +
            "FROM deletions " +
            "JOIN tags ON deletions.tag_id = tags.id " +
            "WHERE tag_id > 0 AND task_id = 0")
    List<Deletion> loadTagDeletionsWithServerId();

    @Insert(onConflict = REPLACE)
    void insert(Deletion... deletions);

    @Delete
    void delete(Deletion... deletions);

    @Query("DELETE FROM deletions WHERE task_id = :taskId AND tag_id = 0")
    void deleteRelationsForTask(long taskId);

    @Query("DELETE FROM deletions WHERE tag_id = :tagId AND task_id = 0")
    void deleteRelationsForTag(int tagId);
}
