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
import android.os.Handler;
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
    private TextView heart_coinCount_text, mCash, mTapDescription;


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

        mCash = (TextView) rootView.findViewById(R.id.home_cash_tv);
        exchangeCash(0);


        //최초 설치시에만 보이도록 수정
        final Animation alphaZero = AnimationUtils.loadAnimation(getContext(), R.anim.alpah_zero);
        alphaZero.setRepeatMode(1);
        alphaZero.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTapDescription.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTapDescription.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mTapDescription = (TextView) rootView.findViewById(R.id.home_decription_tv);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTapDescription.startAnimation(alphaZero);
            }
        }, 10000);



        return rootView;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.e("ios", "처음에 탄다 : " + event.values[0]);

        mOldCount = (int) event.values[0];

        Log.e("step", "mOldCount : " + mOldCount);

        //자정 이후 리셋되면 reset = false
        if (!mSp.getBoolean("reset", false)) {
            mSp.edit().putInt("appOsCount", mOldCount).apply();
            mSp.edit().putBoolean("reset", true).apply(); //자정 지나서 서버에 걸음수가 등록되면 reset false 부분 넣어야됨.
        }

        int oldCount = mSp.getInt("appOsCount", 0);

        //step counter 센서는 핸드폰을 껐다가 키면 0가 된다. 폰에 저장된 appOsCount 보다 센서 카운트가 작으면 껐다가 다시 킨걸로 간주하고 재설정 해줌
        if (mOldCount > oldCount) {
            mCurrentCount = mOldCount - oldCount;
        } else {
            mSp.edit().putInt("appOsCount", mOldCount).apply();
            int newOldCount = mSp.getInt("appOsCount", 0);
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



        /*
         * 전환가능한 걸음수만 계산 및 하트 카운트 업데이트
         *
         * convertedCount : 전환한 걸음수
         *
         * convertabelCount = mCurrentCount - convertedCount : 전환 가능한 걸음수
         *
         * convertingCoin = convertableCount / 5 : 코인으로 전환
         *
         * convertedCount = convertedCount + (convertableCount - convertableCount%5) : 전환된 걸음수 업데이트
         *
         * heartCoinCount = heartCoinCount + convertingCoin : 하트 카운트 업데이트
         *
         * mCurrentCount < convertedCount || mCurrent == 0 : 핸드폰을 리부팅해서 현재 카운트가 0 이니까 convertedCount = 0 으로 업데이트
         *
         *
         * */


        int convertedCount = mSp.getInt("convertedCount", 0);

        if (mCurrentCount < convertedCount || mCurrentCount == 0)
            mSp.edit().putInt("convertedCount", 0).apply();

        int convertableCount = mCurrentCount - convertedCount;
        int convertingCoin = (int) Math.floor(convertableCount / 5); // n.0 으로 나와서 캐스팅


        Log.e("step", "\n mCurrentCount : " + mCurrentCount + "\n convertedCount : " + convertedCount + "\n convertableCount : " + convertableCount + "\n convertingCoin : " + convertingCoin);


        convertedCount += (convertableCount - convertableCount % 5);
        mSp.edit().putInt("convertedCount", convertedCount).apply();

        int heartCoin = mSp.getInt("heartCoinCount", 0);

        Log.e("step", "\n 1 - heartCoin : " + heartCoin);

        heartCoin += convertingCoin;

        Log.e("step", "\n 2 - heartCoin : " + heartCoin);

        mSp.edit().putInt("heartCoinCount", heartCoin).apply();


        changeStatus_heart(heartCoin);

//        Log.e("ios", "2900/10000 = " + 2900.f / 10000.f + "    , walkCountBias/10000.f = " + walkCountBias / 10000.f + "    , walkCountBias = " + walkCountBias);

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

        heart_coinCount_text = (TextView) view.findViewById(R.id.home_heart_tv);

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
                    //카운터에서 코인으로

                    int heartCoin = mSp.getInt("heartCoinCount", 0);
                    if (heartCoin > 0) {
                        exchangeCoin(1);

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
                    }
                } else {
                    Log.e("ios", "광고");
                    //광고가 뜨면 코인으로 전환 : 광고 fail시엔 전환 안됨
                }
            }
        });

    }

    private void exchangeCoin(int toCoin) {
        int heartCoin = mSp.getInt("heartCoinCount", 0);
        heartCoin -= toCoin;
        mSp.edit().putInt("heartCoinCount", heartCoin).apply();
        changeStatus_heart(heartCoin);


        //캐시 올리는 메서드 작성
        //광고가 뜨면 코인으로 전환 : 광고 fail시엔 전환 안됨
    }

    public void exchangeCash(int coin) {
        int nowCash = mSp.getInt("myCash", 0);
        nowCash += coin;
        mCash.setText(String.valueOf(nowCash));
        mSp.edit().putInt("myCash", nowCash).apply();
    }


    public void changeStatus_heart(int heartCount) {

        heart_coinCount_text.setText(String.valueOf(heartCount));

        if (heartCount < 5) {
            heart_imageView.setImageResource(R.drawable.fragment_home_heart_gray);
            mRippleBackground.stopRippleAnimation();
            mTapDescription.setVisibility(View.INVISIBLE);

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
