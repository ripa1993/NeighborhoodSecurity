package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventDetailListFragment.OnListFragmentInteractionListener;
import com.moscowmuleaddicted.neighborhoodsecurity.model.Detail;

import java.util.List;

/**
 * Recycler View extension to show objects of the class {@link Detail}
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.DetailsViewHolder> {
    /**
     * The values to be shown
     */
    private final List<Detail> mValues;
    /**
     * The listener
     */
    private final OnListFragmentInteractionListener mListener;

    /**
     * Adapter creator
     * @param items list of {@link Detail} to be shown
     * @param listener to which notify events
     */
    public DetailRecyclerViewAdapter(List<Detail> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Function to change the value of a particular {@link Detail}, identified by position
     * @param i the new value
     */
    public void updateVotes(int i){
        int votes = Integer.valueOf(mValues.get(mValues.size() - 1).getContent());
        votes += i;
        mValues.get(mValues.size() - 1).setContent(String.valueOf(votes));
        notifyItemChanged(mValues.size() -1);
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_eventdetail, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailsViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getName());
        holder.mContentView.setText(mValues.get(position).getContent());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Extension of ViewHolder that is used to display {@link Detail} content
     */
    public class DetailsViewHolder extends RecyclerView.ViewHolder {
        /**
         * The parent view
         */
        public final View mView;
        /**
         * Text field to show the detail name
         */
        public final TextView mIdView;
        /**
         * Text field to show the detail content
         */
        public final TextView mContentView;
        /**
         * The {@link Detail} item shown in this view
         */
        public Detail mItem;

        /**
         * The creator
         * @param view that needs to be populated
         */
        public DetailsViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
