package com.colorworld.manbocash.requestPermission;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colorworld.manbocash.R;

public class MyBodysizeActivity extends AppCompatActivity {

    private Button body_confirm_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_body_size);

        body_confirm_btn = (Button) findViewById(R.id.body_confirm_btn);
        body_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MyBodysizeActivity.this, RecommendActivity.class));
                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        });


    }
}
