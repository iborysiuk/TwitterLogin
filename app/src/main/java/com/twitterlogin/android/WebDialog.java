package com.twitterlogin.android;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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

import com.twitterlogin.android.annotations.WebSetting;

import java.lang.annotation.Annotation;

/**
 * Created by Yuriy Borysiuk on 8/26/2016.
 */

public class WebDialog extends Dialog {

    public static final int DEFAULT_THEME = android.R.style.Theme_Translucent_NoTitleBar;
    private static final String TAG = "WebDialog";
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final double MIN_SCALE_FACTOR = 0.5;
    private ProgressDialog mProgressDialog;
    private FrameLayout mContent;
    private WebView mWebView;
    private boolean isDetached;
    private String mUrl;
    private String mUrlCallback;
    private WebDialogCallback mWebCallback;

    public WebDialog(Context context, WebDialogCallback callback) {
        super(context);
        this.mWebCallback = callback;
        composeUrlAndCallback();
    }

    private void composeUrlAndCallback() {
        if (getClass().getAnnotations().length > 0) {
            for (Annotation annotation : getClass().getAnnotations()) {
                if (annotation instanceof WebSetting) {
                    WebSetting setting = (WebSetting) annotation;
                    mUrl = setting.url();
                    mUrlCallback = setting.urlCallback();
                }
            }
        }
        if (TextUtils.isEmpty(mUrl) && TextUtils.isEmpty(mUrlCallback))
            throw new IllegalArgumentException("Url and urlCallback is empty");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(dialog -> {
            Log.e(TAG, "Request have been canceled");
            WebDialog.this.dismiss();
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new FrameLayout(getContext());
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
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(null);
        mWebView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setVisibility(View.INVISIBLE);
        WebSettings webSettings = mWebView.getSettings();

        //noinspection deprecation
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        mContent.addView(mWebView);
    }

    @Override
    public void onAttachedToWindow() {
        isDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        isDetached = true;
        super.onDetachedFromWindow();
    }

    interface WebDialogCallback {
        void onSuccess();

        void onError(String error);
    }

    private class OAuthWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isDetached) mProgressDialog.dismiss();
            mContent.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!isDetached) mProgressDialog.show();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mWebCallback.onError(error.toString());
            WebDialog.this.dismiss();
        }


        //        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (url.startsWith(mCallBackUrl)) {
//                if (url.contains("code")) {
//                    String temp[] = url.split("code=");
//                    if (temp[1].contains("&state")) {
//                        temp[1] = temp[1].substring(0, temp[1].indexOf("&state"));
//                    }
//                    mListener.onComplete(temp[1]);
//                } else if (url.contains("error")) {
//                    String temp[] = url.split("=");
//                    mListener.onError(temp[temp.length - 1]);
//                }
//                WebDialog.this.dismiss();
//                return true;
//            }
//            return false;
//        }
//
//        @SuppressWarnings("deprecation")
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            super.onReceivedError(view, errorCode, description, failingUrl);
//            mListener.onError(description);
//            WebDialog.this.dismiss();
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//            if (!isDetached) mProgressDialog.show();
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            if (!isDetached) mProgressDialog.dismiss();
//            mContent.setBackgroundColor(Color.TRANSPARENT);
//            mWebView.setVisibility(View.VISIBLE);
//        }
    }
}
