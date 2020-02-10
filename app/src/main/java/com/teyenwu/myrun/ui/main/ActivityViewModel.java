package com.teyenwu.myrun.ui.main;

import com.teyenwu.myrun.model.ExerciseEntry;

import java.util.ArrayList;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ExerciseEntry>> exerciseEntryMutableLiveData = new MutableLiveData<>();

    public LiveData<ArrayList<ExerciseEntry>> getExerciseEntries() {
        return exerciseEntryMutableLiveData;
    }

    public void setExerciseEntries(ArrayList entries){
        exerciseEntryMutableLiveData.setValue(entries);
    }

    public void insertExerciseEntry(ExerciseEntry entry){
        ArrayList<ExerciseEntry> list = exerciseEntryMutableLiveData.getValue();
        list.add(entry);
        exerciseEntryMutableLiveData.setValue(list);
    }

    private final MutableLiveData<ExerciseEntry> selected = new MutableLiveData<ExerciseEntry>();

    public void select(ExerciseEntry item) {
        selected.setValue(item);
    }

    public LiveData<ExerciseEntry> getSelected() {
        return selected;
    }


}