package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.FollowersFollowingsActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.customlistener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.FriendFollowerModel;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by STEPHAN987 on 8/13/2017.
 */

public class FollowingTimelineFragment extends RelatedUsersFragment {
    private TwitterClient client;
    private User user;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainerUsers;
    private final int REQUEST_CODE = 10;

    private FriendFollowerModel friendFollowerModel;

    public static FollowingTimelineFragment newInstance(User user) {

        Bundle args = new Bundle();

        FollowingTimelineFragment fragment = new FollowingTimelineFragment();
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
            swipeContainerUsers = getSwipeContainerUsers();

            scrollListener = new EndlessRecyclerViewScrollListener(getLinearLayoutManager()) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    long nextCursor = friendFollowerModel.getNextCursor();
                    if (nextCursor > 1){
                        populateTimelineFromCursor(nextCursor);
                    }
                    return;
                }
            };
            super.getRvUsers().addOnScrollListener(scrollListener);

            // Setup refresh listener which triggers new data loading
            swipeContainerUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainerUsers.setRefreshing(false)
                    // once the network request has completed successfully.
                    //sinceID = getSinceID();
                    long nextCursor = -1;
                    populateTimelineFromCursor(nextCursor);
                    swipeContainerUsers.setRefreshing(false);
                }
            });
            // Configure the refreshing colors
            swipeContainerUsers.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);


            ItemClickSupport.addTo(getRvUsers()).setOnItemClickListener(
                    new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            User currentUser = getAUsers().getItem(position);
                            showUserDetails(currentUser);
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
            // get current user Account infos
            user = (User) Parcels.unwrap(getArguments().getParcelable("current_user_to_use"));
            friendFollowerModel = new FriendFollowerModel();

            ((FollowersFollowingsActivity) getActivity()).setActionBarTitle("Following");

            populateTimeline();
        }
        catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void populateTimeline(){
        client.getFollowingTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getApplicationContext(),"SUCCESS!", Toast.LENGTH_LONG).show();
                friendFollowerModel = FriendFollowerModel.fromJSON(response);
                ArrayList<User> newItems = friendFollowerModel.getUsers();
                try {
                    addPulledData(newItems, false);
                    //Toast.makeText(getContext(), String.valueOf(rvUsers.getAdapter().getItemCount()) + " items added",Toast.LENGTH_LONG).show();
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

    private void populateTimelineFromCursor(long maxToUse){
        client.getFollowingTimelineFromCursor(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                friendFollowerModel = FriendFollowerModel.fromJSON(response);
                ArrayList<User> newItems = friendFollowerModel.getUsers();
                addPulledData(newItems, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, maxToUse);
    }

    public void showUserDetails(User userToUse) {
        Intent i = new Intent(getActivity(), ProfileActivity.class);
        i.putExtra("current_user_to_use", Parcels.wrap(userToUse));
        startActivity(i);
    }
}
