package caseydlvr.recurringtasks.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import caseydlvr.recurringtasks.R;

/**
 * Represents a unit of time duration, such as 'day', 'week'. Includes a key string, as well as
 * display strings for both singular and plural versions of the duration. The key string is a
 * constant, while the display strings are localized (and therefore dynamic).
 *
 * Includes static convenience methods for building individual DurationUnits as well as Collections
 * of DurationUnits. These static methods require a Context in order to fetch the display names from
 * Resources (allowing localization of the display names).
 */
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

    /**
     * @return KEY_ String constant for this DurationUnit
     */
    public String getKey() {
        return mKey;
    }

    /**
     * @return Localized plural display String for this DurationUnit. For displaying the unit in the UI.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return Localized singular display String for this DurationUnit. For displaying the unit in the UI.
     */
    public String getNameSingular() {
        return mNameSingular;
    }

    @Override
    public String toString() {
        return mName;
    }

    /**
     * @param context Context to use for fetching String resources
     * @return        List containing all DurationUnits
     */
    public static List<DurationUnit> buildList(Context context) {
        ArrayList<DurationUnit> durationUnits = new ArrayList<>(COUNT);
        durationUnits.add(INDEX_DAY, buildDay(context));
        durationUnits.add(INDEX_WEEK, buildWeek(context));
        durationUnits.add(INDEX_MONTH, buildMonth(context));
        durationUnits.add(INDEX_YEAR, buildYear(context));

        return durationUnits;
    }

    /**
     * Builds a DurationUnit for the given key. Uses String resources to set mName and mNameSingular
     * so that these display Strings are localized.
     *
     * @param context Context to use for fetching String resources
     * @param key     KEY_ constant of the DurationUnit to build
     * @return        DurationUnit for the given key
     */
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

    /**
     * Gets the Index of the DurationUnit specified by key in the List returned by buildList(Context).
     *
     * An example of when this is useful: buildList is used to populate a spinner and a list index
     * must be specified to set which DurationUnit in the spinner should be selected, but the user
     * only has a key available. Getting the index using getIndex saves the user from searching
     * the list for a DurationUnit with a matching key just to determine the appropriate index.
     *
     * @param key KEY_ constant
     * @return    Index of the DurationUnit in the List returned by buildList(Context)
     * @see       #buildList(Context)
     */
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
