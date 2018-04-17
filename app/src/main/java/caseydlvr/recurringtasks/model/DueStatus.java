package caseydlvr.recurringtasks.model;

import android.content.Context;

import caseydlvr.recurringtasks.R;

public class DueStatus {
    public static final int PRIORITY_OVERDUE = 0;
    public static final int PRIORITY_DUE = 1;
    public static final int PRIORITY_DEFAULT = 2;

    private int mPriority;
    private int mColor;
    private String mName;
    private boolean isDefault = false;

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

    public int getPriority() {
        return mPriority;
    }

    public int getColor() {
        return mColor;
    }

    public String getName() {
        return mName;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
