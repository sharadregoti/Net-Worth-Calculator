package com.sharad.myapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sharad.myapp.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

import org.jetbrains.annotations.Nullable;

public class ActivityOnBoarding extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setColorDoneText(ContextCompat.getColor(this, R.color.black));
        setBackArrowColor(ContextCompat.getColor(this, R.color.black));
        setNextArrowColor(ContextCompat.getColor(this, R.color.black));
        setSkipButtonEnabled(false);
        setIndicatorColor(ContextCompat.getColor(this, R.color.black), ContextCompat.getColor(this, R.color.white));
        setSystemBackButtonLocked(true);

        askForPermissions(new String[]{Manifest.permission.READ_SMS}, 1, true);
        addSlide(AppIntroFragment.newInstance("Access to read SMS", "M Money processes your transactional SMS to keep track of your financial activities automatically.\n\nNo Personal SMS are read", 0, 0, ContextCompat.getColor(this, R.color.black), ContextCompat.getColor(this, R.color.black)));
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(ActivityOnBoarding.this, ActivityTransactionProgressBar.class);
        startActivity(intent);
        finish();
    }
}