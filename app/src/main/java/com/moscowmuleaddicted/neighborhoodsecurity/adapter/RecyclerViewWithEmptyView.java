package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Simone Ripamonti on 25/05/2017.
 */

public class RecyclerViewWithEmptyView extends RecyclerView {
    private View mEmptyView;
    public static final String TAG = "RecyclerViewWithEmptyV";
    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Log.d(TAG, "data changed");
            updateEmptyView();
        }
    };

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Designate a view as the empty view. When the backing adapter has no
     * data this view will be made visible and the recycler view hidden.
     *
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    @Override
    public void setAdapter(RecyclerViewWithEmptyView.Adapter adapter) {
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
        updateEmptyView();
    }

    public void updateEmptyView() {
        if (mEmptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            setVisibility(showEmptyView ? GONE : VISIBLE);
        }
    }
}
