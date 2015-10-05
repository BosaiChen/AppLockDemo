package bosaichen.com.pollingtestapp;

import android.content.Context;

public abstract class ForegroundAppPollingSolution{
    protected String TAG = "pollingsolution";

    protected Context mCtx;

    protected String mPreviousApp = "";
    protected String mCurrentApp = "";

    protected ForegroundAppPollingSolution(Context ctx) {
        mCtx = ctx;
    }
    abstract public void pollForegroundApp();
}
