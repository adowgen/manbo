package com.colorworld.manbocash.mainFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.colorworld.manbocash.MainActivity;
import com.colorworld.manbocash.R;
import com.colorworld.manbocash.rank.RankActivity;
import com.colorworld.manbocash.tutorial.tutorials.TutorialSupportActivity;
import com.skyfishjy.library.RippleBackground;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;

public class homeFragment extends Fragment implements SensorEventListener {

    public static homeFragment mStaticHFContext;

    public ImageView walkBg;

    private ImageView mRankBtn, mWalkMan;
    private ConstraintLayout.LayoutParams walkMan_params;

    private SeekBar mWalkGage;
    private RippleBackground mRippleBackground;
    private boolean isClickEnabel = true;

    //step_counter
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private TextView mCounterNum, mPlusOne_1, mPlusOne_2, mPlusOne_3, mPlusOne_4, mPlusOne_5;
    private int currentPlusOne = 1;

    private SharedPreferences mSp;
    private int mCurrentCount = 0, mOldCount;


    //counter
    private ImageView heart_imageView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mStaticHFContext = this;
        View rootView = inflater.inflate(R.layout.fragment_main_home, container, false);

        mSp = this.getActivity().getSharedPreferences("stepCount", MODE_PRIVATE);

        walkBg = (ImageView) rootView.findViewById(R.id.home_walkbg);
        walkBgSizeCheck();

