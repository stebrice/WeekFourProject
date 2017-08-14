package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TimelineActivity;
import com.codepath.apps.mysimpletweets.fragments.ComposeFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.PatternEditableBuilder;
import com.codepath.apps.mysimpletweets.utils.RoundedCornersTransformation;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by STEPHAN987 on 8/6/2017.
 */

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public RelativeLayout relLayoutToTest;
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvCreatedAt;
        public TextView tvScreenName;
        public LinearLayout llReplyIT;
        public LinearLayout llRetweetIT;
        public LinearLayout llLikesIT;
        public TextView tvReplyCount;
        public TextView tvRetweetCount;
        public TextView tvLikesCount;
        public ImageButton ibMessageIT;
        public ImageView ivRetweet;
        public ImageView ivLike;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            relLayoutToTest = (RelativeLayout) itemView.findViewById(R.id.relLayoutToTest);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            llReplyIT = (LinearLayout) itemView.findViewById(R.id.llReplyIT);
            llRetweetIT = (LinearLayout) itemView.findViewById(R.id.llRetweetIT);
            llLikesIT = (LinearLayout) itemView.findViewById(R.id.llLikesIT);
            ibMessageIT = (ImageButton) itemView.findViewById(R.id.ibMessageIT);
            tvReplyCount = (TextView) itemView.findViewById(R.id.tvReplyCount);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvLikesCount = (TextView) itemView.findViewById(R.id.tvLikesCount);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);

        }
    }


    // Store a member variable for the contacts
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
    private TwitterClient client;
    private ProfileActivity pActivity;
    // Pass in the contact array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.mTweets = tweets;
        this.mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }



    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        client = TwitterApplication.getRestClient();
        pActivity = null;
        try{
            pActivity = (ProfileActivity) context;
        } catch (Exception e)
        {

        }
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final TweetsAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvCreatedAt.setText(tweet.getTweetAge());

        viewHolder.tvReplyCount.setText("");

        setLikedRetweetedValues(viewHolder,tweet);

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(getContext(),5,5))).into(viewHolder.ivProfileImage);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.parseColor("#ff00ddff"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                manageClickedSpan(1, text);

                            }
                        }).
                addPattern(Pattern.compile("\\#(\\w+)"), Color.parseColor("#ff00ddff"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                manageClickedSpan(2, text);

                            }
                        }).into(viewHolder.tvBody);


        if (pActivity == null){
            viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tweetToUse = mTweets.get(position);
                    manageImageClick(tweetToUse);
                }
            });
        }

        viewHolder.llReplyIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tweet tweetToUse = mTweets.get(position);
                replyToCurrentTweet(tweetToUse);
                //TweetsAdapter.this.notifyItemChanged(position);
            }
        });

        viewHolder.llRetweetIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tweet tweetToUse = mTweets.get(position);
                retweetUnRetweet(position,viewHolder, tweetToUse);
                //TweetsAdapter.this.notifyItemChanged(position);
            }
        });

        viewHolder.llLikesIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tweet tweetToUse = mTweets.get(position);
                favoriteUnFavorite(position,viewHolder,tweetToUse);
                //TweetsAdapter.this.notifyItemChanged(position);
            }
        });

    }

    private void manageImageClick(Tweet tweetToUse) {
        setUserAccountInfos(tweetToUse.getUser().getScreenName());
        //showUserProfile(user.getScreenName());
    }

    private void showUserProfile(User userToView) {
        if (userToView != null || userToView.getUid() != 0) {
            Intent i = new Intent(mContext, ProfileActivity.class);
            i.putExtra("current_user_to_use", Parcels.wrap(userToView));
            mContext.startActivity(i);
        }
        else{
            Toast.makeText(getContext(),"Unable to retrieve user", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, screenName);

    }


    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Returns the item at the specified position
    public Tweet getItem(int position) {
        return mTweets.get(position);
    }

    private void manageClickedSpan(int userOrHashtag, String value)
    {
        //userOrHashtag values : 1 for usernames staring with @ ; and 2 for hashtags starting with #
        if (userOrHashtag == 1){
            setUserAccountInfos(value);
            //Toast.makeText(mContext, "Clicked username: " + value,
            //       Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mContext, "Clicked hashtag: " + value,
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void replyToCurrentTweet(Tweet currentTweetToUse) {
        final Tweet tweetToUse = currentTweetToUse;
        client.getAccountInformation(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getApplicationContext(),"SUCCESS!", Toast.LENGTH_LONG).show();
                User userToUse = User.fromJSON(response);
                Tweet newTweet = new Tweet("",0,null,"",0,0);
                FragmentManager fm = ((TimelineActivity) mContext).getSupportFragmentManager();
                Fragment currentFragment = fm.findFragmentById(R.id.viewpager);
                ComposeFragment composeFragment = ComposeFragment.newInstance(userToUse, newTweet, tweetToUse);
                if (currentFragment instanceof HomeTimelineFragment){
                    composeFragment.setTargetFragment((HomeTimelineFragment) currentFragment, 300);
                }
                else if (currentFragment instanceof MentionsTimelineFragment){
                    composeFragment.setTargetFragment((MentionsTimelineFragment) currentFragment, 300);
                }
                else if (currentFragment instanceof UserTimelineFragment){
                    composeFragment.setTargetFragment((UserTimelineFragment) currentFragment, 300);
                }

                composeFragment.show(fm, "fragment_compose");
                //showUserProfile(userToUse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void retweetUnRetweet(final int position, final ViewHolder vhTest, final Tweet tweet) {
        if (tweet.isRetweeted()){
            client.postUnRetweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);
                    tweet.setRetweetCount(tweetResult.getRetweetCount());
                    tweet.setRetweeted(tweetResult.isRetweeted());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(vhTest, tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
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
                    setLikedRetweetedValues(vhTest, tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }

    }

    public void favoriteUnFavorite(final int position, final ViewHolder vhTest, final Tweet tweet) {
        if (tweet.isFavorited()){
            client.postUnFavoriteTweet(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweetResult = Tweet.fromJSON(response);
                    //setLikedRetweetedValues(vhTest, tweetResult);
                    //mTweets.set(position,tweetResult);
                    tweet.setFavouritesCount(tweetResult.getFavouritesCount());
                    tweet.setFavorited(tweetResult.isFavorited());
                    //mTweets.set(position,tweet);
                    setLikedRetweetedValues(vhTest, tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
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
                    setLikedRetweetedValues(vhTest, tweet);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            }, tweet.getUid());
        }

    }

    private void setLikedRetweetedValues(final ViewHolder vhTest, final Tweet tweetToUse) {
        if (tweetToUse.isRetweeted()){
            vhTest.ivRetweet.setImageResource(R.drawable.ic_retweeted);
            vhTest.tvRetweetCount.setTextColor(Color.parseColor("#ff33b5e5"));
        }
        else{
            vhTest.ivRetweet.setImageResource(R.drawable.ic_retweet);
            vhTest.tvRetweetCount.setTextColor(Color.BLACK);

        }
        if (tweetToUse.isFavorited()){
            vhTest.ivLike.setImageResource(R.drawable.ic_liked);
            vhTest.tvLikesCount.setTextColor(Color.parseColor("#ff33b5e5"));
        }
        else{
            vhTest.ivLike.setImageResource(R.drawable.ic_like);
            vhTest.tvLikesCount.setTextColor(Color.BLACK);
        }
        vhTest.tvRetweetCount.setText(String.valueOf(tweetToUse.getRetweetCount()));
        vhTest.tvLikesCount.setText(String.valueOf(tweetToUse.getFavouritesCount()));
    }
}