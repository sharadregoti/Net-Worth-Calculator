package com.sharad.myapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.sharad.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView backgroundImage = findViewById(R.id.SplashScreenImage);
        Animation slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide);
        backgroundImage.startAnimation(slideAnimation);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (currentUser == null) {
                Intent intent = new Intent(this, ActivitySignIn.class);
                startActivity(intent);
                finish();
            } else if (!isOnBoardingFinished()) {
                Intent intent = new Intent(this, ActivityOnBoarding.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, ActivityHome.class);
                startActivity(intent);
                finish();
            }

        }, 3000);
    }

    private boolean isOnBoardingFinished() {
        SharedPreferences c = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return c.getBoolean("is_on_boarding_finished", false);
    }
}