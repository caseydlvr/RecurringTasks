package caseydlvr.recurringtasks.model;

public class DurationUnit {
    private String mKey;
    private String mName;
    private String mNameSingular;

    public DurationUnit(String key, String name, String nameSingular) {
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

    public String getNameSignular() {
        return mNameSingular;
    }

    @Override
    public String toString() {
        return mName;
    }
}
