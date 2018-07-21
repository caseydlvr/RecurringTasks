package caseydlvr.recurringtasks.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Comparator;

/**
 * Represents a Task. Room entity for the Tasks table.
 */
@Entity(tableName = "tasks")
public class Task {

    public static final int DURATION_MAX = 999;
    public static final int DURATION_MIN = 1;
    public static final int NAME_MAX_LENGTH = 25;

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

    @ColumnInfo(name = "repeating")
    private boolean mRepeating;

    @ColumnInfo(name = "notification_option")
    private String mNotificationOption;

    // cache for calculated fields
    private transient LocalDate mDueDate;
    private transient int mDuePriority;

    /**
     * Constructor. Sets default values for the task.
     */
    public Task() {
        mName = "";
        mDuration = 1;
        mDurationUnit = DurationUnit.KEY_DAY;
        mStartDate = LocalDate.now();
        setDueDateFields();
        mRepeating = true;
        mNotificationOption = NotificationOption.KEY_OVERDUE_DUE;
    }

    /**
     * @return Unique ID for the Task. This is the DB key.
     */
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

    /**
     * duration is combined with durationUnit to indicate a period of time. For example, 5 days.
     * In this example, getDuration() returns 5.
     *
     * @return duration integer
     * @see    #getDurationUnit()
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Causes calculated due date fields to be recalculated
     *
     * @param duration duration integer
     */
    public void setDuration(int duration) {
        mDuration = duration;
        setDueDateFields();
    }

    /**
     * durationUnit is combined with duration to indicate a period of time. For example, 5 days.
     * In this example, getDurationUnit() returns the String defined by DurationUnit.KEY_DAY. All
     * possible durationUnit values correspond to a DurationUnit.KEY_ constant.
     *
     * @return String corresponding to a DurationUnit.KEY_ constant
     * @see    DurationUnit
     * @see    #getDuration()
     */
    public String getDurationUnit() {
        return mDurationUnit;
    }

    /**
     * Causes calculated due date fields to be recalculated
     *
     * @param durationUnit String corresponding to a DurationUnit.KEY_ constant
     * @see                DurationUnit
     */
    public void setDurationUnit(String durationUnit) {
        mDurationUnit = durationUnit;
        setDueDateFields();
    }

    /**
     * Due date is determined by adding the Task's duration (a combination of duration and durationUnit)
     * to the Task's startDate. This is a rough due date (the Task could be considered due before
     * this date, and due but not overdue after this date).
     *
     * The dueDate is calculated and cached whenever a field that influences the due date changes,
     * rather than calculating the due date every time getDueDate() is called.
     *
     * @return LocalDate representing the Task's rough due date
     */
    public LocalDate getDueDate() {
        return mDueDate;
    }

    /**
     * Priority is determined by comparing the current date to the due date. The duePriority is
     * calculated and cached whenever a field that influences the priority changes, rather than
     * calculating the priority every time getDuePriority() is called.
     *
     * The priority corresponds to a DueStatus.PRIORITY_ constant. A lower int value is higher
     * priority. Return value should be compared to PRIORITY_ constants, rather than hardcoded
     * int values.
     *
     * @return int corresponding to a DueStatus.PRIORITY_ constant
     * @see    DueStatus
     */
    public int getDuePriority() {
        return mDuePriority;
    }

    /**
     * @return LocalDate representing the start of the Task's duration period
     */
    public LocalDate getStartDate() {
        return mStartDate;
    }

    /**
     * Causes calculated due date fields to be recalculated
     *
     * @param startDate LocalDate representing the start of the Task's duration period
     */
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

    /**
     * @return boolean representing whether the Task automatically repeats after completion
     */
    public boolean isRepeating() {
        return mRepeating;
    }

    public void setRepeating(boolean repeats) {
        mRepeating = repeats;
    }

    public String getNotificationOption() {
        return mNotificationOption;
    }

    public void setNotificationOption(String notificationOption) {
        mNotificationOption = notificationOption;
    }

    /**
     * @return boolean representing whether notifications for this Task should be shown
     */
    public boolean usesNotifications() {
        return !mNotificationOption.equals(NotificationOption.KEY_NEVER);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Task.class.isAssignableFrom(obj.getClass())) return false;

        final Task other = (Task) obj;

        return (other.getId() == mId
                && other.getName().equals(mName)
                && other.getDuration() == mDuration
                && other.getDurationUnit().equals(mDurationUnit)
                && other.getStartDate().equals(mStartDate)
                && other.isRepeating() == mRepeating)
                && other.getNotificationOption().equals(mNotificationOption);
    }

    /**
     * Calculates and caches all fields that are related to the tasks due date.
     */
    private void setDueDateFields() {
        calculateDueDate();
        calculateDuePriority();
    }

    /**
     * Calculates and caches (as a field) the rough due date using duration, durationUnit and
     * startDate.
     *
     * @see #getDueDate()
     */
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

    /**
     * Calculates and caches (as a field) the duePriority. Uses dueDate and startDate. Since dueDate
     * is a calculated field, calculateDueDate() should always be called before calculateDuePriority().
     *
     * @see #getDuePriority()
     */
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

    /**
     * Comparator for Task. A Task is less than another Task if its duePriority is less than the
     * other's duePriority. If the due priorities are equal, then a Task is less than another Task
     * if its dueDate is before (a date comparison) the other's due date. This results in a List of
     * Tasks sorted with this Comparator sorted in the order of highest priority task to lowest
     * priority task.
     */
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
