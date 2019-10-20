package com.colorworld.manbocash;

import android.app.Application;
import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.colorworld.manbocash.tutorial.StepWorker;
import com.google.firebase.FirebaseApp;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

import java.util.concurrent.TimeUnit;

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
        /* -- workManager -- */
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        WorkManager mWorkmanager = WorkManager.getInstance(getApplicationContext());
        PeriodicWorkRequest uploadStepRequest = new PeriodicWorkRequest.Builder(StepWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
        mWorkmanager.enqueueUniquePeriodicWork("uploadStep", ExistingPeriodicWorkPolicy.KEEP, uploadStepRequest);
        /* ------------------ */

    }
}
