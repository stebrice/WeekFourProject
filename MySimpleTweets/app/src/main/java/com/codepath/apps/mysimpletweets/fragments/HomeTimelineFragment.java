package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.TweetDetailsActivity;
import com.codepath.apps.mysimpletweets.customlistener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * Created by STEPHAN987 on 8/12/2017.
 */

public class HomeTimelineFragment extends TweetsListFragment implements ComposeFragment.ComposeDialogListener {
    private TwitterClient client;
    private User user;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fabCompose;
    private final int REQUEST_CODE = 10;

    private long sinceID;
    private long maxID;

    public static HomeTimelineFragment newInstance(User user) {

        Bundle args = new Bundle();

        HomeTimelineFragment fragment = new HomeTimelineFragment();
        args.putParcelable("current_user_to_use", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        try{
            client.checkConnection();
            scrollListener = getScrollListener();
            swipeContainer = getSwipeContainer();
            fabCompose = getFabCompose();

            scrollListener = new EndlessRecyclerViewScrollListener(getLinearLayoutManager()) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    maxID = getMaxID();
                    if (maxID > 1){
                        //getProgressBarFooter().setVisibility(View.VISIBLE);
                        populateTimelineFromMaxID(maxID);
                        //getProgressBarFooter().setVisibility(View.GONE);
                    }
                    return;
                }
            };
            super.getRvTweets().addOnScrollListener(scrollListener);

            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    sinceID = getSinceID();
                    populateTimelineFromSinceID(sinceID);
                    swipeContainer.setRefreshing(false);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);


            fabCompose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCompose();
                }
            });

            ItemClickSupport.addTo(getRvTweets()).setOnItemClickListener(
                    new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            Tweet currentTweet = getATweets().getItem(position);
                            showTweetDetails(currentTweet, user);
                            //Toast.makeText(getContext(), "ITEM CLICKED AT POSITION " + String.valueOf(position), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        try{
            client.checkConnection();
            maxID = getMaxID();
            sinceID = getSinceID();
            // get current user Account infos
            user = (User) Parcels.unwrap(getArguments().getParcelable("current_user_to_use"));
            populateTimeline();
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }

    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(getApplicationContext(),"SUCCESS!", Toast.LENGTH_LONG).show();
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(response);
                try {
                    addPulledData(newItems, false);
                    //Toast.makeText(getContext(), String.valueOf(rvTweets.getAdapter().getItemCount()) + " items added",Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateTimelineFromMaxID(long maxToUse){
        client.getHomeTimelineFromMaxID(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(response);
                addPulledData(newItems, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, maxToUse);
    }

    private void populateTimelineFromSinceID(long sinceToUse){
        client.getHomeTimelineFromSinceID(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<Tweet> newItems = Tweet.fromJSONArray(response);
                addPulledData(newItems, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, sinceToUse);
    }

    public void showCompose() {
//        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
//        i.putExtra("user", Parcels.wrap(user));
//        startActivityForResult(i, REQUEST_CODE);
        Tweet tweet = new Tweet("",0,null,"",0,0);
        FragmentManager fm = getFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(user, tweet);
        composeFragment.setTargetFragment(HomeTimelineFragment.this, REQUEST_CODE);
        composeFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            Tweet receivedTweet = null;
            receivedTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("new_tweet_from_reply"));
            if (receivedTweet == null) {
                receivedTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("new_tweet"));
            }

            if (receivedTweet != null)
                insertNewSingleItem(receivedTweet);
            return;
        }
    }


    public void showTweetDetails(Tweet tweetToUse, User currentUser) {
        Intent i = new Intent(getActivity(), TweetDetailsActivity.class);
        i.putExtra("current_tweet", Parcels.wrap(tweetToUse));
        i.putExtra("connected_user", Parcels.wrap(currentUser));
        startActivityForResult(i, REQUEST_CODE);
        //startActivity(i);
    }

    @Override
    public void onFinishComposeDialog(Tweet tweetDefined) {
        if (tweetDefined.getUid() > 0)
            insertNewSingleItem(tweetDefined); //INSERT NEW TWEET AT CORRECT POSITION
        return;
    }

}
