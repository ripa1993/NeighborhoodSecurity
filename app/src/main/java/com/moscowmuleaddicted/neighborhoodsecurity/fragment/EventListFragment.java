package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MyEventRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.RecyclerViewWithEmptyView;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;

import java.util.ArrayList;
import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_COLUMN_COUNT;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_EVENT_LIST;

/**
 * Fragment to show a list of events
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class EventListFragment extends Fragment {

    /**
     * number of columns to show
     */
    private int mColumnCount = 1;
    /**
     * list of events
     */
    private List<Event> mListEvents = new ArrayList<>();
    /**
     * listener
     */
    private OnListFragmentInteractionListener mListener;

    private RecyclerViewWithEmptyView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    /**
     * constructor that receives a list of events to show
     *
     * @param columnCount
     * @param events
     * @return
     */
    public static EventListFragment newInstance(int columnCount, ArrayList<Event> events) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(IE_COLUMN_COUNT, columnCount);
        args.putSerializable(IE_EVENT_LIST, events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(IE_COLUMN_COUNT);
            mListEvents = (List<Event>) getArguments().getSerializable(IE_EVENT_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        Context context = view.getContext();

        // Set the adapter
        mRecyclerView = (RecyclerViewWithEmptyView) view.findViewById(R.id.fragment_event_list);
        mRecyclerView.setEmptyView(view.findViewById(R.id.empty_view_events));
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.setAdapter(new MyEventRecyclerViewAdapter(mListEvents, mListener, getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerViewWithEmptyView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    //scrolling up
                    mListener.scrollingUp();
                } else {
                    // scrolling down
                    mListener.scrollingDown();
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListItemClick(Event event);

        void scrollingUp();

        void scrollingDown();

        boolean onListItemLongClick(Event mItem, View view);
    }

    public RecyclerViewWithEmptyView getRecyclerView() {
        return mRecyclerView;
    }

    public void removeEvent(Event e){
        ((MyEventRecyclerViewAdapter) mRecyclerView.getAdapter()).removeEvent(e);
    }
}
