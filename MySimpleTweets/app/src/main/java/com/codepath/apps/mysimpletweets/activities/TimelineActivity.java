package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.SmartFragmentStatePagerAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity{
    private User user;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();
        try{
            client.checkConnection();
            // get current user Account infos
            client.getAccountInformation(new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //Toast.makeText(getApplicationContext(),"SUCCESS!", Toast.LENGTH_LONG).show();
                    user = User.fromJSON(response);
                    //GET VIEW PAGER
                    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                    //SET ViewPagerAdapter
                    viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
                    //FIND SlidingTabs
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                    //ATTACH TAB LAYOUT TO THE VIEWPAGER
                    tabLayout.setupWithViewPager(viewPager);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_bar_timeline, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.miProfile);
        // Fetch reference to the share action provider

//        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        //shareIntent.setType("text/plain");
//        shareIntent.setType("message/rfc822");
//
//        // pass in the URL currently being used by the WebView
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getHeadline());
//        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());
//
//        miShareAction.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(TimelineActivity.this, ProfileActivity.class);
        i.putExtra("current_user_to_use", Parcels.wrap(user));
        startActivity(i);
    }

    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return HomeTimelineFragment.newInstance(user);
            } else if (position == 1) {
                return MentionsTimelineFragment.newInstance(user);
            } else{
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
