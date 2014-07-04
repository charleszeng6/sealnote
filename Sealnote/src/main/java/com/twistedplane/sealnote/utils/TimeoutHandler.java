package com.twistedplane.sealnote.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import com.twistedplane.sealnote.PasswordActivity;
import com.twistedplane.sealnote.data.DatabaseHandler;

/**
 * Handler implementing the password expiry and appropriate callbacks.
 * This is a singleton and manages timeout for complete application
 * with its own Handler instance.
 */
public class TimeoutHandler implements Runnable {
    final private Handler mHandler = new Handler();

    /**
     * Current activity to use. Always updated from all public
     * methods
     */
    private Activity mActivity;

    /**
     * Are we timed out?
     */
    private boolean mTimedOut = true;

    /**
     * Singleton instance
     */
    final private static TimeoutHandler mInstance = new TimeoutHandler();

    private TimeoutHandler() {}

    /**
     * Get singleton instance.
     */
    public static TimeoutHandler instance() {
        return mInstance;
    }

    /**
     * Method run after timeout. Recycles database and set state
     * as timed out.
     */
    @Override
    public void run() {
        DatabaseHandler db = new DatabaseHandler(mActivity);
        db.recycle();
        mTimedOut = true;
    }

    /**
     * Remove any pending timeout callbacks and halt the state
     */
    public void passwordTimeoutClear() {
        mHandler.removeCallbacks(mInstance, null);
        mTimedOut = false;
    }

    /**
     * Resets any existing/pending callbacks and resets the timeout/state
     * by postDelaying another callback.
     */
    private void passwordTimeoutStart() {
        int timeout = PreferenceHandler.getPasswordTimeout(mActivity);
        passwordTimeoutClear();
        mHandler.postDelayed(mInstance, timeout);
        mTimedOut = false;
    }

    /**
     * Check if we have timed out, and if so start login activity. Otherwise
     * clear and halt state.
     *
     * @param activity  Current active activity
     * @return          true if we are timed out, else false
     */
    public boolean resume(Activity activity) {
        mActivity = activity;
        if (mTimedOut) {
            Intent intent = new Intent(mActivity, PasswordActivity.class);
            mActivity.startActivity(intent);
            mActivity.finish();
        } else {
            passwordTimeoutClear();
        }
        return mTimedOut;
    }

    /**
     * Expire the state immediately ie. set state to timed out.
     * Usage example: logout button
     *
     * @param activity  Current active activity
     */
    public void expire(Activity activity) {
        mActivity = activity;
        passwordTimeoutClear();
        run();
        resume(activity);
    }

    /**
     * Schedule a new timeout callback. Called from activity onPause
     * method
     *
     * @param activity  Current active activity
     */
    public void pause(Activity activity) {
        mActivity = activity;
        passwordTimeoutStart();
    }
}