package com.dagger.globalinfo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.model.InfoObject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Harshit on 25/12/16.
 */

public class InfoAdapter extends FirebaseRecyclerAdapter<InfoObject, InfoAdapter.ViewHolder> {
    public static final String TAG = InfoAdapter.class.getSimpleName();

    private ItemCallback callback;
    private int modelLayout;

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
        this.modelLayout = modelLayout;
    }

    public void setItemCallback(ItemCallback callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(modelLayout, parent, false));
    }

    @Override
    protected void populateViewHolder(final ViewHolder holder, final InfoObject model, int position) {
        holder.bind(model);
    }

    public interface ItemCallback {

        void onItemClicked(InfoObject infoObject);

        void onDeleteClicked(InfoObject infoObject, DatabaseReference databaseReference);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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

        @OnClick(R.id.single_item_card)
        void cardClicked() {
            if (callback != null) {
                InfoObject infoObject = getItem(getAdapterPosition());
                callback.onItemClicked(infoObject);
            }
        }

        @OnClick(R.id.delete)
        void deleteClicked() {
            if (callback != null) {
                InfoObject infoObject = getItem(getAdapterPosition());
                callback.onDeleteClicked(infoObject, getRef(getAdapterPosition()));
            }
        }

        void bind(InfoObject model) {
            date.setText(model.getTimestamp());
            description.setText(model.getDescription());
            title.setText(model.getTitle());
            try {
                Picasso.with(itemView.getContext())
                        .load(model.getPhoto())
                        .placeholder(R.drawable.default_pic)
                        .error(R.drawable.default_pic)
                        .into(author);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(itemView.getContext())
                        .load(R.drawable.default_pic)
                        .into(author);
            }
            if (model.getAuthor() != null)
                authorName.setText(model.getAuthor());
            delete.setVisibility(View.VISIBLE);
        }
    }
}
