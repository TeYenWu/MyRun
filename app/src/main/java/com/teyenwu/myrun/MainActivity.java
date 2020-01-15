package com.teyenwu.myrun;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.teyenwu.myrun.ui.main.SectionsPagerAdapter;
import com.teyenwu.myrun.ui.main.StartRunFragment;

public class MainActivity extends AppCompatActivity implements StartRunFragment.OnStartRunFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onStartActivityManually() {
        Intent intent = new Intent(this, ManualInputActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStartActivityGPS() {
    }

    @Override
    public void onStartActivityAutomatically() {
    }
}