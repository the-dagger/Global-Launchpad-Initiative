package com.dagger.globalinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_info, parent, false));
    }

    @Override
    public void onBindViewHolder(InfoAdapter.ViewHolder holder, int position) {
        holder.date.setText(arrayList.get(position).getTimestamp());
        holder.description.setText(arrayList.get(position).getDescription());
        holder.title.setText(arrayList.get(position).getTitle());
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(arrayList.get(position).getAuthor().substring(0,1), color);
//        Log.e(TAG, arrayList.get(position).getAuthor().substring(0, 1));
        holder.author.setImageDrawable(drawable);
        holder.authorName.setText(arrayList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, date, authorName;
        ImageView author;

        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date_added);
            author = (ImageView) itemView.findViewById(R.id.author);
            authorName = (TextView) itemView.findViewById(R.id.author_name);
        }
    }
}
