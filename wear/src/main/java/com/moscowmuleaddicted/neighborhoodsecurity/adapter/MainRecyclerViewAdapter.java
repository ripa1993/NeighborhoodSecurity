package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.extra.MainItem;

import java.util.List;

/**
 * Created by Simone Ripamonti on 10/06/2017.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private List<MainItem> mItems;
    private OnItemInteractionListener mListener;

    public MainRecyclerViewAdapter(List<MainItem> items, OnItemInteractionListener listener) {
        mItems = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MainItem item = mItems.get(position);
        holder.mItem = item;
        holder.mImage.setImageDrawable(item.getImage());
        holder.mText.setText(String.valueOf(position) + " " + item.getText());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final ImageView mImage;
        public final TextView mText;
        public MainItem mItem;

        public ViewHolder(View view){
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.main_recycler_image);
            mText = (TextView) view.findViewById(R.id.main_recycler_text);
        }

    }

    public interface OnItemInteractionListener {
        void onInteraction(MainItem item);
    }
}
