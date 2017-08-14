package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.FollowersTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.FollowingTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;

import org.parceler.Parcels;

public class FollowersFollowingsActivity extends AppCompatActivity {
    private User user;
    private int listToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_followings);
        TwitterClient client = TwitterApplication.getRestClient();
        try{
            client.checkConnection();
            user = (User) Parcels.unwrap(getIntent().getParcelableExtra("current_user_to_use"));
            listToUse = getIntent().getIntExtra("list_to_use",0);
            if (savedInstanceState == null) {
                if (listToUse == 2) {
                    FollowingTimelineFragment followingTimelineFragment = FollowingTimelineFragment.newInstance(user);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.flFollowList, followingTimelineFragment);
                    ft.commit();
                }
                else if (listToUse == 1)
                {
                    FollowersTimelineFragment followersTimelineFragment = FollowersTimelineFragment.newInstance(user);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.flFollowList, followersTimelineFragment);
                    ft.commit();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Unable to load required list", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}
