package caseydlvr.recurringtasks.model;

public class DurationUnit {
    private String mKey;
    private String mName;

    public DurationUnit(String key, String name) {
        mKey = key;
        mName = name;
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
}
