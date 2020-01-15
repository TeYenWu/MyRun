package com.teyenwu.myrun.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.teyenwu.myrun.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartRunFragment.OnStartRunFragmentListener} interface
 * to handle interaction events.
 * Use the {@link StartRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartRunFragment extends Fragment {
    OnStartRunFragmentListener mListener;
    Spinner inputTypeSpinner;
    Spinner activityTypeSpinner;

    static final int INPUT_TYPE_MANUAL_ENTRY_INDEX = 0;
    static final int INPUT_TYPE_GPS_INDEX = 1;
    static final int INPUT_TYPE_AUTOMATIC_INDEX = 0;
    public StartRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StartRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartRunFragment newInstance() {
        StartRunFragment fragment = new StartRunFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_run, container, false);
        this.inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner);
        this.activityTypeSpinner = view.findViewById(R.id.activityTypeSpinner);
        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartActivity();
            }
        });

        Button syncButton = view.findViewById(R.id.syncButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSyncActivity();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStartRunFragmentListener) {
            mListener = (OnStartRunFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onStartActivity(){
        if(this.inputTypeSpinner.getSelectedItemPosition() == INPUT_TYPE_MANUAL_ENTRY_INDEX){
            mListener.onStartActivityManually();
        } else if(this.inputTypeSpinner.getSelectedItemPosition() == INPUT_TYPE_GPS_INDEX){
            mListener.onStartActivityGPS();
        } else if(this.inputTypeSpinner.getSelectedItemPosition() == INPUT_TYPE_AUTOMATIC_INDEX){
            mListener.onStartActivityAutomatically();
        }
    }

    public void onSyncActivity(){

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStartRunFragmentListener {
        // TODO: Update argument type and name
        void onStartActivityManually();
        void onStartActivityGPS();
        void onStartActivityAutomatically();
    }
}
