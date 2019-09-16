package com.colorworld.manbocash;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

public class ManboApplication extends Application {
    private static ManboApplication self;
    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        FirebaseApp.initializeApp(this);
        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return self;
                    }
                };
            }
        });

    }
}
