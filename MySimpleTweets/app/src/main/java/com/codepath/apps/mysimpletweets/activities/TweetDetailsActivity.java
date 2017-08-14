package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ComposeFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.PatternEditableBuilder;
import com.codepath.apps.mysimpletweets.utils.RoundedCornersTransformation;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity implements ComposeFragment.ComposeDialogListener{

    private TextView tvScreenNameDt;
    private TextView tvUserNameDt;
    private TextView tvBodyDt;
    private TextView tvCreatedAtDt;
    private TextView tvRetweetsDt;
    private TextView tvLikesDt;
    private ImageView ivProfileImageDt;
    private ImageButton ibRetweet;
    private ImageButton ibLike;

    private Tweet currentTweet;
    private Tweet newTweet;
    private User connectedUser;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        client = TwitterApplication.getRestClient();

        try{
            client.checkConnection();
            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details);
            //setSupportActionBar(toolbar);
            newTweet = new Tweet("",0,null,"",0,0);
            currentTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("current_tweet"));
            connectedUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("connected_user"));

            try{
                client.checkConnection();
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
            setupViews();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void setupViews() {
        tvScreenNameDt = (TextView) findViewById(R.id.tvScreenNameDt);
        tvUserNameDt = (TextView) findViewById(R.id.tvUserNameDt);
        tvBodyDt = (TextView) findViewById(R.id.tvBodyDt);
        ivProfileImageDt = (ImageView) findViewById(R.id.ivProfileImageDt);
        tvCreatedAtDt = (TextView) findViewById(R.id.tvCreatedAtDt);
        tvRetweetsDt = (TextView) findViewById(R.id.tvRetweetsDt);
        tvLikesDt = (TextView) findViewById(R.id.tvLikesDt);

        ibRetweet = (ImageButton)  findViewById(R.id.ibRetweet);
        ibLike = (ImageButton)  findViewById(R.id.ibLike);

        tvScreenNameDt.setText(currentTweet.getUser().getScreenName());
        tvUserNameDt.setText(currentTweet.getUser().getName());
        tvBodyDt.setText(currentTweet.getBody());

        setLikedRetweetedValues(currentTweet);

        String dateFormatted = "";
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(twitterFormat);
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("h:mm a, dd MMM yy");
        try {
            Log.d("DEBUG", currentTweet.getCreatedAt());
            Date d = (Date) dateFormat.parse(currentTweet.getCreatedAt());
            dateFormatted = dateFormat1.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvCreatedAtDt.setText(dateFormatted);

        Glide.with(this).load(currentTweet.getUser().getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(getApplicationContext(),5,5))).into(ivProfileImageDt);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.parseColor("#ff00ddff"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                manageClickedSpan(1,text);
                            }
                        }).
                addPattern(Pattern.compile("\\#(\\w+)"), Color.parseColor("#ff00ddff"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                manageClickedSpan(2,text);
                            }
                        }).into(tvBodyDt);
    }

    private void showUserProfile(User userToView) {
        if (userToView != null || userToView.getUid() != 0) {
            Intent i = new Intent(TweetDetailsActivity.this, ProfileActivity.class);
            i.putExtra("current_user_to_use", Parcels.wrap(userToView));
            startActivity(i);
        }
        else{
            Toast.makeText(getApplicationContext(),"Unable to retrieve user", Toast.LENGTH_LONG).show();
        }
    }

    private void setUserAccountInfos(String screenName){
        // get current user Account infos
        client.getAccountInformationForUser(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getApplicationContext(),"SUCCESS!", Toast.LENGTH_LONG).show();
                User userToUse = User.fromJSON(response);
                showUserProfile(userToUse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, screenName);

    }

    private void setLikedRetweetedValues(final Tweet tweetToUse) {
        if (tweetToUse.isRetweeted()){
            ibRetweet.setImageResource(R.drawable.ic_retweeted);
            tvRetweetsDt.setTextColor(Color.parseColor("#ff33b5e5"));
        }
        else{
            ibRetweet.setImageResource(R.drawable.ic_retweet);
            tvRetweetsDt.setTextColor(Color.BLACK);
        }
        if (tweetToUse.isFavorited()){
            ibLike.setImageResource(R.drawable.ic_liked);
            tvLikesDt.setTextColor(Color.parseColor("#ff33b5e5"));
        }
        else{
            ibLike.setImageResource(R.drawable.ic_like);
            tvLikesDt.setTextColor(Color.BLACK);
        }
        tvRetweetsDt.setText(String.valueOf(tweetToUse.getRetweetCount()));
        tvLikesDt.setText(String.valueOf(tweetToUse.getFavouritesCount()));

        currentTweet = tweetToUse;
    }

    public void replyToTweet(View view) {
        showCompose();
    }

    public void showCompose() {
//        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
//        i.putExtra("user", Parcels.wrap(user));
//        startActivityForResult(i, REQUEST_CODE);
        newTweet = new Tweet("",0,null,"",0,0);
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(connectedUser, newTweet, currentTweet, true);
        composeFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onFinishComposeDialog(Tweet tweetDefined) {
        if (tweetDefined.getUid() > 0){
            Toast.makeText(getApplicationContext(),"Replied successfully to " + currentTweet.getUser().getScreenName(), Toast.LENGTH_LONG).show();
            //    insertNewTweetFromReplyAndUpdateFeed(); //INSERT NEW TWEET AT CORRECT POSITION
            Intent data = new Intent();
            data.putExtra("new_tweet_from_reply", Parcels.wrap(tweetDefined));

            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish(); // closes the activity, pass data to parent
        }
        return;
    }

    private void manageClickedSpan(int userOrHashtag, String value)
    {
        //userOrHashtag values : 1 for usernames staring with @ ; and 2 for hashtags starting with #
        if (userOrHashtag == 1){
            setUserAccountInfos(value);
        }
        else {
            Toast.makeText(getApplicationContext(), "Clicked hashtag: " + value,
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void reTweet(View view) {
        final Tweet tweet = currentTweet;
        if (tweet.isRetweeted()){
            client.postUnRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);
                    tweet.setRetweetCount(tweetResult.getRetweetCount());
                    tweet.setRetweeted(tweetResult.isRetweeted());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }
        else
        {
            client.postRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);
                    tweet.setRetweetCount(tweetResult.getRetweetCount());
                    tweet.setRetweeted(tweetResult.isRetweeted());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }
    }

    public void likeTweet(View view) {
        final Tweet tweet = currentTweet;
        if (tweet.isFavorited()){
            client.postUnFavoriteTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);

                    tweet.setFavouritesCount(tweetResult.getFavouritesCount());
                    tweet.setFavorited(tweetResult.isFavorited());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }
        else
        {
            client.postFavoriteTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);
                    tweet.setFavouritesCount(tweetResult.getFavouritesCount());
                    tweet.setFavorited(tweetResult.isFavorited());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }
    }
}
