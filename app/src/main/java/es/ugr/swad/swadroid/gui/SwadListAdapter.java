package es.ugr.swad.swadroid.gui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class SwadListAdapter extends RecyclerView.Adapter<SwadListAdapter.ViewHolder> {

    private int[] mIcon;
    private int[] mText;

    /***
     * Initialize the dataset of the Adapter
     *
     * @param icons Icons ids
     * @param text Text ids
     */
    public SwadListAdapter(int[] icons, int[] text) {
        mIcon = icons;
        mText = text;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SwadListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_dropdown_item_1line, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SwadListAdapter.ViewHolder viewHolder, int position) {
        Log.d("CUSTOM", "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        TextView tv = viewHolder.getTextView();
        tv.setText(mText[position]);
        tv.setCompoundDrawablesWithIntrinsicBounds(mIcon[position], 0, 0, 0);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mText.length;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
//            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mDataset.remove(getLayoutPosition());
//                    notifyItemRemoved(getLayoutPosition());
//                    notifyItemRangeChanged(getLayoutPosition(), mDataset.size());
//                }
//            });
            textView = (TextView) v.findViewById(android.R.id.text1);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
