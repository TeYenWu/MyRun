package com.teyenwu.myrun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.teyenwu.myrun.model.ExerciseEntry;
import com.teyenwu.myrun.model.ExerciseEntryDbHelper;

public class DetailActivity extends AppCompatActivity {

    ExerciseEntryDbHelper dpHelper;
    ExerciseEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dpHelper = new ExerciseEntryDbHelper(this);

        long id = getIntent().getLongExtra("id", -1);
        if(id != -1){
            entry = dpHelper.fetchEntryByIndex(id);
        }

        if(entry != null){
            EditText view = findViewById(R.id.activityTypeEditText);
            String[] activityTypeStringArray = getResources().getStringArray(R.array.activity_type_array);
            view.setText(activityTypeStringArray[entry.getActivityType()]);

            EditText view2 = findViewById(R.id.dateEditText);
            view2.setText(entry.getDateTimeString());

            EditText view3 = findViewById(R.id.durationEditText);
            view3.setText(entry.getDuration() + "mins " + "0secs");

            EditText view4 = findViewById(R.id.distanceEditText);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String unitPreference = sp.getString("unit_preference","Miles");
            float distance = 0;
            if(unitPreference.equals("Miles"))
                distance = entry.getDistance();
            else
                distance = entry.getDistance() * 1.61f;

            view4.setText(distance + " " + unitPreference);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteMenuItem:
                dpHelper.removeEntry(entry.getId());
                finish();
                break;
            default:
                return false;
        }

        return super.onOptionsItemSelected(item);

    }
}
