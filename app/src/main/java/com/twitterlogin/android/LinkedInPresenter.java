package com.twitterlogin.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.twitterlogin.android.WebDialog.WebDialogCallback;

/**
 * Created by Yuriy Borysiuk on 8/26/2016.
 */

public final class LinkedInPresenter {

    private static final String TAG = "LinkedInPresenter";
    public static final String SOCIAL_LINKEDIN_URL = "https://api.linkedin.com/v1/";
    private static final String SOCIAL_LINKED_IN_API_KEY = "77u9o77qvukf8f";
    public static final String SOCIAL_LINKED_IN_SECRET_KEY = "7LBy61zkf46S2WpY";
    private static final String SOCIAL_LINKED_IN_STATE = "FGHKLAS4846dskjffd124";
    private static final String SOCIAL_LINKED_IN_AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization?";
    public static final String SOCIAL_LINKED_IN_ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SOCIAL_LINKED_IN_CALLBACK_URL = "http://www.ap1.io";
    private static final String authUrl = SOCIAL_LINKED_IN_AUTH_URL + "response_type=code"
            + "&client_id=" + SOCIAL_LINKED_IN_API_KEY
            + "&state=" + SOCIAL_LINKED_IN_STATE
            + "&redirect_uri=" + SOCIAL_LINKED_IN_CALLBACK_URL;

    private Context mContext;

    public void authorization(@NonNull Context context) {
        WebDialog webDialog = new WebDialog(context, new WebDialogCallback() {

            @Override
            public void onSuccess(String authCode) {
                Log.d(TAG, "Success: " + authCode);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
        webDialog.setWebUrl(authUrl, SOCIAL_LINKED_IN_CALLBACK_URL);
        webDialog.show();
    }
}
