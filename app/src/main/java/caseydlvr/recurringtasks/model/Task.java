package caseydlvr.recurringtasks.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Comparator;

@Entity(tableName = "tasks")
public class Task {

    public static final int DURATION_MAX = 999;
    public static final int DURATION_MIN = 1;
    public static final int NAME_MAX_LENGTH = 20;

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

    // cache for calculated fields
    private transient LocalDate mDueDate;
    private transient int mDuePriority;

    public Task() {
        mName = "";
        mDuration = 1;
        mDurationUnit = DurationUnit.KEY_DAY;
        mStartDate = LocalDate.now();
        setDueDateFields();
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
        setDueDateFields();
    }

    public String getDurationUnit() {
        return mDurationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        mDurationUnit = durationUnit;
        setDueDateFields();
    }

    public LocalDate getDueDate() {
        return mDueDate;
    }

    public int getDuePriority() {
        return mDuePriority;
    }

    public LocalDate getStartDate() {
        return mStartDate;
    }

    public void setStartDate(LocalDate startDate) {
        mStartDate = startDate;
        setDueDateFields();
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
                || other.getStartDate().equals(mStartDate)
                || other.isRepeats() != mRepeats);
    }

    private void setDueDateFields() {
        calculateDueDate();
        calculateDuePriority();
    }

    private void calculateDueDate() {
        LocalDate dueDate;

        switch (mDurationUnit) {
            case DurationUnit.KEY_DAY:
                dueDate = mStartDate.plusDays(mDuration);
                break;
            case DurationUnit.KEY_WEEK:
                dueDate = mStartDate.plusWeeks(mDuration);
                break;
            case DurationUnit.KEY_MONTH:
                dueDate = mStartDate.plusMonths(mDuration);
                break;
            case DurationUnit.KEY_YEAR:
                dueDate = mStartDate.plusYears(mDuration);
                break;
            default:
                dueDate = LocalDate.now();
                break;
        }

        mDueDate = dueDate;
    }

    private void calculateDuePriority() {
        LocalDate today = LocalDate.now();
        int durationDays = (int) ChronoUnit.DAYS.between(mStartDate, mDueDate);
        int daysSinceStartDate = (int) ChronoUnit.DAYS.between(mStartDate, today);
        double percentOfDuration = ((double) daysSinceStartDate )/ ((double) durationDays);
        int gracePeriod = (durationDays / 7) > 0 ? (durationDays / 7) : 1;
        LocalDate dueStart = mDueDate.minusDays(gracePeriod);
        LocalDate dueEnd = mDueDate.plusDays(gracePeriod);

        if (today.isAfter(dueEnd)) {
            mDuePriority = DueStatus.PRIORITY_OVERDUE;
        }
        else if (today.isBefore(dueEnd) && today.isAfter(dueStart)
                || today.isEqual(dueStart)
                || today.isEqual(dueEnd)) {
            mDuePriority = DueStatus.PRIORITY_DUE;
        }
        else {
            mDuePriority = DueStatus.PRIORITY_DEFAULT;
        }
    }

    public static class TaskComparator implements Comparator<Task> {

        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getDuePriority() < o2.getDuePriority()) return -1;
            else if (o1.getDuePriority() > o2.getDuePriority()) return 1;
            else if (o1.getDueDate().isBefore(o2.getDueDate())) return -1;
            else if (o1.getDueDate().isEqual(o2.getDueDate())) return 0;
            else return 1;
        }
    }
}
