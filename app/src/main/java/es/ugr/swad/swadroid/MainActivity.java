package es.ugr.swad.swadroid;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com) on 15/03/15.
 */

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.ugr.swad.swadroid.gui.CourseAdapter;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;

/**
 * @author Alejandro Alcalde (elbauldelprogramador.com)
 */
public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();

        MaterialAccount account1 = new MaterialAccount(
                this.getResources(),
                "LMD",
                "Dep. Álgebra",
                R.drawable.photo,
                R.drawable.bamboo);
        MaterialAccount account2 = new MaterialAccount(
                this.getResources(),
                "CRIP",
                "Dep. Álgebra",
                R.drawable.photo2,
                R.drawable.mat2);
        MaterialAccount account3 = new MaterialAccount(
                this.getResources(),
                "MH",
                "Decsai",
                R.drawable.photo,
                R.drawable.mat3);

        addAccount(account1);
        addAccount(account2);
        addAccount(account3);

        MaterialSection section1 = newSection(getString(R.string.course),
                new CourseFragment());
        MaterialSection section2 = newSection(getString(R.string.evaluation), R.drawable.ass,
                new CourseFragment());
        MaterialSection section3 = newSection(getString(R.string.users), R.drawable.users,
                new CourseFragment());
        MaterialSection section4 = newSection(getString(R.string.messages), R.drawable.msg,
                new CourseFragment()).setSectionColor(Color.parseColor("#ff0000"));

        addSection(section1);
        addSection(section2);
        addSection(section3);
        addSection(section4);

        setDrawerHeaderImage(R.drawable.mat3);
        setAccountListener(this);
    }

    @Override
    public void onAccountOpening(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnAccountOppening");
    }

    @Override
    public void onChangeAccount(MaterialAccount materialAccount) {
        Log.w(Constants.APP_TAG, "OnChangeAccount");
    }

    public static class CourseFragment extends Fragment {

        private static final String TAG = "CourseFragment";

        private static final int DATASET_COUNT = 60;

        protected ArrayList<String> mDataset;

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
            mDataset = new ArrayList<>();
            for (int i = 0; i < DATASET_COUNT; i++) {
                mDataset.add("This is element #" + i);
            }
        }
    }
}
