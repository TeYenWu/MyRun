package com.teyenwu.myrun.model;


import com.google.android.gms.maps.model.LatLng;
import com.teyenwu.myrun.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExerciseEntry {
    private Long id;
    private int mInputType;  // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime = Calendar.getInstance();    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private float mDistance;      // Distance traveled. Either in meters or feet.
    private float mAvgPace;       // Average pace
    private float mAvgSpeed;     // Average speed
    private int mCalorie;        // Calories burnt
    private float mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;       // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list

    public ExerciseEntry(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getActivityType() {
        return mActivityType;
    }

    public void setActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public int getInputType() {
        return mInputType;
    }

    public void setInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public String getDateTimeString() {
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
        String date = sdf.format(mDateTime.getTime());
        return date;
    }

    public void setDateTimeFromString(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateString);
            mDateTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Calendar getDateTime() {
        return mDateTime;
    }

    public void setDateTime(Calendar mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float mDistance) {
        this.mDistance = mDistance;
    }

    public float getAvgPace() {
        return mAvgPace;
    }

    public void setAvgPace(float mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public float getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(float mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public int getCalorie() {
        return mCalorie;
    }

    public void setCalorie(int mCalorie) {
        this.mCalorie = mCalorie;
    }

    public float getClimb() {
        return mClimb;
    }

    public void setClimb(float mClimb) {
        this.mClimb = mClimb;
    }

    public int getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
    }

    public ArrayList<LatLng> getLocationList() {
        return mLocationList;
    }

    public void setLocationList(ArrayList<LatLng> mLocationList) {
        this.mLocationList = mLocationList;
    }

}