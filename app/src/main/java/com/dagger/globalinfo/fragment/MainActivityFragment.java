package com.dagger.globalinfo.fragment;

/**
 * Created by Harshit on 31/12/16.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

import static com.dagger.globalinfo.activity.MainActivity.contentDbReference;
import static com.dagger.globalinfo.activity.MainActivity.eduDbReference;
import static com.dagger.globalinfo.activity.MainActivity.hackDbReference;
import static com.dagger.globalinfo.activity.MainActivity.meetDbReference;
import static com.dagger.globalinfo.activity.MainActivity.techDbReference;

public class MainActivityFragment extends Fragment implements InfoAdapter.ItemCallback {
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

    public static MainActivityFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);
        MainActivityFragment fragment = new MainActivityFragment();
        fragment.setArguments(args);
        return fragment;
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
        fetchData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetchData();
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
        infoAdapter.setItemCallback(this);
    }

    public void fetchData() {
        if (infoAdapter != null) {
            infoAdapter.cleanup();
        }
        if (getCurrReference() != null) {
            addListener(getCurrReference());
        }
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

    private DatabaseReference getCurrReference() {
        int position = getArguments().getInt(ARG_SECTION_NUMBER);
        switch (position) {
            case 0:
                return eduDbReference;
            case 1:
                return hackDbReference;
            case 2:
                return meetDbReference;
            case 3:
                return techDbReference;
            default:
                return null;
        }
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

    @Override
    public void onItemClicked(InfoObject model) {
        String url = model.getUrl();
        if (!(url.startsWith("https://") || url.startsWith("http://"))) {
            url = "https://" + url;
        }

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .addDefaultShareMenuItem()
                .setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }

    @Override
    public void onDeleteClicked(InfoObject infoObject, DatabaseReference databaseReference) {
        String contentKey = infoObject.getContentKey();
        //Backport check.
        if (!TextUtils.isEmpty(contentKey)) {
            contentDbReference.child(contentKey).removeValue();
        }
        databaseReference.removeValue();
    }
}


