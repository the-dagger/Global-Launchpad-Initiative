package com.dagger.globalinfo.fragment;

/**
 * Created by Harshit on 31/12/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.InfoAdapter;
import com.dagger.globalinfo.model.InfoObject;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dagger.globalinfo.activity.MainActivity.eduDbReference;
import static com.dagger.globalinfo.activity.MainActivity.hackDbReference;
import static com.dagger.globalinfo.activity.MainActivity.meetDbReference;
import static com.dagger.globalinfo.activity.MainActivity.techDbReference;

public class MainActivityFragment extends Fragment {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.recyclerViewContent)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private InfoAdapter infoAdapter;
    private Unbinder unbinder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 300.00);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        final int position = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.e(TAG, String.valueOf(position));
        fetchData(position);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchData(position);
            }
        });
        recyclerView.setAdapter(infoAdapter);
        return rootView;
    }

    public void addListener(DatabaseReference databaseReference) {
        infoAdapter = new InfoAdapter(
                InfoObject.class,
                R.layout.single_info,
                InfoAdapter.ViewHolder.class,
                databaseReference.orderByChild("timeInMillis"));
    }

    public void fetchData(int position) {
        if (infoAdapter != null) {
            infoAdapter.cleanup();
        }
        switch (position) {
            case 0:
                addListener(eduDbReference);
                break;
            case 1:
                addListener(hackDbReference);
                break;
            case 2:
                addListener(meetDbReference);
                break;
            case 3:
                addListener(techDbReference);
                break;
        }
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        cleanup();
    }

    private void cleanup() {
        if (infoAdapter != null) {
            infoAdapter.cleanup();
        }
    }
}


