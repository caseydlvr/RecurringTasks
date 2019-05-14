package caseydlvr.recurringtasks.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.model.Tag;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TagDao {

    @Query("Select * FROM tags ORDER BY name")
    LiveData<List<Tag>> observeAll();

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId " +
            "ORDER BY tags.name")
    LiveData<List<Tag>> observeByTask(long taskId);

    @Query("SELECT * FROM tags WHERE id = :tagId")
    LiveData<Tag> observeById(int tagId);

    @Query("SELECT * FROM tags WHERE NOT synced")
    List<Tag> loadUnsynced();

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId " +
            "ORDER BY tags.name")
    List<Tag> loadByTask(long taskId);

    @Insert(onConflict = REPLACE)
    long[] insert(Tag... tags);

    @Update
    void update(Tag... tags);

    @Delete
    void delete(Tag... tags);
}
