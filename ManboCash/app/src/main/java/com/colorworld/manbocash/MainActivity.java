package com.colorworld.manbocash;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;

import com.colorworld.manbocash.mainFragments.chartFragment;
import com.colorworld.manbocash.mainFragments.homeFragment;
import com.colorworld.manbocash.mainFragments.settingFragment;
import com.colorworld.manbocash.mainFragments.storeFragment;
import com.colorworld.manbocash.tutorial.tutorials.TutorialSupportActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends AppCompatActivity {

    Context mContext;


    //main fragment view
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private homeFragment homeFragment = new homeFragment();
    private chartFragment chartFragment = new chartFragment();
    private storeFragment storeFragment = new storeFragment();
    private settingFragment settingFragment = new settingFragment();

    private BottomNavigationView bottomNavigationView;



    private String test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();


        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        switch(item.getItemId()) {
                            case R.id.action_botMenu_home:
                                Log.e("ios", "action_botMenu_home");
                                transaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();

                                break;
                            case R.id.action_botMenu_chart:
                                Log.e("ios", "action_botMenu_chart");
                                transaction.replace(R.id.frameLayout, chartFragment).commitAllowingStateLoss();
                                break;
                            case R.id.action_botMenu_stroe:
                                Log.e("ios", "action_botMenu_stroe");
                                transaction.replace(R.id.frameLayout, storeFragment).commitAllowingStateLoss();
                                break;
                            case R.id.action_botMenu_set:
                                Log.e("ios", "action_botMenu_set");
                                transaction.replace(R.id.frameLayout, settingFragment).commitAllowingStateLoss();
                                break;
                        }
                        return true;
                    }
                });


        /*
        *테스트용으로 프로덕트일때 풀어야됨
        //intro_login
        Intent isLogin = new Intent(mContext, Tutorial_LoginActivity.class);
        startActivity(isLogin);

        SharedPreferences sp = getSharedPreferences("isFirstRun", MODE_PRIVATE);
        Log.e("ios", "isFirst running : " + sp.getBoolean("running", false));
        if (!sp.getBoolean("running", false)) {

        */
            TutorialSupportActivity.start(this);
//        }




    }





}
