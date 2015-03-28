package es.ugr.swad.swadroid.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.ugr.swad.swadroid.Constants;
import es.ugr.swad.swadroid.R;

/**
 * Created by Alejandro Alcalde (elbauldelprogramador.com) on 20/03/15.
 */
public class SwadListFragment extends Fragment {

    private static final String TAG = "SwadListFragment";

    private static final int DATASET_COUNT = 9;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public SwadListFragment() {
    }

    public static SwadListFragment newInstance(int[] icons, int[] text) {

        SwadListFragment fragment = new SwadListFragment();

        Bundle args = new Bundle();

        args.putIntArray(Constants.TEXT_ARG, text);
        args.putIntArray(Constants.DRW_ARG, icons);
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
        View rootView = inflater.inflate(R.layout.courses_fragment, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SwadListAdapter(getArguments().getIntArray(Constants.DRW_ARG), getArguments().getIntArray(Constants.TEXT_ARG));
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}
