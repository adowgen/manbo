package com.colorworld.manbocash.requestPermission;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colorworld.manbocash.MainActivity;
import com.colorworld.manbocash.R;

public class RecommendActivity extends AppCompatActivity {


    private Button recommend_confirm_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activyty_recommend);

        recommend_confirm_btn = (Button) findViewById(R.id.recommend_confirm_btn);
        recommend_confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecommendActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


    }
}
