package caseydlvr.recurringtasks.sync;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.Operation;
import androidx.work.WorkManager;

public class SyncActions {

    private static final String RECURRING_SYNC_WORK_NAME = "recurring_sync_work";
    private static final String ONE_TIME_SYNC_WORK_NAME = "one_time_sync_work";
    private static final String FULL_EXPORT_WORK_NAME = "full_export_work";

    public static Operation enqueueRecurringSync() {
        return WorkManager.getInstance().enqueueUniquePeriodicWork(
                RECURRING_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                SyncWorkRequests.recurringSyncWorkRequest());
    }

    public static Operation enqueueOneTimeSync() {
        return WorkManager.getInstance().enqueueUniqueWork(
                ONE_TIME_SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorkRequests.oneTimeSyncWorkRequest());
    }

    public static Operation enqueueFullExport() {
        return WorkManager.getInstance().enqueueUniqueWork(
                FULL_EXPORT_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorkRequests.fullExportWorkRequest());
    }
}
