package com.twitterlogin.android.annotations;

import android.support.annotation.LayoutRes;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface FragmentView {

    @LayoutRes int layout() default View.NO_ID;

}
