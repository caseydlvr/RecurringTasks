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
    LiveData<List<Tag>> loadAll();

    @Query("SELECT * FROM tags WHERE id = :tagId")
    LiveData<Tag> loadById(int tagId);

    @Query("SELECT tags.* FROM tags " +
            "JOIN tasks_tags ON tags.id = tasks_tags.tag_id " +
            "WHERE tasks_tags.task_id = :taskId " +
            "ORDER BY tags.name")
    LiveData<List<Tag>> loadForTask(long taskId);

    @Insert(onConflict = REPLACE)
    long insert(Tag tag);

    @Update
    void update(Tag tag);

    @Delete
    void delete(Tag... tags);
}
