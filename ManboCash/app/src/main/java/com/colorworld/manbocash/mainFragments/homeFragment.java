package com.colorworld.manbocash.mainFragments;

import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.colorworld.manbocash.R;
import com.colorworld.manbocash.rank.RankActivity;
import com.skyfishjy.library.RippleBackground;

import java.text.DecimalFormat;

public class homeFragment extends Fragment  implements SensorEventListener {

    public ImageView walkBg;

    private ImageView rankBtn;

    //step_counter
    private SensorManager sensorManager;
    private Sensor stepCountSensor;
    private TextView mCounterNum, mPlusOne;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_home, container, false);
        walkBg = (ImageView) rootView.findViewById(R.id.home_walkbg);
        walkBgSizeCheck();

        rankBtn = (ImageView) rootView.findViewById(R.id.main_lanking_btn);
        rankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RankActivity.class);
                startActivity(intent);
            }
        });

        mCounterNum = (TextView) rootView.findViewById(R.id.greetingTV2_2);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCountSensor == null) {
            Toast.makeText(getActivity(), "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }

        ImageView walkMan = (ImageView) rootView.findViewById(R.id.home_walking_man);
        Glide.with(getActivity()).load(R.drawable.walkingman).into(walkMan);


        final Animation upAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.ani);
        upAnimation.setRepeatMode(1);
        upAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPlusOne.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPlusOne.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mPlusOne = (TextView) rootView.findViewById(R.id.home_plusone);
        mPlusOne.setVisibility(View.INVISIBLE);

        final RippleBackground rippleBackground = (RippleBackground)rootView.findViewById(R.id.heart_ripple_content);
        rippleBackground.startRippleAnimation();
        ImageView imageView=(ImageView)rootView.findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlusOne.startAnimation(upAnimation);
            }
        });


        return rootView;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(event.values[0]);

        mCounterNum.setText(formattedStringPrice);
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

    public void walkBgSizeCheck() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

        float width = (float)dm.widthPixels;
        float height = (float)dm.heightPixels;

        float ratio = height / width;


        Log.e("ios", "w : " + width + " , h : " + height + " , ratio : " + ratio + " , dpi : " + dm.densityDpi);

// && dm.densityDpi > 480
        if (ratio > 1.80) { // s8, s9 +
            Log.e("ios" , "ratio 1.8+");
            walkBg.setImageResource(R.drawable.fragment_home_walkbg);
        }else { //s6 edge -
            Log.e("ios" , "ratio 1.8-");
            walkBg.setImageResource(R.drawable.fragment_home_walkbg_2560);
        }


    }
}
