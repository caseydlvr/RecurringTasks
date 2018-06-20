package caseydlvr.recurringtasks.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

public class NotificationOption {
    private static final String TAG = NotificationOption.class.getSimpleName();

    public static final String KEY_NEVER = "never";
    public static final String KEY_OVERDUE = "overdue";
    public static final String KEY_OVERDUE_DUE = "overdue_due";

    private static final int COUNT = 3;
    private static final int INDEX_NEVER = 0;
    private static final int INDEX_OVERDUE = 1;
    private static final int INDEX_OVERDUE_DUE = 2;

    private String mOption;
    private String mName;

    public NotificationOption(@NonNull Context context, String option) {
        mOption = option;

        switch (option) {
            case KEY_OVERDUE:
                mName = context.getString(R.string.notificationOption_Overdue);
                break;
            case KEY_OVERDUE_DUE:
                mName = context.getString(R.string.notificationOption_OverdueDue);
                break;
            default: // KEY_NEVER
                mOption = KEY_NEVER;
                mName = context.getString(R.string.notificationOption_Never);
        }
    }

    public String getOption() {
        return mOption;
    }

    public String getName() {
        return mName;
    }

    public static List<NotificationOption> buildList(@NonNull Context context) {
        List<NotificationOption> notificationOptions = new ArrayList<>(COUNT);
        notificationOptions.add(INDEX_NEVER, new NotificationOption(context, KEY_NEVER));
        notificationOptions.add(INDEX_OVERDUE, new NotificationOption(context, KEY_OVERDUE));
        notificationOptions.add(INDEX_OVERDUE_DUE, new NotificationOption(context, KEY_OVERDUE_DUE));

        return notificationOptions;
    }
}
