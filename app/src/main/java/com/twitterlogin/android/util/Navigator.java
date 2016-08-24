package com.twitterlogin.android.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */

public class Navigator {

    private static Navigator mInstance;
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
        setRootFragment(rootFragment, false, false);
    }

    public void initFragment(@NonNull Fragment rootFragment, boolean isAnimated, boolean isSlided) {
        setRootFragment(rootFragment, isAnimated, isSlided);
    }

    public void nextFragment(@NonNull Fragment nextFragment) {
        goTo(nextFragment, false);
    }

    public void nextFragment(@NonNull Fragment nextFragment, boolean isSlided) {
        goTo(nextFragment, isSlided);
    }

    private void setRootFragment(@NonNull Fragment rootFragment, boolean isAnimated, boolean isSlided) {
        if (getSize() > 0) {
            this.clearHistory();
        }
        if (!isAnimated) this.replaceFragmentWithOutAnimation(rootFragment);
        else this.replaceFragmentWithAnimation(rootFragment, isSlided);
    }

    public Fragment getActiveFragment() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = mFragmentManager
                .getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
        return mFragmentManager.findFragmentByTag(tag);
    }

    private void goTo(final Fragment fragment, boolean isLeftIn) {
        mFragmentManager.beginTransaction()
                .addToBackStack(getName(fragment))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .setCustomAnimations(
//                        !isLeftIn ? R.anim.anim_slide_in_left : R.anim.anim_slide_in_right,
//                        !isLeftIn ? R.anim.anim_slide_out_left : R.anim.anim_slide_out_right)
                .replace(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }


    private void replaceFragmentWithOutAnimation(@NonNull Fragment fragment) {
        mFragmentManager.beginTransaction()
                .add(mDefaultContainer, fragment, getName(fragment))
                .commit();
        mFragmentManager.executePendingTransactions();
    }

    private void replaceFragmentWithAnimation(final Fragment fragment, boolean isSlided) {
        mFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                        !isSlided ? R.anim.anim_slide_in_left : R.anim.anim_slide_in_right,
//                        !isSlided ? R.anim.anim_slide_out_left : R.anim.anim_slide_out_right)
                .add(mDefaultContainer, fragment, getName(fragment))
                .commitNow();
        mFragmentManager.executePendingTransactions();
    }

    public void removeFragment(final Fragment fragment) {
        mFragmentManager.beginTransaction()
                //.setCustomAnimations(R.anim.anim_slide_in_right , R.anim.anim_slide_out_right)
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
        mFragmentManager.popBackStackImmediate();
    }

    public void goBack() {
        mFragmentManager.popBackStack();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

}
