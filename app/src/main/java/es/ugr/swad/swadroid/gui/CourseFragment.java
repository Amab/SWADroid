package es.ugr.swad.swadroid.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import es.ugr.swad.swadroid.R;

/**
 * Created by Alejandro Alcalde (elbauldelprogramador.com) on 20/03/15.
 */
public class CourseFragment extends Fragment {

    private static final String TAG = "CourseFragment";

    private static final int DATASET_COUNT = 9;

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

        int[] text = new int[] {
                R.string.introductionModuleLabel,
                R.string.teachingguideModuleLabel,
                R.string.syllabusLecturesModuleLabel,
                R.string.syllabusPracticalsModuleLabel,
                R.string.documentsDownloadModuleLabel,
                R.string.sharedsDownloadModuleLabel,
                R.string.bibliographyModuleLabel,
                R.string.faqsModuleLabel,
                R.string.linksModuleLabel
        };

        mDataset = new ArrayList<>();
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset.add(getString(text[i]));
        }
    }
}
