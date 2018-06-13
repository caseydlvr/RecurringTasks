package caseydlvr.recurringtasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * BroadcastReceiver for the BOOT_COMPLETED system action. This receiver doesn't run any of its own
 * code, but it being triggered causes the Application's onCreate() to run, which does the
 * necessary initialization that is required for the app to function properly after a reboot.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // notifications initialized in App onCreate when this receiver runs
    }
}
