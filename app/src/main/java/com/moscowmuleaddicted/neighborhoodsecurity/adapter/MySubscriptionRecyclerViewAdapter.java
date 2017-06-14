package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionListFragment.OnListFragmentInteractionListener;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.SHARED_PREFERENCES_SUBSCRIPTIONS;

public class MySubscriptionRecyclerViewAdapter extends RecyclerViewWithEmptyView.Adapter<MySubscriptionRecyclerViewAdapter.ViewHolder> {
    public static final String TAG = "MySusRVAdapter";
    private final List<Subscription> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySubscriptionRecyclerViewAdapter(List<Subscription> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        Collections.sort(mValues, idComparator);
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

        final Context context = holder.mCity.getContext();
        final SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_SUBSCRIPTIONS, Context.MODE_PRIVATE);
        boolean enabled = sharedPreferences.getBoolean(String.valueOf(holder.mItem.getId()), true);
        holder.mSwitch.setChecked(enabled);
        if (enabled) {
            holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_notifications_button));
        } else {
            holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_turn_notifications_off_button));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemClick(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListener) {
                    return mListener.onListItemLongClick(holder.mItem, v);
                }
                return false;
            }
        });

        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(String.valueOf(holder.mItem.getId()), isChecked);
                editor.commit();
                if (isChecked) {
                    holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_notifications_button));
                } else {
                    holder.mIcon.setImageDrawable(context.getDrawable(R.drawable.ic_turn_notifications_off_button));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerViewWithEmptyView.ViewHolder {
        public final View mView;
        public final TextView mCity;
        public final TextView mStreet;
        public final TextView mRadius;
        public Subscription mItem;
        public final Switch mSwitch;
        public final ImageView mIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCity = (TextView) view.findViewById(R.id.sub_city);
            mStreet = (TextView) view.findViewById(R.id.sub_street);
            mRadius = (TextView) view.findViewById(R.id.sub_radius);
            mSwitch = (Switch) view.findViewById(R.id.switch_notifications);
            mIcon = (ImageView) view.findViewById(R.id.sub_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCity.getText() + " " + mStreet.getText() + " " + mRadius.getText() + "'";
        }
    }

    public synchronized void addSubscriptions(Collection<Subscription> subscriptions) {
        int oldSize = mValues.size();
        // remove events already present
        ArrayList<Subscription> tempSubscriptions = new ArrayList<Subscription>(subscriptions);
        tempSubscriptions.removeAll(mValues);
        // add remaining ones
        Log.d(TAG, "inserted " + tempSubscriptions.size() + " items");
        mValues.addAll(tempSubscriptions);
        // notify changes
//        notifyItemRangeInserted(oldSize, tempSubscriptions.size());
        Collections.sort(mValues, idComparator);
        notifyDataSetChanged();
    }

    private Comparator<Subscription> idComparator = new Comparator<Subscription>() {
        @Override
        public int compare(Subscription o1, Subscription o2) {
            return Integer.valueOf(o1.getId()).compareTo(o2.getId());
        }
    };

    public synchronized void removeSubscription(Subscription s){
        mValues.remove(s);
        notifyDataSetChanged();
    }


}
