package com.teyenwu.myrun;


public class Util {
    static int getCalories(int activityType, float distance, long duration){
        return (int)((float)(activityType+1)*distance/1000*(float)duration/3600);
    }
}
