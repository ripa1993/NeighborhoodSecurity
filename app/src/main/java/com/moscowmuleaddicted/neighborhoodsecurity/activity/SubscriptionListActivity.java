package com.moscowmuleaddicted.neighborhoodsecurity.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.internal.view.SupportSubMenu;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.moscowmuleaddicted.neighborhoodsecurity.fragment.SubscriptionListFragment;
import com.moscowmuleaddicted.neighborhoodsecurity.utilities.jsonclasses.Subscription;

import java.util.ArrayList;

public class SubscriptionListActivity extends AppCompatActivity implements SubscriptionListFragment.OnListFragmentInteractionListener {

    private FloatingActionButton mFab;
    private SubscriptionListFragment mFragment;
    private ArrayList<Subscription> mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        Bundle extras = getIntent().getExtras();

        boolean sample = true;

        if (extras != null) {
            if (extras.containsKey("subscription-list")) {
                mSubscriptions = (ArrayList<Subscription>) extras.getSerializable("subscription-list");
                sample = false;
            }
        }

        if (sample) {
            generateSampleList();
        }
        mFragment = SubscriptionListFragment.newInstance(1, mSubscriptions);

        // initialize fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.subscription_list_fragment, mFragment);
        fragmentTransaction.commit();



        mFab = (FloatingActionButton) findViewById(R.id.subscription_create_fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SubscriptionCreateActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onListFragmentInteraction(Subscription item) {
        Toast.makeText(getApplicationContext(), String.valueOf(item.getId()), Toast.LENGTH_SHORT).show();
    }

    private void generateSampleList() {
        mSubscriptions = new ArrayList<>();
        Subscription a = new Subscription();
        a.setId(1);
        a.setCity("Guanzate");
        a.setCountry("Italy");
        a.setStreet("Via Roma 3");
        a.setRadius(1000);
        Subscription b = new Subscription();
        b.setId(2);
        b.setCity("Como");
        b.setCountry("Italy");
        b.setStreet("Via Innocenzo 23");
        b.setRadius(500);
        Subscription c = new Subscription();
        c.setId(3);
        c.setCity("Milano");
        c.setCountry("Italy");
        c.setStreet("Via Golgi 20");
        c.setRadius(2000);
        mSubscriptions.add(a);
        mSubscriptions.add(b);
        mSubscriptions.add(c);
    }
}
