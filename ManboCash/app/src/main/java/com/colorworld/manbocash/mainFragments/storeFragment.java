package com.colorworld.manbocash.mainFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colorworld.manbocash.R;

public class storeFragment extends Fragment {

    public WebView mStoreWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_store, container, false);

        mStoreWebView = (WebView) rootView.findViewById(R.id.store_webview);

        mStoreWebView.loadUrl("https://color-world-9fef9.firebaseapp.com");



        mStoreWebView.clearCache(true);

        mStoreWebView.getSettings().setJavaScriptEnabled(true);
        mStoreWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mStoreWebView.getSettings().setGeolocationEnabled(true);
        mStoreWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mStoreWebView.getSettings().setSupportZoom(true);
        mStoreWebView.getSettings().setBuiltInZoomControls(true);
        mStoreWebView.getSettings().setUseWideViewPort(true);
        mStoreWebView.getSettings().setLoadWithOverviewMode(true);
        mStoreWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mStoreWebView.getSettings().setDomStorageEnabled(true);

        return rootView;
    }
}
