package caseydlvr.recurringtasks.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

public class DurationUnit {
    public static final String KEY_DAY = "day";
    public static final String KEY_WEEK = "week";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
    private static final int COUNT = 4;
    private static final int INDEX_DAY = 0;
    private static final int INDEX_WEEK = 1;
    private static final int INDEX_MONTH = 2;
    private static final int INDEX_YEAR = 3;

    private String mKey;
    private String mName;
    private String mNameSingular;

    private DurationUnit(String key, String name, String nameSingular) {
        mKey = key;
        mName = name;
        mNameSingular = nameSingular;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getNameSingular() {
        return mNameSingular;
    }

    @Override
    public String toString() {
        return mName;
    }

    public static List<DurationUnit> buildList(Context context) {
        ArrayList<DurationUnit> durationUnits = new ArrayList<>(COUNT);
        durationUnits.add(INDEX_DAY, buildDay(context));
        durationUnits.add(INDEX_WEEK, buildWeek(context));
        durationUnits.add(INDEX_MONTH, buildMonth(context));
        durationUnits.add(INDEX_YEAR, buildYear(context));

        return durationUnits;
    }

    public static DurationUnit build(Context context, String key) {
        DurationUnit durationUnit;

        switch (key) {
            case KEY_DAY:
                durationUnit = buildDay(context);
                break;
            case KEY_WEEK:
                durationUnit = buildWeek(context);
                break;
            case KEY_MONTH:
                durationUnit = buildMonth(context);
                break;
            case KEY_YEAR:
                durationUnit = buildYear(context);
                break;
            default:
                durationUnit = buildDay(context);
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
                index = INDEX_DAY;
        }

        return index;
    }

    private static DurationUnit buildDay(Context context) {
        return new DurationUnit(KEY_DAY,
                context.getString(R.string.days),
                context.getString(R.string.day));
    }

    private static DurationUnit buildWeek(Context context) {
        return new DurationUnit(KEY_WEEK,
                context.getString(R.string.weeks),
                context.getString(R.string.week));
    }

    private static DurationUnit buildMonth(Context context) {
        return new DurationUnit(KEY_MONTH,
                context.getString(R.string.months),
                context.getString(R.string.month));
    }

    private static DurationUnit buildYear(Context context) {
        return new DurationUnit(KEY_YEAR,
                context.getString(R.string.years),
                context.getString(R.string.year));
    }
}
