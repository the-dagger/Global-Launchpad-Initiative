package com.dagger.globalinfo.adapter;

import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.activity.MainActivity;
import com.dagger.globalinfo.model.InfoObject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harshit on 25/12/16.
 */

public class InfoAdapter extends FirebaseRecyclerAdapter<InfoObject, InfoAdapter.ViewHolder> {
    public static final String TAG = InfoAdapter.class.getSimpleName();

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public InfoAdapter(Class<InfoObject> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }


    @Override
    protected void populateViewHolder(final ViewHolder holder, final InfoObject model, int position) {
        holder.date.setText(model.getTimestamp());
        holder.description.setText(model.getDescription());
        holder.title.setText(model.getTitle());
        Picasso.with(holder.itemView.getContext())
                .load(model.getPhoto())
                .placeholder(R.drawable.default_pic)
                .error(R.drawable.default_pic)
                .into(holder.author);
        holder.authorName.setText(model.getAuthor());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = model.getUrl();
                if (!(url.startsWith("https://") || url.startsWith("http://"))) {
                    url = "https://" + url;
                }

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary))
                        .addDefaultShareMenuItem()
                        .setShowTitle(true);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(view.getContext(), Uri.parse(url));
            }
        });
        if (!MainActivity.admins.contains(MainActivity.auth.getCurrentUser().getEmail()))
            holder.delete.setVisibility(View.GONE);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.date_added)
        TextView date;
        @BindView(R.id.author_name)
        TextView authorName;
        @BindView(R.id.author)
        CircleImageView author;
        @BindView(R.id.single_item_card)
        CardView cardView;
        @BindView(R.id.delete)
        ImageButton delete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
