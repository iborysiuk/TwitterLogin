package com.twitterlogin.android.annotations;

import com.twitterlogin.android.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Yuriy Borysiuk on 8/25/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ToolbarConfig {

    int title() default R.string.empty_title;

    boolean backArrow() default false;

    Theme theme() default Theme.MAIN;

    enum Theme {
        MAIN(0), SECONDARY(1);

        private final int themeCode;

        Theme(int code) {
            this.themeCode = code;
        }

        public int getThemeCode() {
            return this.themeCode;
        }
    }
}
