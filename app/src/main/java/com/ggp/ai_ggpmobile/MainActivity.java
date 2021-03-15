package com.ggp.ai_ggpmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ggp.ai_ggpmobile.Model.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int splashTimeOut=4000;
    AnimationDrawable animationDrawable;
    ConstraintLayout lySplash;
    ImageView ivGgfLogo;
    TextView tvTitleSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        lySplash = findViewById(R.id.ly_splash);
        ivGgfLogo = findViewById(R.id.iv_ggf_logo);
        tvTitleSplash = findViewById(R.id.tv_title_splash);
        animationDrawable = (AnimationDrawable) lySplash.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        String level = SharedPrefManager.getInstance(this).getUserLogin().getLevel();

        User users = new User(
                SharedPrefManager.getInstance(this).getUserLogin().getkit(),
                SharedPrefManager.getInstance(this).getUserLogin().getPassword(),
                SharedPrefManager.getInstance(this).getUserLogin().getLevel());

        new Handler().postDelayed(() -> {
            if (SharedPrefManager.getInstance(this).login()) {
                switch (level) {
                    case "tk": {
                        Intent intent = new Intent(MainActivity.this, MainTkActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "mandor": {
                        SharedPrefManager.getInstance(MainActivity.this).login();
                        Intent intent = new Intent(MainActivity.this, HomeMandorActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case "kasie": {
                        SharedPrefManager.getInstance(MainActivity.this).login();
                        Intent intent = new Intent(MainActivity.this, HomeKasieActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },splashTimeOut);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        ivGgfLogo.startAnimation(animation);
        tvTitleSplash.startAnimation(animation);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

}