package com.dagger.globalinfo.adapter;

import android.view.View;

import com.dagger.globalinfo.activity.MainActivity;
import com.dagger.globalinfo.model.InfoObject;
import com.google.firebase.database.Query;

import javax.inject.Inject;

/**
 * Created by Harshit on 08/01/17.
 */

public class InfoAdapter extends BaseAdapter {
    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    @Inject
    public InfoAdapter(Class<InfoObject> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ViewHolder holder, InfoObject model, int position) {
        super.populateViewHolder(holder, model, position);
        holder.delete.setVisibility(MainActivity.isAdmin ? View.VISIBLE : View.GONE);
    }
}
