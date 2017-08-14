package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.customlistener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by STEPHAN987 on 8/12/2017.
 */

public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    //private TweetsArrayAdapter aTweets;
    private TweetsAdapter aTweets;
    //private ListView lvTweets;
    private RecyclerView rvTweets;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fabCompose;
    private ProgressBar progressBarFooter;


    private long sinceID = 1;
    private long maxID = 1;

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public RecyclerView getRvTweets() {
        return rvTweets;
    }

    public TweetsAdapter getATweets() {
        return aTweets;
    }

    public EndlessRecyclerViewScrollListener getScrollListener() {
        return scrollListener;
    }

    public SwipeRefreshLayout getSwipeContainer() {
        return swipeContainer;
    }

    public FloatingActionButton getFabCompose() {
        return fabCompose;
    }

    public long getMaxID() {
        return maxID;
    }

    public long getSinceID() {
        return sinceID;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    public void setSinceID(long sinceID) {
        this.sinceID = sinceID;
    }

    public void setMaxID(long maxID) {
        this.maxID = maxID;
    }

    public ProgressBar getProgressBarFooter() {
        return progressBarFooter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);

        rvTweets.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        // Inflate the footer
        View footer = inflater.inflate(
                R.layout.progress, parent, false);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)
                footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        rvTweets.setAdapter(aTweets);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);

        //PULL TO REFRESH
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        fabCompose = (FloatingActionButton) v.findViewById(R.id.fabCompose);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        //aTweets = new TweetsArrayAdapter(this, tweets);
        aTweets = new TweetsAdapter(getActivity(), tweets);
    }



    public void addPulledData(ArrayList<Tweet> items, boolean resetScrollToStart){
        int currentSize = 0;
        if (!resetScrollToStart){
            currentSize = aTweets.getItemCount();
        }
        tweets.addAll(items);
        aTweets.notifyItemRangeInserted(currentSize, items.size());
        if (resetScrollToStart){
            rvTweets.scrollToPosition(0);   // index 0 position
        }
        maxID = tweets.get(tweets.size()-1).getUid() - 1;
        sinceID = tweets.get(0).getUid();
    }

    public void insertNewSingleItem(Tweet newTweet){
        int indexToUse = 0;
        for (int i = 0; i < aTweets.getItemCount(); i++){
            Tweet retrievedTweet = aTweets.getItem(i);
            if (retrievedTweet.getUid() < newTweet.getUid()){
                indexToUse = i;
                break;
            }
        }
        //aTweets.insert(newTweet,indexToUse);
        tweets.add(indexToUse,newTweet);
        aTweets.notifyItemInserted(indexToUse);
        rvTweets.scrollToPosition(indexToUse);   // index 0 position
        sinceID = newTweet.getUid();
        maxID = tweets.get(tweets.size()-1).getUid() - 1;
    }

}
