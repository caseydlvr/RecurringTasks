package caseydlvr.recurringtasks.sync;

import android.content.Context;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import caseydlvr.recurringtasks.RecurringTaskApp;
import retrofit2.HttpException;

public class FullExportWorker extends Worker {

    private static final int MAX_ATTEMPTS = 4;

    public FullExportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Sync sync = new Sync((RecurringTaskApp) getApplicationContext());

        try {
            sync.startFulLExport();
        } catch (IOException e) {
            if (getRunAttemptCount() > MAX_ATTEMPTS) {
                return Result.failure();
            } else {
                return Result.retry();
            }
        } catch (HttpException e) {
            return Result.failure();
        }

        return Result.success();
    }
}
