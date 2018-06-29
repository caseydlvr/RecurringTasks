package caseydlvr.recurringtasks.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import caseydlvr.recurringtasks.model.Tag;

public interface TagDao {

    @Query("Select * FROM tags")
    LiveData<List<Tag>> getAllTags();

    @Insert
    int insert(Tag tag);

    @Update
    void update(Tag tag);

    @Delete
    void delete(Tag... tags);
}
