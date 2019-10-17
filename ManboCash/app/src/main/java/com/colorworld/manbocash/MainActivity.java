package com.colorworld.manbocash;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.colorworld.manbocash.mainFragments.chartFragment;
import com.colorworld.manbocash.mainFragments.homeFragment;
import com.colorworld.manbocash.mainFragments.settingFragment;
import com.colorworld.manbocash.mainFragments.storeFragment;
import com.colorworld.manbocash.requestPermission.RequestPermission;
import com.colorworld.manbocash.tutorial.StepWorker;
import com.colorworld.manbocash.tutorial.tutorials.TutorialSupportActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {


    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String android_version = "android_version";
    private static final String main_greeting = "main_greeting";

    public static MainActivity mStaticContext;
    public Context mContext;


    //main fragment view
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private homeFragment mHomeFragment = new homeFragment();
    private chartFragment mChartFragment = new chartFragment();
    private storeFragment mStoreFragment = new storeFragment();
    private settingFragment mSettingFragment = new settingFragment();

    private BottomNavigationView bottomNavigationView;

    /*구글광고*/
    private FrameLayout mAd_bg;
    private ImageView mAd_bg_x_btn;

    private AdView mBottomAdView;
    private AdView mRectangleAdView;

    private boolean isAdSuccess = false;

    //test
    private boolean isView = false;
    FirebaseAuth auth ;
    private WorkManager mWorkmanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mStaticContext = this;
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();


        if (currentUser != null) {
            Log.i("user", "currentUser=> " + currentUser.getDisplayName()+ ", id:" + currentUser.getUid());
            isView = true;
        }
        MobileAds.initialize(this, "ca-app-pub-6561710413911304~8412658551");

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });



        initAdvertising();



        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, mHomeFragment).commitAllowingStateLoss();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = mFragmentManager.beginTransaction();
                        switch (item.getItemId()) {
                            case R.id.action_botMenu_home:
                                Log.e("ios", "action_botMenu_home");
                                transaction.replace(R.id.frameLayout, mHomeFragment).commitAllowingStateLoss();

                                break;
                            case R.id.action_botMenu_chart:
                                Log.e("ios", "action_botMenu_chart");
                                transaction.replace(R.id.frameLayout, mChartFragment).commitAllowingStateLoss();
                                break;
                            case R.id.action_botMenu_stroe:
                                Log.e("ios", "action_botMenu_stroe");
                                transaction.replace(R.id.frameLayout, mStoreFragment).commitAllowingStateLoss();
                                break;
                            case R.id.action_botMenu_set:
                                Log.e("ios", "action_botMenu_set");
                                transaction.replace(R.id.frameLayout, mSettingFragment).commitAllowingStateLoss();
                                break;
                        }
                        return true;
                    }
                });

        initFirebaseRemoteConfig();

        requestPermissionActivity();

        getWeatherStatus();


//        *테스트용으로 프로덕트일때 풀어야됨
        //intro_login
