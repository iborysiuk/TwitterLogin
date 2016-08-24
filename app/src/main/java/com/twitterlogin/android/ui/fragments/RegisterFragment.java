package com.twitterlogin.android.ui.fragments;

import android.os.Bundle;

import com.twitterlogin.android.annotations.FragmentView;
import com.twitterlogin.android.R;
import com.twitterlogin.android.ui.base.BaseFragment;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */
@FragmentView(layout = R.layout.fragment_register)
public class RegisterFragment extends BaseFragment {

    public RegisterFragment() {}

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }
}