package caseydlvr.recurringtasks.model;

import android.content.Context;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

public class NotificationOption {
    private static final String TAG = NotificationOption.class.getSimpleName();

    public static final String KEY_NEVER = "never";
    public static final String KEY_OVERDUE = "overdue";
    public static final String KEY_OVERDUE_DUE = "overdue_due";

    private static final int COUNT = 3;
    private static final int INDEX_OVERDUE_DUE = 0;
    private static final int INDEX_OVERDUE = 1;
    private static final int INDEX_NEVER = 2;

    private String mKey;
    private String mName;

    private NotificationOption(@NonNull Context context, String optionKey) {
        mKey = optionKey;

        switch (optionKey) {
            case KEY_OVERDUE_DUE:
                mName = context.getString(R.string.notificationOption_OverdueDue);
                break;
            case KEY_OVERDUE:
                mName = context.getString(R.string.notificationOption_Overdue);
                break;
            default: // KEY_NEVER
                mKey = KEY_NEVER;
                mName = context.getString(R.string.notificationOption_Never);
        }
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * @param context Context to use for fetching String resources
     * @return        List containing all NotificationOptions
     */
    public static List<NotificationOption> buildList(@NonNull Context context) {
        List<NotificationOption> notificationOptions = new ArrayList<>(COUNT);
        notificationOptions.add(INDEX_OVERDUE_DUE, new NotificationOption(context, KEY_OVERDUE_DUE));
        notificationOptions.add(INDEX_OVERDUE, new NotificationOption(context, KEY_OVERDUE));
        notificationOptions.add(INDEX_NEVER, new NotificationOption(context, KEY_NEVER));

        return notificationOptions;
    }

    /**
     * Gets the Index of the NotificationOption specified by key in the List returned by
     * buildList(Context).
     *
     * An example of when this is useful: buildList is used to populate a spinner and a list index
     * must be specified to set which NotificationUnit in the spinner should be selected,
     * but the user only has a key available. Getting the index using getIndex saves the user
     * from searching the list for a DurationUnit with a matching key just to determine the
     * appropriate index.
     *
     * @param key KEY_ constant
     * @return    Index of the NotificationOption in the List returned by buildList(Context)
     * @see       #buildList(Context)
     */
    public static int getIndex(String key) {
        int index;

        switch (key) {
            case KEY_OVERDUE_DUE:
                index = INDEX_OVERDUE_DUE;
                break;
            case KEY_OVERDUE:
                index = INDEX_OVERDUE;
                break;
            default:
                index = INDEX_NEVER;
        }

        return index;
    }
}
