package com.twitterlogin.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

/**
 * Created by Yuriy Borysiuk on 8/26/2016.
 */

public class WebDialog extends Dialog {

    private static final int DEFAULT_THEME = R.style.AppTheme_ProgressDialog;
    private static final String TAG = "WebDialog";
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final double MIN_SCALE_FACTOR = 0.5;
    private ProgressDialog mProgressDialog;
    private FrameLayout mContent;
    private WebView mWebView;
    private String mUrl;
    private String mUrlCallback;
    private WebDialogCallback mWebCallback;

    public WebDialog(Context context, WebDialogCallback callback) {
        super(context);
        this.mWebCallback = callback;
    }

    public void setWebUrl(@NonNull String url, @Nullable String urlCallback) {
        this.mUrl = url;
        this.mUrlCallback = urlCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(getContext(), DEFAULT_THEME);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(dialog -> {
            Log.e(TAG, "Request have been canceled");
            if (mWebView != null) mWebView.stopLoading();
            WebDialog.this.dismiss();
        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new FrameLayout(getContext());
        mContent.setBackgroundColor(Color.TRANSPARENT);
        calculateSize();
        initWebView();
        addContentView(mContent, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(mContent);
    }

    private void calculateSize() {
        if (getWindow() == null) return;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        int dialogWidth = Math.min(
                getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, MAX_PADDING_SCREEN_WIDTH),
                metrics.widthPixels);

        int dialogHeight = Math.min(
                getScaledSize(height, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT),
                metrics.heightPixels);

        getWindow().setLayout(dialogWidth, dialogHeight);
        getWindow().setGravity(Gravity.CENTER);
    }

    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        int scaledSize = (int) ((float) screenSize / density);
        double scaleFactor;
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            scaleFactor = MIN_SCALE_FACTOR +
                    ((double) (maxPaddingSize - scaledSize))
                            / ((double) (maxPaddingSize - noPaddingSize))
                            * (1.0 - MIN_SCALE_FACTOR);
        }
        return (int) (screenSize * scaleFactor);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView = new WebView(getContext());
        mWebView.setWebViewClient(new OAuthWebViewClient());
        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setVisibility(View.INVISIBLE);

        WebSettings webSettings = mWebView.getSettings();
        //noinspection deprecation
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            webSettings.setOffscreenPreRaster(true);
        mContent.addView(mWebView);
    }

    @Override
    public void onDetachedFromWindow() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        super.onDetachedFromWindow();
    }

    interface WebDialogCallback {
        void onSuccess(String authCode);

        void onError(String error);
    }

    private class OAuthWebViewClient extends WebViewClient {

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return handleUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!mProgressDialog.isShowing()) mProgressDialog.show();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mWebCallback.onError(error.toString());
            WebDialog.this.dismiss();
        }

        private boolean handleUrl(@Nullable String url) {
            if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            if (TextUtils.isEmpty(url)) return false;
            if (!TextUtils.isEmpty(mUrlCallback) && !url.startsWith(mUrlCallback))
                return false;

            if (url.contains("code")) {
                String temp[] = url.split("code=");
                if (temp[1].contains("&state")) {
                    temp[1] = temp[1].substring(0, temp[1].indexOf("&state"));
                    Log.d(TAG, temp[1]);
                }
                mWebCallback.onSuccess(temp[1]);
            } else if (url.contains("error")) {
                String temp[] = url.split("=");
                Log.e(TAG, temp[temp.length - 1]);
                mWebCallback.onError(temp[temp.length - 1]);
            }

            mWebView.stopLoading();
            WebDialog.this.dismiss();
            return true;
        }
    }
}
