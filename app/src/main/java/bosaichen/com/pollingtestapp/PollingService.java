package bosaichen.com.pollingtestapp;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PollingService extends Service {
    private static final String TAG = "PollingService";
    private static final int APP_FOREGROUND_POLL = 0;

    private String currentPkg = "";
    private String previousPkg = "";

//    private ServiceHandler mServiceHandler;
    private ForegroundAppPollingSolution mPollingSolution;
    private Timer timer;

    /*private class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case APP_FOREGROUND_POLL:
                    checkForegroundApp();
                    break;
            }
        }
    }*/

    @Override
    public void onCreate() {
        Log.d(TAG, "service oncreate()");
        super.onCreate();
        HandlerThread thread = new HandlerThread("AppForegroundPollingServiceThread");
        thread.start();

//        mServiceHandler = new ServiceHandler(thread.getLooper());

//        startRepeatedPolling();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        mServiceHandler.sendEmptyMessage(APP_FOREGROUND_POLL);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mPollingSolution = new PreLPollSolution(this.getApplicationContext());
        } else {
            mPollingSolution = new UsageAccessPollSolution(this.getApplicationContext());
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                checkForegroundApp();
                mPollingSolution.pollForegroundApp();
            }
        }, 500, /*frequency*/1000);
        return START_STICKY;
    }

    private void startRepeatedPolling() {
//        stopAlarmManager();
     /*   Intent pollingIntent = new Intent(this, PollingService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, pollingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 500, pi);*/
        /*mServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForegroundApp();
            }
        }, 500);*/

    }

    /*private void stopAlarmManager()
    {
        Intent pollingIntent = new Intent(this, PollingService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, pollingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }*/

    private void checkForegroundApp() {
        final String newPkg = findForegroundPkg();
        if (newPkg == null) {
            Log.d(TAG, "Foreground process is NULL? Is getRunningAppProcesses() working correctly?");
            return;
        }

//        Toast.makeText(this, newPkg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Foreground app: [" + newPkg + "]");

        boolean changed = !newPkg.equalsIgnoreCase(currentPkg);
        Log.d(TAG, "Foreground App changed? " + changed);
        Log.d(TAG, "=========End this poll===========");
        if (changed) {
            previousPkg = currentPkg;
            currentPkg = newPkg;
            // class name is not available implementing with getRunningAppProcesses(). Do not write logic relying on class name.
            final ComponentName foreground = new ComponentName(currentPkg, "");
            final ComponentName background = new ComponentName(previousPkg, "");
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), newPkg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, ">>>onDestroy<<<");
//        mServiceHandler.getLooper().quit();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    private String findForegroundPkg() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // Before 5.0, getRunningTasks() is considered reliable but is deprecated as of 5.0
            return activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } else {
            // TODO: find alternative solution to support 5.0 and later versions.
            // Checking Foreground flag in getRunningAppProcesses() may cause multiple processes have the same flag.
            // E.g. Apps playing music at background may start the service with Foreground priority. (Service#startForeground())
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();
            if (runningProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            }
            return null;
        }
    }
}