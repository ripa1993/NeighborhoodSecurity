package com.moscowmuleaddicted.neighborhoodsecurity.extra;

import android.content.Context;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;

/**
 * Created by Simone Ripamonti on 11/06/2017.
 */

public class MyCurvedChildLayoutManager extends CurvedChildLayoutManager {
    /** How much should we scale the icon at most. */
    private static final float MAX_ICON_PROGRESS = 0.65f;

    private float mProgressToCenter;

    private int mLayoutWidth;
    private int mLayoutHeight;
    private WearableRecyclerView mParentView;

    public MyCurvedChildLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void updateChild(View child, WearableRecyclerView parent) {
        super.updateChild(child, parent);
        if(this.mParentView != parent) {
            this.mParentView = parent;
            this.mLayoutWidth = this.mParentView.getWidth();
            this.mLayoutHeight = this.mParentView.getHeight();
        }

        // Figure out % progress from top to bottom
        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) mParentView.getHeight();
        float yRelativeToCenterOffset = (child.getY() / mParentView.getHeight()) + centerOffset;

        // Normalize for center
        mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
        // Adjust to the maximum scale
        mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS);

        child.setScaleX(1 - mProgressToCenter);
        child.setScaleY(1 - mProgressToCenter);
    }
}
