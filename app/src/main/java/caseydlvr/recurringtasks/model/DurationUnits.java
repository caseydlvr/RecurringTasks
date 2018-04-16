package caseydlvr.recurringtasks.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

public class DurationUnits {
    private static final int COUNT = 4;
    private static final String ID_DAY = "day";
    private static final String ID_WEEK = "week";
    private static final String ID_MONTH = "month";
    private static final String ID_YEAR = "year";
    private static final int INDEX_DAY = 0;
    private static final int INDEX_WEEK = 1;
    private static final int INDEX_MONTH = 2;
    private static final int INDEX_YEAR = 3;

    public static List<DurationUnit> getList(Context context) {
        ArrayList<DurationUnit> durationUnits = new ArrayList<>(COUNT);
        durationUnits.add(INDEX_DAY, new DurationUnit(ID_DAY, context.getString(R.string.days)));
        durationUnits.add(INDEX_WEEK, new DurationUnit(ID_WEEK, context.getString(R.string.weeks)));
        durationUnits.add(INDEX_MONTH, new DurationUnit(ID_MONTH, context.getString(R.string.months)));
        durationUnits.add(INDEX_YEAR, new DurationUnit(ID_YEAR, context.getString(R.string.years)));

        return durationUnits;
    }

    public static DurationUnit getDurationUnit(Context context, String key) {
        DurationUnit durationUnit;

        switch (key) {
            case ID_DAY:
                durationUnit = new DurationUnit(ID_DAY, context.getString(R.string.days));
                break;
            case ID_WEEK:
                durationUnit = new DurationUnit(ID_WEEK, context.getString(R.string.weeks));
                break;
            case ID_MONTH:
                durationUnit = new DurationUnit(ID_MONTH, context.getString(R.string.months));
                break;
            case ID_YEAR:
                durationUnit = new DurationUnit(ID_YEAR, context.getString(R.string.years));
                break;
            default:
                durationUnit = new DurationUnit(ID_DAY, context.getString(R.string.days));
        }

        return durationUnit;
    }

    public static int getIndex(String key) {
        int index;

        switch (key) {
            case ID_DAY:
                index = INDEX_DAY;
                break;
            case ID_WEEK:
                index = INDEX_WEEK;
                break;
            case ID_MONTH:
                index = INDEX_MONTH;
                break;
            case ID_YEAR:
                index = INDEX_YEAR;
                break;
            default:
                index = 0;
        }

        return index;
    }
}
