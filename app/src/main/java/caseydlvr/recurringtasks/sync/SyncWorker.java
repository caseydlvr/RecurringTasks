package caseydlvr.recurringtasks.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import caseydlvr.recurringtasks.RecurringTaskApp;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Sync sync = new Sync((RecurringTaskApp) getApplicationContext());

        sync.start();

        return Result.success();
    }
}
