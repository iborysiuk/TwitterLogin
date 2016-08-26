package com.twitterlogin.android.ui.base;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.twitterlogin.android.R;
import com.twitterlogin.android.annotations.FragmentView;
import com.twitterlogin.android.annotations.ToolbarConfig;
import com.twitterlogin.android.util.Navigator;

import java.lang.annotation.Annotation;

import butterknife.ButterKnife;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */
public abstract class BaseFragment extends Fragment {

    public static final int TOOLBAR_MAIN = 0;
    public static final int TOOLBAR_SECONDARY = 1;
    public int DEFAULT_TOOLBAR = TOOLBAR_MAIN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        if (view != null) ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
    }

    @LayoutRes
    private int getLayout() {
        int layout = View.NO_ID;
        if (getClass().getAnnotations().length > 0) {
            for (Annotation annotation : getClass().getAnnotations()) {
                if (annotation instanceof FragmentView) {
                    FragmentView fragmentView = (FragmentView) annotation;
                    layout = fragmentView.layout();
                }
            }
        }
        if (layout == View.NO_ID)
            throw new Resources.NotFoundException("Layout recourse not found");
        return layout;
    }


    private void initToolbar(@NonNull View view) {
        Toolbar toolbar = ButterKnife.findById(view, R.id.toolbar);
        if (toolbar == null) return;
        setupToolbarConfig(toolbar);
    }

    private void setupToolbarConfig(final Toolbar toolbar) {
        if (getClass().getAnnotations().length > 0) {
            for (Annotation annotation : getClass().getAnnotations()) {
                if (annotation instanceof ToolbarConfig) {
                    final ToolbarConfig config = (ToolbarConfig) annotation;
                    DEFAULT_TOOLBAR = config.theme().getThemeCode();
                    toolbar.setTitle(config.title());
                    toolbar.setTitleTextColor(getThemeColor());
                    toolbar.setNavigationIcon(getNavigationIcon(config.hasArrow()));
                    toolbar.setNavigationOnClickListener(v -> {
                        if (config.hasArrow()) ((Activity) getContext()).onBackPressed();
                    });
                    return;
                }
            }
        }
    }

    private Drawable getNavigationIcon(boolean hasArrow) {
        Drawable drawable;
        drawable = ContextCompat.getDrawable(getContext(),
                !hasArrow ? R.drawable.ic_menu : R.drawable.ic_arrow_back);
        drawable.setColorFilter(getThemeColor(), PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    private boolean isThemeChanged() {
        return DEFAULT_TOOLBAR != TOOLBAR_SECONDARY;
    }

    private int getThemeColor() {
        return isThemeChanged() ? Color.BLACK : Color.WHITE;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        final Animation animation = Navigator.get().getFragmentAnimation(getContext(), enter);
        if (animation == null) return super.onCreateAnimation(transit, enter, nextAnim);
        return animation;
    }


}
