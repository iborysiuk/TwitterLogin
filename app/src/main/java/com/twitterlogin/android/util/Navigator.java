package com.twitterlogin.android.util;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.twitterlogin.android.R;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */

public class Navigator {

    private static Navigator mInstance;
    private boolean isAnimationEnabled;
    private boolean isAnimationReversed;
    private FragmentManager mFragmentManager;
    @IdRes
    private int mDefaultContainer;

    private Navigator() {
    }

    public static void init(@NonNull FragmentManager manager, @IdRes int defaultContainer) {
        if (mInstance == null) {
            mInstance = new Navigator();
            mInstance.config(manager, defaultContainer);
        }
    }

    public static Navigator get() {
        return mInstance;
    }

    public static void remove() {
        if (mInstance != null) mInstance = null;
    }

    private void config(@NonNull FragmentManager manager, @IdRes int defaultContainer) {
        this.mFragmentManager = manager;
        this.mDefaultContainer = defaultContainer;
    }

    public void initFragment(@NonNull Fragment rootFragment) {
        setRootFragment(rootFragment);
    }

    public void initFragment(@NonNull Fragment rootFragment, boolean isAnimated) {
        isAnimationEnabled = isAnimated;
        setRootFragment(rootFragment);
    }

    public void nextFragment(@NonNull Fragment nextFragment) {
        goTo(nextFragment);
    }

    public void nextFragment(@NonNull Fragment nextFragment, boolean isAnimated) {
        isAnimationEnabled = isAnimated;
        goTo(nextFragment);
    }

    private void setRootFragment(@NonNull Fragment rootFragment) {
        if (getSize() > 0) this.clearHistory();
        this.replaceFragment(rootFragment);
    }

    private Fragment getActiveFragment() {
        if (mFragmentManager.getBackStackEntryCount() == 0) return null;
        String tag = mFragmentManager
                .getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
        return mFragmentManager.findFragmentByTag(tag);
    }

    private void goTo(final Fragment fragment) {
        isAnimationReversed = false;
        mFragmentManager.beginTransaction()
                .addToBackStack(getName(fragment))
                .replace(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    private void replaceFragment(@NonNull Fragment fragment) {
        isAnimationReversed = false;
        mFragmentManager.beginTransaction()
                .add(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    public void removeFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                .remove(fragment)
                .commit();
        mFragmentManager.popBackStack();
    }


    private String getName(final Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    private int getSize() {
        return mFragmentManager.getBackStackEntryCount();
    }

    private void clearHistory() {
        //noinspection StatementWithEmptyBody - it works as wanted
        while (mFragmentManager.popBackStackImmediate()) ;
    }

    public void goOneBack() {
        isAnimationReversed = true;
        mFragmentManager.popBackStackImmediate();
    }

    public void goBack() {
        isAnimationReversed = true;
        mFragmentManager.popBackStack();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public Animation getFragmentAnimation(@NonNull Context context, boolean enter) {
        if (!isAnimationEnabled) return null;
        return AnimationUtils.loadAnimation(context, !isAnimationReversed
                ? (enter ? R.anim.slide_left_in : R.anim.slide_left_out)
                : (enter ? R.anim.slide_right_in : R.anim.slide_right_out));
    }
}
