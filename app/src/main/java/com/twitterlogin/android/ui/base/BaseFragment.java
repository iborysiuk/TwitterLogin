package com.twitterlogin.android.ui.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitterlogin.android.R;
import com.twitterlogin.android.annotations.FragmentView;

import java.lang.annotation.Annotation;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        if (view != null) ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
    }

    @LayoutRes
    private int getLayout() {
        Class aClass = getClass();
        Annotation[] annotations = aClass.getAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof FragmentView) {
                    FragmentView fragmentView = (FragmentView) annotation;
                    return fragmentView.layout();
                }
            }
        } else throw new Resources.NotFoundException("Layout recourse not found");
        return -1;
    }

    private void initToolbar() {
        if (mToolbar == null) return;
        mToolbar.setTitle(R.string.fragment_login_title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
    }
}
