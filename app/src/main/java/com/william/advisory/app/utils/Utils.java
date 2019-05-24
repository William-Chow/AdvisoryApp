package com.william.advisory.app.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;

import com.william.advisory.mvp.model.entity.UserProfile;

/**
 * Created by William Chow on 2019-05-23.
 */
public class Utils {

    public static final String SP_KEY_USER_DATA = "SP_KEY_USER_DATA";

    /**
     * 字符串不为 null 而且不为 "" 时返回 true
     */
    static boolean notBlank(String str) {
        return str != null && !"".equals(str.trim());
    }

    /**
     * @param _activity _activity
     *                  Hide Keyboard
     */
    public static void hideKeyboard(Activity _activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) _activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(_activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        }
    }

    /**
     * @param _currentActivity _currentActivity
     * @param _moveActivity    _moveActivity
     */
    public static void intentAct(Activity _currentActivity, Class _moveActivity) {
        _currentActivity.startActivity(new Intent(_currentActivity, _moveActivity));
        // _currentActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        _currentActivity.finish();
    }

    /**
     * Get UserProfile
     *
     * @param mApplication mApplication
     * @return UserProfile
     */
    public static UserProfile getUserProfile(Application mApplication) {
        return PrefUtils.getInstance(mApplication).getUserProfile();
    }
}
