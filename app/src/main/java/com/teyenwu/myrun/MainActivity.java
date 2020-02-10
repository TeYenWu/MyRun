package com.teyenwu.myrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.teyenwu.myrun.model.ExerciseEntry;
import com.teyenwu.myrun.model.ExerciseEntryDbHelper;
import com.teyenwu.myrun.ui.main.ActivityViewModel;
import com.teyenwu.myrun.ui.main.SectionsPagerAdapter;
import com.teyenwu.myrun.ui.main.StartRunFragment;

public class MainActivity extends AppCompatActivity implements StartRunFragment.OnStartRunFragmentListener {
    ExerciseEntry entryOnStart = null;
    final static int REQUEST_FOR_MANUAL_ACTIVITY = 1;
    final static int REQUEST_FOR_MAP_ACTIVITY = 2;
    final static int REQUEST_FOR_DETAIL_ACTIVITY = 3;
    ActivityViewModel viewModel;
    ExerciseEntryDbHelper dpHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        dpHelper = new ExerciseEntryDbHelper(this);
        viewModel = ViewModelProviders.of(this).get(ActivityViewModel.class);
        viewModel.getSelected().observe(this, new Observer<ExerciseEntry>() {
            @Override
            public void onChanged(ExerciseEntry entry) {
                onStartDetailActivity(entry);
            }
        });
        viewModel.setExerciseEntries(dpHelper.fetchEntries());
    }


    public void onStartDetailActivity(ExerciseEntry entry) {
        if(entry.getInputType() == ExerciseEntry.InputType.TYPE_GPS.ordinal()){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("id", entry.getId());
            startActivityForResult(intent, REQUEST_FOR_MAP_ACTIVITY);
        } else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", entry.getId());
            startActivityForResult(intent, REQUEST_FOR_DETAIL_ACTIVITY);
        }

    }

    @Override
    public void onStartActivityManually(ExerciseEntry entry) {
        Intent intent = new Intent(this, ManualInputActivity.class);
        intent.putExtra("activityType", entry.getActivityType());
        startActivityForResult(intent, REQUEST_FOR_MANUAL_ACTIVITY);
    }

    @Override
    public void onStartActivityGPS(ExerciseEntry entry) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("inputType", entry.getInputType());
        intent.putExtra("activityType", entry.getActivityType());
        startActivityForResult(intent, REQUEST_FOR_MAP_ACTIVITY);
    }

    @Override
    public void onStartActivityAutomatically(ExerciseEntry entry) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("activityType", entry.getActivityType());
        startActivityForResult(intent, REQUEST_FOR_MAP_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_MANUAL_ACTIVITY && resultCode == RESULT_OK) {
            long id = data.getLongExtra("id", -1);


        } else if (requestCode == REQUEST_FOR_MAP_ACTIVITY && resultCode == RESULT_OK) {
            long id = data.getLongExtra("id", -1);

        } else if (requestCode == REQUEST_FOR_DETAIL_ACTIVITY && resultCode == RESULT_OK) {
            long id = data.getLongExtra("id", -1);
        }

        viewModel.setExerciseEntries(dpHelper.fetchEntries());
    }

}