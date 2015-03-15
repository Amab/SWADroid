package es.ugr.swad.swadroid;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com) on 15/03/15.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.ugr.swad.swadroid.gui.CourseAdapter;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();

        MaterialSection section1 = newSection(getString(R.string.course), R.drawable.crs,
                new CourseFragment());
        MaterialSection section2 = newSection(getString(R.string.evaluation), R.drawable.ass,
                new CourseFragment());
        MaterialSection section3 = newSection(getString(R.string.users), R.drawable.users,
                new CourseFragment());
        MaterialSection section4 = newSection(getString(R.string.messages), R.drawable.msg,
                new CourseFragment());

        addSection(section1);
        addSection(section2);
        addSection(section3);
        addSection(section4);
    }

    public static class CourseFragment extends Fragment {

        private static final String TAG = "CourseFragment";

        private static final int DATASET_COUNT = 60;

        protected String[] mDataset;

        private RecyclerView mRecyclerView;

        private RecyclerView.Adapter mAdapter;

        private RecyclerView.LayoutManager mLayoutManager;

        public CourseFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initDataset();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.courses_fragment, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new CourseAdapter(mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            mRecyclerView.setAdapter(mAdapter);

            return rootView;
        }

        /**
         * Generates Strings for RecyclerView's adapter. This data would usually come
         * from a local content provider or remote server.
         */
        private void initDataset() {
            mDataset = new String[DATASET_COUNT];
            for (int i = 0; i < DATASET_COUNT; i++) {
                mDataset[i] = "This is element #" + i;
            }
        }
    }
}
