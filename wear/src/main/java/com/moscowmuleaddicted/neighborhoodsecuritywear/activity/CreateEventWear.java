package com.moscowmuleaddicted.neighborhoodsecuritywear.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;

import com.moscowmuleaddicted.neighborhoodsecuritywear.R;
import com.moscowmuleaddicted.neighborhoodsecuritywear.adapter.MainRecyclerViewAdapter;
import com.moscowmuleaddicted.neighborhoodsecuritywear.extra.ItemType;
import com.moscowmuleaddicted.neighborhoodsecuritywear.extra.MainItem;

import java.util.List;

public class CreateEventWear extends Activity implements MainRecyclerViewAdapter.OnItemInteractionListener {

    private WearableRecyclerView mRecyclerView;
    private CurvedChildLayoutManager mChildLayoutManager;
    private List<MainItem> mActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.new_event), ItemType.NEW_EVENT));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.nearby), ItemType.NEARBY));
        mActions.add(new MainItem(getDrawable(R.mipmap.ic_launcher), getString(R.string.map), ItemType.MAP));

        mRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_view_main);
        mChildLayoutManager = new CurvedChildLayoutManager(getApplicationContext());

        mRecyclerView.setCenterEdgeItems(true);
        mRecyclerView.setLayoutManager(mChildLayoutManager);
        mRecyclerView.setCircularScrollingGestureEnabled(true);
        mRecyclerView.setAdapter(new MainRecyclerViewAdapter(mActions, this));


    }

    @Override
    public void onInteraction(MainItem item) {
        switch(item.getType()){
            case NEW_EVENT:
                break;
            case MAP:
                break;
            case NEARBY:
                break;
            default:
                break;
        }

    }
}
