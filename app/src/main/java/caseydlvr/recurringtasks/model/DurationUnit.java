package caseydlvr.recurringtasks.model;

public class DurationUnit {
    private String mId;
    private String mName;

    public DurationUnit(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
