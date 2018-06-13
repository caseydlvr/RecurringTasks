package caseydlvr.recurringtasks.model;

import android.content.Context;

import caseydlvr.recurringtasks.R;

/**
 * Represents the due status of a Task, such as "overdue" or "due". In addition to an integer to
 * represent the priority of the status relative to other status', the class also contains a display
 * String and display color to use when displaying the due status in the UI. The display fields are
 * loaded from XML resources, so a Context is required.
 */
public class DueStatus {
    public static final int PRIORITY_OVERDUE = 0;
    public static final int PRIORITY_DUE = 1;
    public static final int PRIORITY_DEFAULT = 2;

    private int mPriority;
    private int mColor;
    private String mName;
    private boolean isDefault = false;

    /**
     * @param context Context to use for getting resources
     * @param priority a PRIORITY_ constant. Don't pass a hardcoded integer
     */
    public DueStatus(Context context, int priority) {
        mPriority = priority;

        switch (priority) {
            case PRIORITY_OVERDUE:
                mColor = context.getResources().getColor(R.color.overdueColor);
                mName = context.getString(R.string.overdueStatus);
                break;
            case PRIORITY_DUE:
                mColor = context.getResources().getColor(R.color.dueColor);
                mName = context.getString(R.string.dueStatus);
                break;
            default:
                isDefault = true;
        }
    }

    /**
     * Priority corresponds to a PRIORITY_ constant. The lower the number, the higher the priority.
     * 0 is the highest priority. Always compare to a PRIORITY_ constant rather than hardcoded
     * integer value, as the int values of the PRIORITY_ constants are not guaranteed to stay the same.
     *
     * @return the priority
     */
    public int getPriority() {
        return mPriority;
    }

    /**
     * The color associated with this due status. Use when setting UI elements to a color to
     * represent this due status. For example, the background color of a View.
     *
     * @return a color integer
     */
    public int getColor() {
        return mColor;
    }

    /**
     * The display name of this due status. This is loaded from string resources, so this name is
     * localized. Use this whenever displaying the due status in the UI.
     *
     * @return the display String for the priority
     */
    public String getName() {
        return mName;
    }

    /**
     * Convenience method for checking if this is the default status. Allows user to write:
     * if (status.isDefault()) instead of if (status.getPriority() == DueStatus.PRIORITY_DEFAULT)
     *
     * @return boolean indicating if the priority is equal to PRIORITY_DEFAULT
     */
    public boolean isDefault() {
        return isDefault;
    }
}
