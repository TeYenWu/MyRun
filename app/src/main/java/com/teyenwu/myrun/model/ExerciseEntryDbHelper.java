package com.teyenwu.myrun.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class ExerciseEntryDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMMENTS = "Exercise";//comments is the name of the database table we create for this example
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUTTYPE = "input_type";
    public static final String COLUMN_ACTIVITYTYPE = "activity_type";
    public static final String COLUMN_DATETIME = "date_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_AVGPACE = "avg_pace";
    public static final String COLUMN_AVGSPEED = "avg_speed";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_HEARTRATE = "heartrate";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_PRIVACY = "privacy";
    public static final String COLUMN_GPSDATA = "gps_data";
    private static final String DATABASE_NAME = "exercises.db";

    private String[] allColumns = { COLUMN_ID,COLUMN_INPUTTYPE, COLUMN_ACTIVITYTYPE, COLUMN_DATETIME, COLUMN_DURATION, COLUMN_DISTANCE, COLUMN_AVGPACE, COLUMN_AVGSPEED, COLUMN_CALORIES, COLUMN_CLIMB, COLUMN_HEARTRATE, COLUMN_COMMENT, COLUMN_GPSDATA };

    private static final int DATABASE_VERSION = 1;

    //XD: SQL command - create table table_name(col1, col1 property, col2, col2 property);
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_COMMENTS + "(" + COLUMN_ID  + " integer primary key autoincrement, "
            + COLUMN_INPUTTYPE + " integer not null, "
            + COLUMN_ACTIVITYTYPE + " integer not null, "
            + COLUMN_DATETIME + " datetime not null, "
            + COLUMN_DURATION + " integer not null, "
            + COLUMN_DISTANCE + " real, "
            + COLUMN_AVGPACE + " real, "
            + COLUMN_AVGSPEED + " real, "
            + COLUMN_CALORIES + " integer, "
            + COLUMN_CLIMB + " real, "
            + COLUMN_HEARTRATE + " integer, "
            + COLUMN_COMMENT  + " text,"
            + COLUMN_GPSDATA + " blob );";

    public ExerciseEntryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

    // Insert a item given each column value
    public long insertEntry(ExerciseEntry entry) {

        ContentValues values = new ContentValues();//a row of data, here we have only one string item
        values.put(COLUMN_INPUTTYPE, entry.getInputType());
        values.put(COLUMN_ACTIVITYTYPE, entry.getActivityType());
        values.put(COLUMN_DATETIME, entry.getDateTimeString());
        values.put(COLUMN_DURATION, entry.getDuration());
        values.put(COLUMN_DISTANCE, entry.getDistance());
        values.put(COLUMN_AVGPACE, entry.getAvgPace());
        values.put(COLUMN_AVGSPEED, entry.getAvgSpeed());
        values.put(COLUMN_CALORIES, entry.getCalorie());
        values.put(COLUMN_CLIMB, entry.getClimb());
        values.put(COLUMN_HEARTRATE, entry.getHeartRate());
        values.put(COLUMN_COMMENT, entry.getComment());
//        values.put(COLUMN_PRIVACY, entry.get());
        values.put(COLUMN_GPSDATA, entry.getLocationListAsString().getBytes());
        SQLiteDatabase database = getWritableDatabase();
        long insertId = database.insert(ExerciseEntryDbHelper.TABLE_COMMENTS, null,	values);

        return insertId;
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_COMMENTS, COLUMN_ID	+ " = " + rowIndex, null);
    }

    // Query a specific entry by its index.
    public ExerciseEntry fetchEntryByIndex(long rowId) {
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.query(TABLE_COMMENTS,
                allColumns,
                COLUMN_ID + " = " + rowId,
                null,null, null, null);

        cursor.moveToFirst();//now the cursor has only one element but the index is -1, so we need to do cursor.moveToFirst()
        ExerciseEntry entry = cursorToExerciseEntry(cursor);
        cursor.close();
        return entry;
    }

    // Query the entire table, return all rows
    public ArrayList<ExerciseEntry> fetchEntries() {
        ArrayList<ExerciseEntry> entries = new ArrayList<ExerciseEntry>();
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.query(TABLE_COMMENTS,
                allColumns, null, null, null, null, null);
        //Log.d("XD", cursor.getCount() + " " + cursor.getPosition());

        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {//Returns whether the cursor is pointing to the position after the last row.
            ExerciseEntry entry = cursorToExerciseEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return entries;
    }

    private ExerciseEntry cursorToExerciseEntry(Cursor cursor) {
        ExerciseEntry entry = new ExerciseEntry();
        entry.setId(cursor.getLong(0));//Returns the value of the requested column as a long.
        entry.setInputType(cursor.getInt(1));
        entry.setActivityType(cursor.getInt(2));
        entry.setDateTimeFromString(cursor.getString(3));
        entry.setDuration(cursor.getInt(4));
        entry.setDistance(cursor.getFloat(5));
        entry.setAvgPace(cursor.getFloat(6));
        entry.setAvgSpeed(cursor.getFloat(7));
        entry.setCalorie(cursor.getInt(8));
        entry.setClimb(cursor.getFloat(9));
        entry.setHeartRate(cursor.getInt(10));
        entry.setComment(cursor.getString(11));
        if (cursor.getBlob(12) != null)
            entry.setLocationListFromString(new String(cursor.getBlob(12)));
//        entry.setId(cursor.getLong(9));


        entry.setComment(cursor.getString(1));//Returns the value of the requested column as a string.
        return entry;
    }
}
