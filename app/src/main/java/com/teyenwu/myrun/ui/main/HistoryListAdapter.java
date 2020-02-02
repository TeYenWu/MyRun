package com.teyenwu.myrun.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teyenwu.myrun.R;
import com.teyenwu.myrun.model.ExerciseEntry;

import java.util.ArrayList;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder>  {
    private ClickListener clickListener;
    Context context;
    ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<>();
    String[] activityTypeStringArray;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ExerciseEntry entry;
        private ClickListener clickListener;
        public TextView titleTextView;
        public TextView detailTextView;
        public HistoryViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            detailTextView = v.findViewById(R.id.detailTextView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null)
                        clickListener.onItemClick(entry);
                }
            });
        }

        public void bind(ExerciseEntry entry, ClickListener listener){
            this.entry= entry;
            this.clickListener = listener;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HistoryListAdapter(ArrayList<ExerciseEntry> myDataset, ClickListener listener) {
        exerciseEntries = myDataset;
        clickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HistoryListAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history_item, parent, false);
        context = parent.getContext();
        activityTypeStringArray = parent.getContext().getResources().getStringArray(R.array.activity_type_array);
        HistoryViewHolder vh = new HistoryViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ExerciseEntry entry = exerciseEntries.get(position);
        String title = activityTypeStringArray[entry.getActivityType()] + ", " + entry.getDateTimeString();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String unitPreference = sp.getString("unit_preference","Miles");

        float distance = 0;
        if(unitPreference.equals("Miles"))
            distance = entry.getDistance();
        else
            distance = entry.getDistance() * 1.61f;

        holder.bind(entry, clickListener);

        String detail = distance + " " + unitPreference + ", " + entry.getDuration() + "mins " + "0secs";
        holder.titleTextView.setText(title);
        holder.detailTextView.setText(detail);

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return exerciseEntries == null ? 0 : exerciseEntries.size() ;
    }

    public void reloadData(ArrayList<ExerciseEntry> myDataset){
        exerciseEntries = myDataset;
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onItemClick(ExerciseEntry entry);
    }
}
