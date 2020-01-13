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

    @Query("SELECT * FROM deletions WHERE task_id != '' AND tag_id = ''")
    List<Deletion> loadTaskDeletions();

    @Query("SELECT * FROM deletions WHERE task_id = '' AND tag_id != ''")
    List<Deletion> loadTagDeletions();

    @Insert(onConflict = REPLACE)
    void insert(Deletion... deletions);

    @Delete
    void delete(Deletion... deletions);
}
