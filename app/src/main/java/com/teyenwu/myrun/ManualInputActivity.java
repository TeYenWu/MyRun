package com.teyenwu.myrun;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class ManualInputActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
        ListView listView = findViewById(R.id.manualInfoListView);
        listView.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.manual_info_array,
                android.R.layout.simple_spinner_dropdown_item));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String infoEntry = (String)parent.getItemAtPosition(position);
                switch (infoEntry){
                    case "Date":
                        showDatePickerDialog();
                        break;
                    case "Time":
                        showTimePickerDialog();
                        break;
                    case "Duration":
                        showNumbericDialog("Duration");
                        break;
                    case "Distance":
                        showNumbericDialog("Distance");
                        break;
                    case "Calories":
                        showNumbericDialog("Calories");
                        break;
                    case "Heart rate":
                        showNumbericDialog("Heart Rate");
                        break;
                    case "Comment":
                        showEditableDialog("Comment");
                        break;
                }
            }
        });
    }

    protected void showDatePickerDialog(){
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, ManualInputActivity.this, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    protected void showTimePickerDialog(){
        Calendar newCalendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, ManualInputActivity.this, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    protected void showNumbericDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("OK", ManualInputActivity.this);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    protected void showEditableDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.comment_dialog_hint);
        builder.setView(input);
        builder.setPositiveButton("OK", ManualInputActivity.this);
        builder.setNegativeButton("Cancel", ManualInputActivity.this);

        builder.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == BUTTON_POSITIVE){

        } else{
            dialog.cancel();
        }
    }

    public void onSaveInfo(View view){
        finish();
    }

    public void onCancelInfo(View view){
        finish();
    }
}
