package bosaichen.com.pollingtestapp;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class LockScreen extends Service implements View.OnClickListener{
    private static final String TAG = "LockScreen";

    private WindowManager mWindowManager;
    private LinearLayout mLayout;

    private Button mBtnUnlock;

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    private void initLayouts() {
        WindowManager.LayoutParams wmlp = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        wmlp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        wmlp.alpha = 1.0f;
        wmlp.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wmlp.flags &= ~(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        wmlp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        wmlp.format = -1;
        wmlp.token = null;

        mLayout = new LinearLayout(this) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (mLinearLayoutwbview.getVisibility() == View.VISIBLE) {
//                        mLinearLayoutwbview.setVisibility(View.GONE);
//                        mLinearLayoutpin.setVisibility(View.VISIBLE);
//                    } else {
                    cancelAndGoHome();
//                    }
                    return true;
                }

                return super.dispatchKeyEvent(event);
            }
        };
        mLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));

        View view = LayoutInflater.from(this).inflate(R.layout.lock_screen, mLayout, true);
        mBtnUnlock = (Button) view.findViewById(R.id.btn_unlock);
        mBtnUnlock.setOnClickListener(this);
        try {
            mWindowManager.addView(mLayout, wmlp);
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to add lock page.", e);
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

//        String packageName = intent.getStringExtra(PACKAGE_NAME);
//        Log.v(TAG, String.format("onStartCommand: %s", packageName));

//        if (TextUtils.isEmpty(packageName)) {
//            stopSelf();
//            return START_NOT_STICKY;
//        }

        if (mLayout == null) {
            initLayouts();
        }

//        mPackageName = packageName;
        return START_STICKY;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_unlock) {
            cancelAndGoHome();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLockScreen();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void cancelAndGoHome() {
        Intent startHome = new Intent(Intent.ACTION_MAIN);
        startHome.addCategory(Intent.CATEGORY_HOME);
        startHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startHome);

        // Post a delayed dismiss so home screen can show up when removing lock screen.
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                removeLockScreen();
                stopSelf();
            }
        }, 1000);
    }

    private void removeLockScreen() {
        if (mLayout != null) {
            try {
                mWindowManager.removeView(mLayout);
            } catch (IllegalStateException e) {
                Log.e(TAG, "View not attached.");
            }

            mLayout = null;
        }
    }
}
