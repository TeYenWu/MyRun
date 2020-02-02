package com.teyenwu.myrun.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teyenwu.myrun.R;
import com.teyenwu.myrun.model.ExerciseEntry;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ActivityViewModel activityViewModel;
    RecyclerView recyclerView;
    HistoryListAdapter mAdapter;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewModel = ViewModelProviders.of(this.getActivity()).get(ActivityViewModel.class);
        activityViewModel.getExerciseEntries().observe(this, new Observer<ArrayList<ExerciseEntry>>() {
            @Override
            public void onChanged(ArrayList<ExerciseEntry> exerciseEntries) {
                mAdapter.reloadData(exerciseEntries);
            }
        });

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.historyListView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HistoryListAdapter(activityViewModel.getExerciseEntries().getValue(), new HistoryListAdapter.ClickListener() {
            @Override
            public void onItemClick(ExerciseEntry entry) {
                activityViewModel.select(entry);
            }
        });
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this.getContext()).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this.getContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("unit_preference")){
            mAdapter.notifyDataSetChanged();
        }
    }
}