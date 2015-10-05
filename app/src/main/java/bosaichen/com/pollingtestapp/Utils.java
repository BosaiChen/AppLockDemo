package bosaichen.com.pollingtestapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

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
}
