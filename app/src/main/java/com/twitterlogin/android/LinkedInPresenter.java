package com.twitterlogin.android;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitterlogin.android.annotations.WebSetting;

import static com.twitterlogin.android.WebDialog.WebDialogCallback;

/**
 * Created by Yuriy Borysiuk on 8/26/2016.
 */

public final class LinkedInPresenter {

    private static final String TAG = "LinkedInPresenter";

    @WebSetting(url = "", urlCallback = "")
    private WebDialog mWebDialog;
    private Context mContext;

    public void authorization() {
        mWebDialog = new WebDialog(mContext, new WebDialogCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
        mWebDialog.show();
    }

    private void appAuthorization() {
        LISessionManager.getInstance(mContext).init((Activity) mContext, Scope.build(Scope.R_BASICPROFILE),
                new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                   //     getUserProfile();
                    }

                    @Override
                    public void onAuthError(LIAuthError error) {
                  //      LOGE(this, error.toString(), null);
                    }
                }, true);
    }

}
