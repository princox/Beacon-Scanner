package com.hogervries.beaconscanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.fragment.BeaconFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroActionFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroLogFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroScanFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroSettingsFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroStartFragment;
import com.hogervries.beaconscanner.fragment.intro.IntroTransmitFragment;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(IntroStartFragment.newInstance());
        addSlide(IntroScanFragment.newInstance());
        addSlide(IntroTransmitFragment.newInstance());
        addSlide(IntroLogFragment.newInstance());
        addSlide(IntroSettingsFragment.newInstance());
        addSlide(IntroActionFragment.newInstance());
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        starMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        starMainActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }

    private void starMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
