package caseydlvr.recurringtasks.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

public class DurationUnits {
    public static final String KEY_DAY = "day";
    public static final String KEY_WEEK = "week";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    private static final int COUNT = 4;
    private static final int INDEX_DAY = 0;
    private static final int INDEX_WEEK = 1;
    private static final int INDEX_MONTH = 2;
    private static final int INDEX_YEAR = 3;

    public static List<DurationUnit> getList(Context context) {
        ArrayList<DurationUnit> durationUnits = new ArrayList<>(COUNT);
        durationUnits.add(INDEX_DAY, new DurationUnit(KEY_DAY, context.getString(R.string.days)));
        durationUnits.add(INDEX_WEEK, new DurationUnit(KEY_WEEK, context.getString(R.string.weeks)));
        durationUnits.add(INDEX_MONTH, new DurationUnit(KEY_MONTH, context.getString(R.string.months)));
        durationUnits.add(INDEX_YEAR, new DurationUnit(KEY_YEAR, context.getString(R.string.years)));

        return durationUnits;
    }

    public static DurationUnit getDurationUnit(Context context, String key) {
        DurationUnit durationUnit;

        switch (key) {
            case KEY_DAY:
                durationUnit = new DurationUnit(KEY_DAY, context.getString(R.string.days));
                break;
            case KEY_WEEK:
                durationUnit = new DurationUnit(KEY_WEEK, context.getString(R.string.weeks));
                break;
            case KEY_MONTH:
                durationUnit = new DurationUnit(KEY_MONTH, context.getString(R.string.months));
                break;
            case KEY_YEAR:
                durationUnit = new DurationUnit(KEY_YEAR, context.getString(R.string.years));
                break;
            default:
                durationUnit = new DurationUnit(KEY_DAY, context.getString(R.string.days));
        }

        return durationUnit;
    }

    public static int getIndex(String key) {
        int index;

        switch (key) {
            case KEY_DAY:
                index = INDEX_DAY;
                break;
            case KEY_WEEK:
                index = INDEX_WEEK;
                break;
            case KEY_MONTH:
                index = INDEX_MONTH;
                break;
            case KEY_YEAR:
                index = INDEX_YEAR;
                break;
            default:
                index = 0;
        }

        return index;
    }
}
