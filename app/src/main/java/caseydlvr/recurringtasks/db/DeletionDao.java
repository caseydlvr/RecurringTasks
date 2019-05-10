package caseydlvr.recurringtasks.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import caseydlvr.recurringtasks.model.Deletion;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DeletionDao {

    @Insert(onConflict = REPLACE)
    void insert(Deletion... deletions);

    @Query("DELETE FROM deletions WHERE task_id = :taskId AND tag_id = 0")
    void deleteRelationsForTask(long taskId);

    @Query("DELETE FROM deletions WHERE tag_id = :tagId AND task_id = 0")
    void deleteRelationsForTag(int tagId);
}
