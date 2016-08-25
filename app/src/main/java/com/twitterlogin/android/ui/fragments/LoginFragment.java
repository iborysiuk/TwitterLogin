package com.twitterlogin.android.ui.fragments;

import android.os.Bundle;

import com.twitterlogin.android.annotations.FragmentView;
import com.twitterlogin.android.R;
import com.twitterlogin.android.annotations.ToolbarConfig;
import com.twitterlogin.android.ui.base.BaseFragment;
import com.twitterlogin.android.util.Navigator;

import butterknife.OnClick;

/**
 * Created by Yuriy Borysiuk on 8/24/2016.
 */
@FragmentView(layout = R.layout.fragment_login)
@ToolbarConfig(title = R.string.title_login)
public class LoginFragment extends BaseFragment {

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.register_btn)
    public void actionRegister() {
        Navigator.get().nextFragment(RegisterFragment.newInstance());
    }

}
