package com.twitterlogin.android.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.twitterlogin.android.ui.base.BaseActivity;
import com.twitterlogin.android.ui.fragments.LoginFragment;

public class MainActivity extends BaseActivity {

    @NonNull
    @Override
    protected Fragment getRootFragment() {
        return LoginFragment.newInstance();
    }

}
