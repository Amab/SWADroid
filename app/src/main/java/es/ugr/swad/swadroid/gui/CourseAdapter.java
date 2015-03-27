package es.ugr.swad.swadroid.gui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private ArrayList<String> mDataset;
    private int[] mIcon;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CourseAdapter(ArrayList<String> dataSet, int[] icons) {
        mDataset = dataSet;
        mIcon = icons;
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
        TextView tv = viewHolder.getTextView();
        tv.setText(mDataset.get(position));
        tv.setCompoundDrawablesWithIntrinsicBounds(mIcon[position], 0, 0, 0);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataset.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    notifyItemRangeChanged(getLayoutPosition(), mDataset.size());
                }
            });
            textView = (TextView) v.findViewById(android.R.id.text1);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
