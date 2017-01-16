package com.dagger.globalinfo.di.activity;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.adapter.InfoAdapter;
import com.dagger.globalinfo.di.qualifiers.Object;
import com.dagger.globalinfo.di.qualifiers.ViewHolder;
import com.dagger.globalinfo.model.InfoObject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import dagger.Module;
import dagger.Provides;

/**
 * Created by saurabh on 16/1/17.
 */

@Module
public class ActivityModule {
    private DatabaseReference databaseReference;

    public ActivityModule(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    @Provides
    @Object
    Class<InfoObject> provideInfoClass() {
        return InfoObject.class;
    }

    @Provides
    Integer provideLayout() {
        return R.layout.single_info;
    }


    @Provides
    @ViewHolder
    Class<InfoAdapter.ViewHolder> provideHolderClass() {
        return InfoAdapter.ViewHolder.class;
    }

    @Provides
    Query provideQuery() {
        return databaseReference.orderByChild("timeInMillis");
    }

}
