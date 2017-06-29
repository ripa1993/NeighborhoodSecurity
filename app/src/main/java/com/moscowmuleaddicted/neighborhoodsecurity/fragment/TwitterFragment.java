package com.moscowmuleaddicted.neighborhoodsecurity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moscowmuleaddicted.neighborhoodsecurity.R;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;


public class TwitterFragment extends DialogFragment {

    LinearLayout mLayout;
    Tweet mTweet;

    public TwitterFragment(){

    }

    public static TwitterFragment newInstance(){
        return new TwitterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_twitter, container, false);
        mLayout = (LinearLayout) v.findViewById(R.id.twitter_layout);
        mLayout.addView(new TweetView(getActivity(), mTweet));
        return v;
    }

    public void addTweet(Tweet tweet){
        mTweet = tweet;
    }
}
