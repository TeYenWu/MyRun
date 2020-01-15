package com.teyenwu.myrun.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.teyenwu.myrun.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ActivityViewModel activityViewMode;

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewMode = ViewModelProviders.of(this).get(ActivityViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        return root;
    }
}