package caseydlvr.recurringtasks.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import caseydlvr.recurringtasks.RecurringTaskApp;

public class FullExportWorker extends Worker {

    public FullExportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Sync sync = new Sync((RecurringTaskApp) getApplicationContext());

        sync.startFulLExport();

        return Result.success();
    }
}
