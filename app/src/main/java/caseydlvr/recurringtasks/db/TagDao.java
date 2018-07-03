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

    @Query("Select * FROM tags")
    LiveData<List<Tag>> getAllTags();

    @Insert(onConflict = REPLACE)
    long insert(Tag tag);

    @Update
    void update(Tag tag);

    @Delete
    void delete(Tag... tags);
}
