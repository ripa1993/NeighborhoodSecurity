package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment.OnListFragmentInteractionListener;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter for an ArrayList of Events
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class MyEventRecyclerViewAdapter extends RecyclerViewWithEmptyView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {
    public static final String TAG ="MyEvetRVAdapter";
    /**
     * Application context, used to retrieve localized strings for the events
     */
    private Context mContext;
    /**
     * List of events to display
     */
    private final List<Event> mValues;
    /**
     * Listener for element click
     */
    private final OnListFragmentInteractionListener mListener;
    /**
     * Date formatter
     */
    private java.text.DateFormat mDateFormat;
    /**
     * Constructor
     * @param items events to display
     * @param listener the listener to perform callback
     * @param context the application context
     */
    public MyEventRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
        mDateFormat = DateFormat.getDateFormat(mContext);
        Collections.sort(mValues, dateComparator);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Event e = mValues.get(position);

        holder.mItem = mValues.get(position);
        holder.mEventType.setText(e.getEventType().toString());
        holder.mEventLocation.setText(e.getCity()+", "+e.getStreet());
        holder.mEventDate.setText(mDateFormat.format(e.getDate()));


        if(e.getVotes() > 99){
            holder.mEventVotes.setText("99+");
        } else {
            holder.mEventVotes.setText(String.valueOf(e.getVotes()));
        }

        switch (e.getEventType()){
            case BURGLARY:
                holder.mEventIcon.setImageResource(R.drawable.ic_burglary);
                break;
            case CARJACKING:
                holder.mEventIcon.setImageResource(R.drawable.ic_carjacking);
                break;
            case ROBBERY:
                holder.mEventIcon.setImageResource(R.drawable.ic_robbery);
                break;
            case SCAMMERS:
                holder.mEventIcon.setImageResource(R.drawable.ic_scammers);
                break;
            case SHADY_PEOPLE:
                holder.mEventIcon.setImageResource(R.drawable.ic_shady_people);
                break;
            case THEFT:
                holder.mEventIcon.setImageResource(R.drawable.ic_theft);
                break;
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");
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
                Log.d(TAG, "long clicked");
                if (null != mListener){
                    return mListener.onListItemLongClick(holder.mItem, v);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * View Holder to contain the events data
     */
    public class ViewHolder extends RecyclerViewWithEmptyView.ViewHolder {
        public final View mView;
        public final TextView mEventType;
        public final TextView mEventLocation;
        public final TextView mEventDate;
        public final TextView mEventVotes;
        public final ImageView mEventIcon;

        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mEventType = (TextView) view.findViewById(R.id.event_type);
            mEventLocation = (TextView) view.findViewById(R.id.event_location);
            mEventDate = (TextView) view.findViewById(R.id.event_date);
            mEventVotes = (TextView) view.findViewById(R.id.event_votes);
            mEventIcon = (ImageView) view.findViewById(R.id.event_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mEventType.getText() + " "+ mEventDate.getText() + "'";
        }
    }

    public synchronized void addEvents(Collection<Event> events){
        int oldSize = mValues.size();
        // remove events already present
        ArrayList<Event> tempEvents = new ArrayList<Event>(events);
        tempEvents.removeAll(mValues);
        // add remaining ones
        mValues.addAll(tempEvents);
        // notify changes
        Log.d(TAG, "inserted "+tempEvents.size()+" items");
//        notifyItemRangeInserted(oldSize, tempEvents.size());
        Collections.sort(mValues, dateComparator);
        notifyDataSetChanged();
    }

    public synchronized void removeEvent(Event e){
        mValues.remove(e);
        notifyDataSetChanged();
    }

    public synchronized void clear() {
        int size = this.mValues.size();
        this.mValues.clear();
        Log.d(TAG, "removed "+size+" items");
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    private Comparator<Event> dateComparator = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    };

    private Comparator<Event> voteComparator = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return Integer.valueOf(o2.getVotes()).compareTo(o1.getVotes());
        }
    };
}
