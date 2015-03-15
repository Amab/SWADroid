package es.ugr.swad.swadroid.gui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.ugr.swad.swadroid.R;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private String[] mDataset;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CourseAdapter(String[] dataSet) {
        mDataset = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_dropdown_item_1line, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CourseAdapter.ViewHolder viewHolder, int position) {
        Log.d("CUSTOM", "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(mDataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CUSTOMADAPTER", "Element " + getPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.crs, 0, 0, 0);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
