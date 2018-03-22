package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "tasks")
class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int duration;

    @ColumnInfo(name = "duration_units")
    public String durationUnits;

    @ColumnInfo(name = "start_date")
    public Date startDate;

    @ColumnInfo(name = "end_date")
    public Date endDate;

    public boolean repeats;
}
