package bosaichen.com.pollingtestapp;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import java.util.List;

public class Utils {
    public static void runOnMainThread(Context ctx, Runnable r) {
        new Handler(ctx.getMainLooper()).post(r);
    }

    public static String  getSDKVersionName(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static boolean hasPermission(Context ctx, String permission) {
        PackageManager pm = ctx.getPackageManager();
        return pm.checkPermission(permission, ctx.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasUsageAccessPermission(Context ctx) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
        final List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis());

        return !queryUsageStats.isEmpty();
    }

    public static void goHome(Context ctx) {
        Intent startHome = new Intent(Intent.ACTION_MAIN);
        startHome.addCategory(Intent.CATEGORY_HOME);
        startHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(startHome);
    }
}