//        Intent isLogin = new Intent(mContext, Tutorial_LoginActivity.class);
//        startActivity(isLogin);


        if (!isView) {
            SharedPreferences sp = getSharedPreferences("isFirstRun", MODE_PRIVATE);
            Log.e("ios", "isFirst running : " + sp.getBoolean("running", false));
            if (!sp.getBoolean("running", false)) {
                TutorialSupportActivity.start(this);
            }

            isView = true;
        }
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // 서버에 UID 저장
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("displayName", currentUser.getDisplayName().toString());
                    user.put("uid", currentUser.getUid());
                    user.put("photoUrl", currentUser.getPhotoUrl().toString());

                    db.collection("users").document(currentUser.getUid().toString())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("db", "user saved");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("db", "user save 실패 :"+ e);
                                }
                            });

                    /* -- workManager -- */
                    Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true)
                            .build();
                    mWorkmanager = WorkManager.getInstance((getApplicationContext()));
                    PeriodicWorkRequest uploadStepRequest = new PeriodicWorkRequest.Builder(StepWorker.class, 15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .build();
                    mWorkmanager.enqueueUniquePeriodicWork("uploadStep", ExistingPeriodicWorkPolicy.REPLACE, uploadStepRequest);
                    /* ------------------ */

                }
            }
        });

    }

    public void initAdvertising() {
        mBottomAdView = findViewById(R.id.bottomAdView);
        mRectangleAdView = findViewById(R.id.rectangleAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mBottomAdView.loadAd(adRequest);
        mRectangleAdView.loadAd(adRequest);
        mBottomAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                isAdSuccess = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                isAdSuccess = false;
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                isAdSuccess = true;
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        //앱 처음 오픈시 onAdLoaded(),
        mRectangleAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
//                Log.e("ios", "==============================================mRectangleAdView : onAdLoaded()");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("ios", "==============================================mRectangleAdView : onAdFailedToLoad() : " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
//                Log.e("ios", "==============================================mRectangleAdView : onAdOpened()");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
//                Log.e("ios", "==============================================mRectangleAdView : onAdLeftApplication()");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
//                Log.e("ios", "==============================================mRectangleAdView : onAdClosed()");
            }
        });


        mAd_bg = (FrameLayout) findViewById(R.id.ad_bg);
        mAd_bg_x_btn = (ImageView) findViewById(R.id.ad_bg_x);
        mAd_bg_x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAd_bg.setVisibility(View.INVISIBLE);
                bottomNavigationView.setAlpha(1.0f);
                enableBottomNavi(true);
                ((homeFragment) homeFragment.mStaticHFContext).enableBtns(true);

                if (isAdSuccess) ((homeFragment) homeFragment.mStaticHFContext).exchangeCash(1);

            }
        });

        mAd_bg.setVisibility(View.INVISIBLE);
    }

    public void getWeatherStatus() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("ios", "PERMISSION_GRANTED");
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String provider = location.getProvider();
        final double longitude = location.getLongitude();
        final double latitude = location.getLatitude();
        double altitude = location.getAltitude();

        Log.e("ios", "위치정보 : " + provider + "\n" +
                "위도 : " + longitude + "\n" +
                "경도 : " + latitude + "\n" +
                "고도  : " + altitude);


        new Thread(new Runnable() {
            @Override
            public void run() {
                // Thread 안에서 바로 UI작업을 한다.
                String myLocationWeatherURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=fcaeb43325261acc32ca270c4c7f9196";

                try {
                    Log.e("ios", "json : " + readJsonFromUrl(myLocationWeatherURL));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public void requestPermissionActivity() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                startActivity(new Intent(MainActivity.this, RequestPermission.class));

                return;
            }
        }

    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public void openadverstising() {

        mAd_bg.setVisibility(View.VISIBLE);
        bottomNavigationView.setAlpha(0.1f);
        enableBottomNavi(false);

    }

    public void enableBottomNavi(boolean enable) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            bottomNavigationView.getMenu().getItem(i).setEnabled(enable);
        }
    }

    public void initFirebaseRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.e("ios", "Config params updated: " + updated);
                            Log.e("ios", "Fetch and activate succeeded");


                        } else {
                             Log.e("ios", "Fetch failed");

                        }
                        displayWelcomeMessage();
                    }
                });
    }


    public void displayWelcomeMessage() {

        String mainGreeting = mFirebaseRemoteConfig.getString("main_greeting");
        String andVersion = mFirebaseRemoteConfig.getString("android_version");

        SharedPreferences dataSp = this.getSharedPreferences("manboData", MODE_PRIVATE);
        dataSp.edit().putString("greeting", mainGreeting).apply();
        dataSp.edit().putString("version", andVersion).apply();


        //생각 좀 해야함
        ((homeFragment) homeFragment.mStaticHFContext).fetchGreetingText(mainGreeting);
//        ((settingFragment) settingFragment.mStaticSFContext).fetchVersionText(andVersion);

    }

    @Override
    protected void onStop() {
        super.onStop();
        /* ---- google signout ---- */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("google", "google Signout complete!!");
            }
        });
        /* ------------------------ */

        /* ---- kakao signout ---- */
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                FirebaseAuth.getInstance().signOut();
                Log.d("google", "kakao Signout complete!!");
            }
        });
        /* ------------------------ */
    }

//    @Override
//    protected void onDestroy() {
//
//
//        /* ---- google signout ---- */
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        auth.signOut();
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.d("google", "google Signout complete!!");
//            }
//        });
//        /* ------------------------ */
//
//        /* ---- kakao signout ---- */
//        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//            @Override
//            public void onCompleteLogout() {
//                FirebaseAuth.getInstance().signOut();
//            }
//        });
//        /* ------------------------ */
//    }
}
