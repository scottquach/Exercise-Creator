package com.example.scott.speaksteps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Scott Quach on 5/20/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> entrySet;
    private Context context;
    private int step= 0;
    private int tracker = 0;

    private final int NORMAL = 0;
    private final int DONE = 1;
    private final int CURRENT = 2;

    public RecyclerAdapter(ArrayList<String> entrySet, Context context){
        this.entrySet = entrySet;
        this.context = context;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void resetTracker() {
        this.tracker = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (tracker < step) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_row_done, parent, false);
            return new ViewHolderDone(view);
        } else if (tracker == step){
            View view1 = LayoutInflater.from(context).inflate(R.layout.recycler_row_current, parent, false);
            return new ViewHolderCurrent(view1);
        } else {
            View view2 = LayoutInflater.from(context).inflate(R.layout.recycler_row_item,
                    parent, false);
            return new ViewHolderNormal(view2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Timber.d("tracker " + tracker + " step " + step);
        if (tracker < step) {
            return DONE;
        } else if (tracker == step) {
            return CURRENT;
        } else {
            return NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case NORMAL:
                ((ViewHolderNormal) holder).entryNameView.setText(entrySet.get(position));
                break;
            case DONE:
                ((ViewHolderDone) holder).entryNameView.setText(entrySet.get(position));
                break;
            case CURRENT:
                ((ViewHolderCurrent) holder).entryNameView.setText(entrySet.get(position));
                break;
        }
        tracker++;
    }

    @Override
    public int getItemCount() {
        return entrySet.size();
    }

    public static class ViewHolderNormal extends RecyclerView.ViewHolder {
        public TextView entryNameView;

        public ViewHolderNormal(View itemView) {
            super(itemView);
            entryNameView = (TextView) itemView.findViewById(R.id.entryNameView);
        }
    }

    public static class ViewHolderDone extends RecyclerView.ViewHolder {
        public TextView entryNameView;

        public ViewHolderDone(View itemView) {
            super(itemView);
            entryNameView = itemView.findViewById(R.id.entryNameView);
        }
    }

    public static class ViewHolderCurrent extends RecyclerView.ViewHolder {
        public TextView entryNameView;

        public ViewHolderCurrent(View itemView) {
            super(itemView);
            entryNameView = itemView.findViewById(R.id.entryNameView);
        }
    }
}
