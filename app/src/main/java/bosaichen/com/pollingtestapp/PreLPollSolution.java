package bosaichen.com.pollingtestapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class PreLPollSolution extends ForegroundAppPollingSolution{
    private final ActivityManager mActivityManager;

    public PreLPollSolution(Context ctx) {
        super(ctx);
        Log.d(TAG, "Pre L Poll Solution chosen");
        mActivityManager = (ActivityManager) mCtx.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void pollForegroundApp() {
        final String newPkg = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        if (newPkg == null) {
            Log.d(TAG, "Foreground process is NULL? Is getRunningAppProcesses() working correctly?");
            return;
        }

        Log.d(TAG, "Foreground app: [" + newPkg + "]");

        final boolean changed = !newPkg.equalsIgnoreCase(mCurrentApp);
        Log.d(TAG, "Foreground App changed? " + changed);
        Log.d(TAG, "=========End this poll===========");
        if (changed) {
            mPreviousApp = mCurrentApp;
            mCurrentApp = newPkg;
            // class name is not available implementing with getRunningAppProcesses(). Do not write logic relying on class name.
            final ComponentName foreground = new ComponentName(mCurrentApp, "");
            final ComponentName background = new ComponentName(mPreviousApp, "");
            Utils.runOnMainThread(mCtx, new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mCtx.getApplicationContext(), newPkg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
