package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable goNext = () -> {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(goNext, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(goNext);
    }
}