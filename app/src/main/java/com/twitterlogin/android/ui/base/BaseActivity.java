package com.twitterlogin.android.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.twitterlogin.android.R;
import com.twitterlogin.android.util.Navigator;

import butterknife.ButterKnife;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ActionBar mActionBar;

    public BaseActivity() {
        Navigator.init(getSupportFragmentManager(), R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);
        initToolbar();

        //init root fragment
        Navigator.get().initFragment(getRootFragment());
    }

    @NonNull
    protected abstract Fragment getRootFragment();

    private void initToolbar() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar == null) return;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Navigator.remove();
    }

    @Override
    public void onBackPressed() {
        if (!Navigator.get().isEmpty()) Navigator.get().goBack();
        else super.onBackPressed();

    }
}
