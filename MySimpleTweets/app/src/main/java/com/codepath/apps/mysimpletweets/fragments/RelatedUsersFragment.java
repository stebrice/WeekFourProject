package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.UsersAdapter;
import com.codepath.apps.mysimpletweets.customlistener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by STEPHAN987 on 8/13/2017.
 */

public class RelatedUsersFragment extends Fragment {
    private ArrayList<User> users;
    //private UsersArrayAdapter aUsers;
    private UsersAdapter aUsers;
    //private ListView lvUsers;
    private RecyclerView rvUsers;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    private LinearLayoutManager linearLayoutManager;

    private SwipeRefreshLayout swipeContainerUsers;

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public RecyclerView getRvUsers() {
        return rvUsers;
    }

    public UsersAdapter getAUsers() {
        return aUsers;
    }

    public EndlessRecyclerViewScrollListener getScrollListener() {
        return scrollListener;
    }

    public SwipeRefreshLayout getSwipeContainerUsers() {
        return swipeContainerUsers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_list, parent, false);
        rvUsers = (RecyclerView) v.findViewById(R.id.rvUsers);

        rvUsers.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        rvUsers.setAdapter(aUsers);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvUsers.setLayoutManager(linearLayoutManager);

        //PULL TO REFRESH
        // Lookup the swipe container view
        swipeContainerUsers = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainerUsers);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users = new ArrayList<>();
        //aUsers = new UsersArrayAdapter(this, users);
        aUsers = new UsersAdapter(getActivity(), users);
    }

    public void addPulledData(ArrayList<User> items, boolean resetScrollToStart){
        int currentSize = 0;
        if (!resetScrollToStart){
            currentSize = aUsers.getItemCount();
        }
        users.addAll(items);
        aUsers.notifyItemRangeInserted(currentSize, items.size());
        if (resetScrollToStart){
            rvUsers.scrollToPosition(0);   // index 0 position
        }
    }

    public void insertNewSingleItem(User newUser){
        int indexToUse = 0;
        for (int i = 0; i < aUsers.getItemCount(); i++){
            User retrievedUser = aUsers.getItem(i);
            if (retrievedUser.getUid() < newUser.getUid()){
                indexToUse = i;
                break;
            }
        }
        //aUsers.insert(newUser,indexToUse);
        users.add(indexToUse,newUser);
        aUsers.notifyItemInserted(indexToUse);
        rvUsers.scrollToPosition(indexToUse);   // index 0 position
    }
}
