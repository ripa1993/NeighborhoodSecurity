package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moscowmuleaddicted.neighborhoodsecurity.adapter.DetailRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Detail;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Event;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_COLUMN_COUNT;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_EVENT;

/**
 * Fragment that shows a list of {@link Detail} about a given event
 */
public class EventDetailListFragment extends Fragment {
    /**
     * Number of columns
     */
    private int mColumnCount = 1;
    /**
     * Currently shown event details
     */
    private Event mEvent;
    /**
     * Fragment listener
     */
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventDetailListFragment() {
    }

    /**
     * Builder method
     * @param columnCount number of columns
     * @param event to be shown
     * @return
     */
    public static EventDetailListFragment newInstance(int columnCount, Event event) {
        EventDetailListFragment fragment = new EventDetailListFragment();
        Bundle args = new Bundle();
        args.putInt(IE_COLUMN_COUNT, columnCount);
        args.putSerializable(IE_EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(IE_COLUMN_COUNT);
            mEvent = (Event) getArguments().getSerializable(IE_EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventdetail_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new DetailRecyclerViewAdapter(Detail.listFromEvent(mEvent), mListener));
        }
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

    /**
     * Fragment listener interface
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Detail item);
    }
}
