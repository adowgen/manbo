package com.colorworld.manbocash;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import com.colorworld.manbocash.tutorial.tutorials.TutorialSupportActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        mStaticContext = this;


        MobileAds.initialize(this, "ca-app-pub-6561710413911304~8412658551");

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });


        initAdvertising();


        permissionAccess();

        getWeatherStatus();


//        *테스트용으로 프로덕트일때 풀어야됨
        //intro_login
//        Intent isLogin = new Intent(mContext, Tutorial_LoginActivity.class);
//        startActivity(isLogin);

        SharedPreferences sp = getSharedPreferences("isFirstRun", MODE_PRIVATE);
        Log.e("ios", "isFirst running : " + sp.getBoolean("running", false));
        if (!sp.getBoolean("running", false)) {
            TutorialSupportActivity.start(this);
        }


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
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
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

        mRectangleAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
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


        mAd_bg = (FrameLayout) findViewById(R.id.ad_bg);
        mAd_bg_x_btn = (ImageView) findViewById(R.id.ad_bg_x);
        mAd_bg_x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAd_bg.setVisibility(View.INVISIBLE);
                bottomNavigationView.setAlpha(1.0f);
                enableBottomNavi(true);
                ((homeFragment) homeFragment.mStaticHFContext).enableBtns(true);
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


    public void permissionAccess() {
        ActivityCompat.requestPermissions(this, new String[]{
//                Manifest.permission.CAMERA, //사진
//                Manifest.permission.CALL_PHONE,  //통화
//                Manifest.permission.READ_PHONE_STATE,
//				Manifest.permission.WRITE_CALL_LOG,
//				Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.ACCESS_FINE_LOCATION, //위치
                Manifest.permission.ACCESS_COARSE_LOCATION
//                Manifest.permission.GET_ACCOUNTS, //주소록
//                Manifest.permission.WRITE_CONTACTS,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.READ_EXTERNAL_STORAGE, //사진 미디어
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO, //오디오 녹음
//				Manifest.permission.SEND_SMS, //문자
//				Manifest.permission.RECEIVE_SMS,
//				Manifest.permission.READ_SMS,
//				Manifest.permission.RECEIVE_MMS
        }, 11111);

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


}
