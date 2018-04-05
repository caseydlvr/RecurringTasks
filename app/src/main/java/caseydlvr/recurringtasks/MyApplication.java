package caseydlvr.recurringtasks;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import caseydlvr.recurringtasks.db.AppDatabase;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    public AppDatabase getDb() {
        return AppDatabase.getInstance(this);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDb());
    }
}
