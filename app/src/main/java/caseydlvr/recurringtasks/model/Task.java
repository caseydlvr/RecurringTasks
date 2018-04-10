package caseydlvr.recurringtasks.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Comparator;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "duration")
    private int mDuration;

    @ColumnInfo(name = "duration_unit")
    private String mDurationUnit;

    @ColumnInfo(name = "start_date")
    private LocalDate mStartDate;

    @ColumnInfo(name = "end_date")
    private LocalDate mEndDate;

    @ColumnInfo(name = "repeats")
    private boolean mRepeats;

    public Task() {
        mDuration = 1;
        mDurationUnit = "day";
        mStartDate = LocalDate.now();
        mRepeats = true;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public String getDurationUnit() {
        return mDurationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        mDurationUnit = durationUnit;
    }

    public LocalDate getDueDate() {
        LocalDate dueDate;

        switch (mDurationUnit) {
            case "day":
                dueDate = mStartDate.plusDays(mDuration);
                break;
            case "week":
                dueDate = mStartDate.plusWeeks(mDuration);
                break;
            case "month":
                dueDate = mStartDate.plusMonths(mDuration);
                break;
            case "year":
                dueDate = mStartDate.plusYears(mDuration);
                break;
            default:
                dueDate = LocalDate.now();
                break;
        }

        return dueDate;
    }

    public int getPriority() {
        int priority;
        LocalDate today = LocalDate.now();
        LocalDate dueDate = getDueDate();
        int durationDays = (int) ChronoUnit.DAYS.between(mStartDate, dueDate);
        int daysSinceStartDate = (int) ChronoUnit.DAYS.between(mStartDate, today);
        double percentOfDuration = ((double) daysSinceStartDate )/ ((double) durationDays);
        int gracePeriod = (durationDays / 7) > 0 ? (durationDays / 7) : 1;
        LocalDate dueStart = dueDate.minusDays(gracePeriod);
        LocalDate dueEnd = dueDate.plusDays(gracePeriod);

        if (today.isAfter(dueEnd)) priority = 0;
        else if (today.isBefore(dueEnd) && today.isAfter(dueStart)) priority = 1;
        else priority = 10;

        return priority;
    }

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public void setStartDate(LocalDate startDate) {
        mStartDate = startDate;
    }

    public LocalDate getEndDate() {
        return mEndDate;
    }

    public void setEndDate(LocalDate endDate) {
        mEndDate = endDate;
    }

    public boolean isRepeats() {
        return mRepeats;
    }

    public void setRepeats(boolean repeats) {
        mRepeats = repeats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Task.class.isAssignableFrom(obj.getClass())) return false;

        final Task other = (Task) obj;

        return !(other.getId() != mId
                || other.getName().equals(mName)
                || other.getDuration() != mDuration
                || other.getDurationUnit().equals(mDurationUnit)
                || other.isRepeats() != mRepeats);
    }

    public static class DueDateComparator implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            return o1.getDueDate().isBefore(o2.getDueDate()) ? -1 : o1.getDueDate().isEqual(o2.getDueDate()) ? 0 : 1;
        }
    }
}
