package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionListFragment.OnListFragmentInteractionListener;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;

import java.util.List;

public class MySubscriptionRecyclerViewAdapter extends RecyclerView.Adapter<MySubscriptionRecyclerViewAdapter.ViewHolder> {
    private final List<Subscription> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySubscriptionRecyclerViewAdapter(List<Subscription> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mCity.setText(mValues.get(position).getCity());
        holder.mStreet.setText(mValues.get(position).getStreet());
        holder.mRadius.setText(String.format("%1$dm", mValues.get(position).getRadius()));

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCity;
        public final TextView mStreet;
        public final TextView mRadius;
        public Subscription mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCity = (TextView) view.findViewById(R.id.sub_city);
            mStreet = (TextView) view.findViewById(R.id.sub_street);
            mRadius = (TextView) view.findViewById(R.id.sub_radius);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCity.getText() +" " +mStreet.getText()+" "+mRadius.getText()+ "'";
        }
    }
}
