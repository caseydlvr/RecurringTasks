package caseydlvr.recurringtasks.sync;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;

public class SyncWorkRequests {

    public static final String FULL_EXPORT_WORK_NAME = "full_export_work";

    public static PeriodicWorkRequest recurringSyncWorkRequest() {
        Constraints constraints = baseConstraints()
                .setRequiresBatteryNotLow(true)
                .build();

        return new PeriodicWorkRequest.Builder(SyncWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
    }

    public static OneTimeWorkRequest fullExportWorkRequest() {
        Constraints constraints = baseConstraints().build();

        return new OneTimeWorkRequest.Builder(FullExportWorker.class)
                .setConstraints(constraints)
                .build();
    }

    private static Constraints.Builder baseConstraints() {
        return new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED);
    }
}
