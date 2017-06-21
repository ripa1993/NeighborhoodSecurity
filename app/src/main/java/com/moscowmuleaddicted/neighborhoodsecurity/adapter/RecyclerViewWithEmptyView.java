package com.moscowmuleaddicted.neighborhoodsecurity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * {@link RecyclerView} extension that shows an particular view when no data to show is available
 *
 * @author Simone Ripamonti
 * @version 1
 */
public class RecyclerViewWithEmptyView extends RecyclerView {
    /**
     * Logger's TAG
     */
    public static final String TAG = "RecyclerViewWEmptyV";
    /**
     * The view that must be shown when no data is available
     */
    private View mEmptyView;
    /**
     * Data observer that updates the empty view
     */
    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Log.d(TAG, "data changed");
            updateEmptyView();
        }
    };

    /**
     * Creator
     * @param context
     */
    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    /**
     * Creator
     * @param context
     * @param attrs
     */
    public RecyclerViewWithEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Creator
     * @param context
     * @param attrs
     * @param defStyle
     */
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

    /**
     * Shows or hides mEmptyView according to the data available to the adapter
     */
    public void updateEmptyView() {
        if (mEmptyView != null && getAdapter() != null) {
            boolean showEmptyView = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(showEmptyView ? VISIBLE : GONE);
            setVisibility(showEmptyView ? GONE : VISIBLE);
        }
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
}
