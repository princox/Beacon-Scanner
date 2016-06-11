package com.hogervries.beaconscanner.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hogervries.beaconscanner.R;
import com.hogervries.beaconscanner.adapter.TabPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class BeaconActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.pager) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        ButterKnife.bind(this);

        setToolbar();

        setUpTabs();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setUpTabs() {
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), this));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_transition_fade_in, R.anim.anim_transition_exit_left);
    }
}
