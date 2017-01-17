package com.dagger.globalinfo.di.list;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.InfoAdapter;
import com.dagger.globalinfo.model.InfoObject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import dagger.Module;
import dagger.Provides;

/**
 * Created by saurabh on 16/1/17.
 */

@Module
public class ListModule {
    private DatabaseReference databaseReference;
    private Context context;

    public ListModule(DatabaseReference databaseReference, Context context) {
        this.databaseReference = databaseReference;
        this.context = context;
    }

    @Provides
    Class<InfoObject> provideInfoClass() {
        return InfoObject.class;
    }

    @Provides
    int provideLayout() {
        return R.layout.single_info;
    }


    @Provides
    Class<InfoAdapter.ViewHolder> provideHolderClass() {
        return InfoAdapter.ViewHolder.class;
    }

    @Provides
    Query provideQuery() {
        return databaseReference.orderByChild("timeInMillis");
    }

    @Provides
    GridLayoutManager provideLayoutManager() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int spanCount = (int) (width / 300.00);
        return new GridLayoutManager(context, spanCount);
    }

}
