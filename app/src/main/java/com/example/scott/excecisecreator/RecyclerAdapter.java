package com.example.scott.excecisecreator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Scott Quach on 5/20/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<String> entrySet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView entryNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            entryNameView = (TextView) itemView.findViewById(R.id.entryNameView);
        }
    }

    public RecyclerAdapter(ArrayList<String> entrySet, Context context){
        this.entrySet = entrySet;
        this.context = context;
    }


    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item,
                parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        holder.entryNameView.setText(entrySet.get(position));
    }

    @Override
    public int getItemCount() {
        return entrySet.size();
    }
}
