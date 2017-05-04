package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.EventListFragment.OnListFragmentInteractionListener;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Event;

import java.util.List;

/**
 * Adapter for an ArrayList of Events
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

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
        holder.mEventLocation.setText(e.getStreet()+", "+e.getCity());
        holder.mEventDate.setText(mDateFormat.format(e.getDate()));

//        String singleVote = mContext.getResources().getString(R.string.single_vote);
//        String multipleVotes = mContext.getResources().getString(R.string.multiple_vote);
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
     * View Holder to contain the events data
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
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
}
