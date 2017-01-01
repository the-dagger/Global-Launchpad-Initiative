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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dagger.globalinfo.activity.MainActivity.eduDbReference;
import static com.dagger.globalinfo.activity.MainActivity.hackDbReference;
import static com.dagger.globalinfo.activity.MainActivity.meetDbReference;
import static com.dagger.globalinfo.activity.MainActivity.techDbReference;

public class MainActivityFragment extends Fragment {

    public static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<InfoObject> infoObjectList = new ArrayList<>();
    InfoAdapter infoAdapter;
    @BindView(R.id.recyclerViewContent)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        infoAdapter = new InfoAdapter(infoObjectList, getContext());
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 300.00);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        final int position = getArguments().getInt(ARG_SECTION_NUMBER);
        Log.e("Position", String.valueOf(position));
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoObjectList.clear();

                for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                    InfoObject note = infoDataSnapshot.getValue(InfoObject.class);
                    infoObjectList.add(0, note);
                }
                infoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fetchData(int position) {
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

}


