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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dagger.globalinfo.GlobalInfoApplication;
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.InfoAdapter;
import com.dagger.globalinfo.di.application.ApplicationComponent;
import com.dagger.globalinfo.di.list.DaggerListComponent;
import com.dagger.globalinfo.di.list.ListComponent;
import com.dagger.globalinfo.di.list.ListModule;
import com.dagger.globalinfo.di.qualifiers.Content;
import com.dagger.globalinfo.model.InfoObject;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivityFragment extends Fragment implements InfoAdapter.ItemCallback {
    public static final String TAG = MainActivityFragment.class.getSimpleName();

    public static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.recyclerViewContent)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Inject
    InfoAdapter infoAdapter;
    @Inject
    @Content
    DatabaseReference contentReference;
    @Inject
    GridLayoutManager layoutManager;
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

        ApplicationComponent applicationComponent = GlobalInfoApplication.get(getContext()).getComponent();
        ListComponent component = DaggerListComponent.builder()
                .listModule(new ListModule(getCurrReference(), getContext()))
                .applicationComponent(applicationComponent)
                .build();
        component.inject(this);
        infoAdapter.setItemCallback(this);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setAdapter(infoAdapter);
        return rootView;
    }


    private DatabaseReference getCurrReference() {
        int position = getArguments().getInt(ARG_SECTION_NUMBER);
        ApplicationComponent applicationComponent = GlobalInfoApplication.get(getContext()).getComponent();
        switch (position) {
            case 0:
                return applicationComponent.educationReference();
            case 1:
                return applicationComponent.hackReference();
            case 2:
                return applicationComponent.meetReference();
            case 3:
                return applicationComponent.technicalReference();
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
            contentReference.child(contentKey).removeValue();
        }
        databaseReference.removeValue();
    }
}


