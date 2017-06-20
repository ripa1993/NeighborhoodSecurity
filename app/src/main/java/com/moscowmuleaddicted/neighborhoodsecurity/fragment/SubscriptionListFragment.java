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
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.MySubscriptionRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecurity.adapter.RecyclerViewWithEmptyView;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.model.Subscription;

import java.util.ArrayList;
import java.util.List;

import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_COLUMN_COUNT;
import static com.moscowmuleaddicted.neighborhoodsecurity.utilities.Constants.IE_SUBSCRIPTION_LIST;

public class SubscriptionListFragment extends Fragment {


    private List<Subscription> mSubscriptions = new ArrayList<>();
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private RecyclerViewWithEmptyView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SubscriptionListFragment() {
    }


    public static SubscriptionListFragment newInstance(int columnCount, ArrayList<Subscription> subscriptions) {
        SubscriptionListFragment fragment = new SubscriptionListFragment();
        Bundle args = new Bundle();
        args.putInt(IE_COLUMN_COUNT, columnCount);
        args.putSerializable(IE_SUBSCRIPTION_LIST, subscriptions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(IE_COLUMN_COUNT);
            mSubscriptions = (ArrayList<Subscription>) getArguments().getSerializable(IE_SUBSCRIPTION_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_list, container, false);
        Context context = view.getContext();

        mRecyclerView = (RecyclerViewWithEmptyView) view.findViewById(R.id.fragment_subscription_list);
        mRecyclerView.setEmptyView(view.findViewById(R.id.empty_view_subscriptions));
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mRecyclerView.setAdapter(new MySubscriptionRecyclerViewAdapter(mSubscriptions, mListener));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        void onListItemClick(Subscription item);

        void scrollingUp();

        void scrollingDown();

        boolean onListItemLongClick(Subscription mItem, View v);
    }

    public RecyclerView getRecyclerView() {

        return mRecyclerView;
    }

    public void showData(List<Subscription> subscriptions) {
        mRecyclerView.swapAdapter(new MySubscriptionRecyclerViewAdapter(subscriptions, mListener), false);
    }

    public void removeSubscription(Subscription s){
        ((MySubscriptionRecyclerViewAdapter) mRecyclerView.getAdapter()).removeSubscription(s);
    }

}
