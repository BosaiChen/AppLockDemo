package bosaichen.com.pollingtestapp;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UsageAccessPollSolution extends ForegroundAppPollingSolution {
    private UsageStatsManager mUsageStatsManager;

    public UsageAccessPollSolution(Context ctx) {
        super(ctx);
        mUsageStatsManager = (UsageStatsManager) mCtx.getSystemService("usagestats");
        Log.d(TAG, "Usage Access Poll Solution chosen");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void pollForegroundApp() {
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
//        calendar.add(Calendar.DATE, -2);
        long startTime = endTime - 5 * 1000;
        List<UsageStats> statsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (statsList == null) {
            return;
        }

        // Sort the stats by the last time used
        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
        for (UsageStats usageStats : statsList) {
            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }
        if (mySortedMap == null || mySortedMap.isEmpty()) {
            return;
        }

        String newPkg = mySortedMap.get(mySortedMap.lastKey()).getPackageName();

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
            final String toastNewPkg = newPkg;
            Utils.runOnMainThread(mCtx, new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mCtx.getApplicationContext(), toastNewPkg, Toast.LENGTH_SHORT).show();
                    mCtx.startService(new Intent(mCtx, LockScreen.class));
                }
            });
        }
    }
}
