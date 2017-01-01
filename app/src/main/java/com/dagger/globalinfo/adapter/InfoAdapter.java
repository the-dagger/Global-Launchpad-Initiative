package com.dagger.globalinfo.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.activity.MainActivity;
import com.dagger.globalinfo.model.InfoObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.dagger.globalinfo.activity.MainActivity.EDUCATION;

/**
 * Created by Harshit on 25/12/16.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private ArrayList<InfoObject> arrayList;
    private Context context;

    public InfoAdapter(ArrayList<InfoObject> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public InfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InfoAdapter.ViewHolder holder, int position) {
        holder.date.setText(arrayList.get(position).getTimestamp());
        holder.description.setText(arrayList.get(position).getDescription());
        holder.title.setText(arrayList.get(position).getTitle());
        Picasso.with(context).load(arrayList.get(holder.getAdapterPosition())
                .getPhoto())
                .placeholder(R.drawable.default_pic)
                .error(R.drawable.default_pic)
                .into(holder.author);
        holder.authorName.setText(arrayList.get(position).getAuthor());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = arrayList.get(holder.getAdapterPosition()).getUrl();
                if (!url.startsWith("https://") || !url.startsWith("http://")) {
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


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void removeItem(InfoObject object) {
        switch (object.getCategory()) {
            case EDUCATION:
//                eduDbReference.
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InfoObject object = arrayList.get(getAdapterPosition());
                    removeItem(object);
                }
            });
        }
    }
}