        mRankBtn = (ImageView) rootView.findViewById(R.id.main_lanking_btn);
        mRankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RankActivity.class);
                startActivity(intent);
            }
        });


        mWalkGage = (SeekBar) rootView.findViewById(R.id.home_gage);
        mWalkGage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mCounterNum = (TextView) rootView.findViewById(R.id.greetingTV2_2);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCountSensor == null) {
            Log.e("ios", "No Step Detect Sensor");
            Toast.makeText(getActivity(), "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }

        mWalkMan = (ImageView) rootView.findViewById(R.id.home_walking_man);
        Glide.with(getActivity()).load(R.drawable.walkingman).into(mWalkMan);

        walkMan_params = (ConstraintLayout.LayoutParams) mWalkMan.getLayoutParams();

        initPlusOneAndAnimation(rootView);


        return rootView;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.e("ios", "처음에 탄다 : " + event.values[0]);

        mOldCount = (int) event.values[0];

        //자정 이후 리셋되면 reset = false
        if (!mSp.getBoolean("reset", false)) {
            mSp.edit().putInt("oldCount", mOldCount).apply();
            mSp.edit().putBoolean("reset", true).apply(); //자정 지나서 서버에 걸음수가 등록되면 reset false 부분 넣어야됨.
        }

        int oldCount = mSp.getInt("oldCount", 0);

        //step counter 센서는 핸드폰을 껐다가 키면 0가 된다. 폰에 저장된 oldCount 보다 센서 카운트가 작으면 껐다가 다시 킨걸로 간주하고 재설정 해줌
        if (mOldCount > oldCount) {
            mCurrentCount = mOldCount - oldCount;
        }else {
            mSp.edit().putInt("oldCount", mOldCount).apply();
            int newOldCount = mSp.getInt("oldCount", 0);
            mCurrentCount = mOldCount - newOldCount;
        }

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(mCurrentCount);

        mCounterNum.setText(formattedStringPrice);

        float walkCountBias = (float) mCurrentCount;

        if (walkCountBias <= 290) {
            mWalkGage.setProgress(290);
            walkMan_params.horizontalBias = 0.f / 10000.f;
            mWalkMan.setLayoutParams(walkMan_params);
        } else {
            mWalkGage.setProgress(mCurrentCount);
            walkMan_params.horizontalBias = walkCountBias / 10000.f;
            mWalkMan.setLayoutParams(walkMan_params);
        }


        //여기서 하면 안됨;;;;
        int heartCount = mCurrentCount/5;
        changeStatus_heart(mCurrentCount);

        Log.e("ios", "2900/10000 = " + 2900.f / 10000.f + "    , walkCountBias/10000.f = " + walkCountBias / 10000.f + "    , walkCountBias = " + walkCountBias);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void initPlusOneAndAnimation(View view) {
        final Animation upAnimation_1 = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation_1.setRepeatMode(1);
        upAnimation_1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne_1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne_1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation upAnimation_2 = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation_2.setRepeatMode(1);
        upAnimation_2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne_2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne_2.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation upAnimation_3 = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation_3.setRepeatMode(1);
        upAnimation_3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne_3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne_3.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation upAnimation_4 = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation_4.setRepeatMode(1);
        upAnimation_4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne_4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne_4.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final Animation upAnimation_5 = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation_5.setRepeatMode(1);
        upAnimation_5.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne_5.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne_5.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mPlusOne_1 = (TextView) view.findViewById(R.id.home_plusone_1);
        mPlusOne_1.setVisibility(View.INVISIBLE);

        mPlusOne_2 = (TextView) view.findViewById(R.id.home_plusone_2);
        mPlusOne_2.setVisibility(View.INVISIBLE);

        mPlusOne_3 = (TextView) view.findViewById(R.id.home_plusone_3);
        mPlusOne_3.setVisibility(View.INVISIBLE);

        mPlusOne_4 = (TextView) view.findViewById(R.id.home_plusone_4);
        mPlusOne_4.setVisibility(View.INVISIBLE);

        mPlusOne_5 = (TextView) view.findViewById(R.id.home_plusone_5);
        mPlusOne_5.setVisibility(View.INVISIBLE);


        mRippleBackground = (RippleBackground) view.findViewById(R.id.heart_ripple_content);
        heart_imageView = (ImageView) view.findViewById(R.id.centerImage);
        heart_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickEnabel) {
                    if (currentPlusOne == 1) {
                        mPlusOne_1.startAnimation(upAnimation_1);
                        currentPlusOne = 2;
                    } else if (currentPlusOne == 2) {
                        mPlusOne_2.startAnimation(upAnimation_2);
                        currentPlusOne = 3;
                    } else if (currentPlusOne == 3) {
                        mPlusOne_3.startAnimation(upAnimation_3);
                        currentPlusOne = 4;
                    } else if (currentPlusOne == 4) {
                        mPlusOne_4.startAnimation(upAnimation_4);
                        currentPlusOne = 5;
                    } else if (currentPlusOne == 5) {
                        mPlusOne_5.startAnimation(upAnimation_5);
                        currentPlusOne = 1;
                        ((MainActivity) MainActivity.mStaticContext).openadverstising();
                        enableBtns(false);
                    }
                } else {
                    Log.e("ios", "광고");
                }
            }
        });


        //현재 걸음 계산
        mCurrentCount = mSp.getInt("currentCount", 0);


        changeStatus_heart(mCurrentCount);


    }

    public void changeStatus_heart(int heartCount) {
        if (heartCount < 5) {
            heart_imageView.setImageResource(R.drawable.fragment_home_heart_gray);
            mRippleBackground.stopRippleAnimation();
        } else {
            heart_imageView.setImageResource(R.drawable.fragment_home_heart_red);
            mRippleBackground.startRippleAnimation();
        }
    }

    public void enableBtns(boolean enable) {
        mRankBtn.setEnabled(enable);
        isClickEnabel = enable;
    }

    public void walkBgSizeCheck() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

        float width = (float) dm.widthPixels;
        float height = (float) dm.heightPixels;

        float ratio = height / width;


        Log.e("ios", "w : " + width + " , h : " + height + " , ratio : " + ratio + " , dpi : " + dm.densityDpi);

// && dm.densityDpi > 480
        if (ratio > 1.80) { // s8, s9 +
            Log.e("ios", "ratio 1.8+");
            walkBg.setImageResource(R.drawable.fragment_home_walkbg);
        } else { //s6 edge -
            Log.e("ios", "ratio 1.8-");
            walkBg.setImageResource(R.drawable.fragment_home_walkbg_2560);
        }


    }
}
