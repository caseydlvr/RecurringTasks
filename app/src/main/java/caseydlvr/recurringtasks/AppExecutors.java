package caseydlvr.recurringtasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private final Executor mDiskIO;

    public AppExecutors() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    public Executor diskIO() {
        return mDiskIO;
    }

}
