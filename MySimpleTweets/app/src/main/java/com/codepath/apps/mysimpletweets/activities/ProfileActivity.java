package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TwitterClient client = TwitterApplication.getRestClient();
        try{
            client.checkConnection();
            user = (User) Parcels.unwrap(getIntent().getParcelableExtra("current_user_to_use"));
            getSupportActionBar().setTitle(user.getScreenName());
            populateUserHeader(user);
            if (savedInstanceState == null) {
                UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(user);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.flUserTimeline, userTimelineFragment);
                ft.commit();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void populateUserHeader(User user) {
        TextView tvUserNameUP = (TextView) findViewById(R.id.tvUserNameUP);
        TextView tvTagline = (TextView) findViewById(R.id.tvBodyUP);
        ImageView ivProfileImageUP = (ImageView) findViewById(R.id.ivProfileImageUP);
        TextView tvFollowersUP = (TextView) findViewById(R.id.tvFollowersUP);
        TextView tvFollowingUP = (TextView) findViewById(R.id.tvFollowingUP);

        tvUserNameUP.setText(user.getName());
        tvTagline.setText(user.getTagLine());
        tvFollowersUP.setText(String.valueOf(user.getFollowersCount()) + " Followers");
        tvFollowingUP.setText(String.valueOf(user.getFriendsCount()) + " Following");
        Glide.with(getApplicationContext()).load(user.getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(getApplicationContext(), 5, 5))).into(ivProfileImageUP);

        tvFollowingUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 Followers ; 2 Following
                showListToUse(2);
            }
        });

        tvFollowersUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 Followers ; 2 Following
                showListToUse(1);
            }
        });
    }

    private void showListToUse(int listToShow) {
        Intent i = new Intent(ProfileActivity.this, FollowersFollowingsActivity.class);
        i.putExtra("current_user_to_use", Parcels.wrap(user));
        i.putExtra("list_to_use", listToShow);
        startActivity(i);
    }

}
