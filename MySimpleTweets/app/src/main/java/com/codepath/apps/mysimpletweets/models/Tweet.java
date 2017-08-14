package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by STEPHAN987 on 8/5/2017.
 * "text": "just another test",
 "contributors": null,
 "id": 240558470661799936,
 "retweet_count": 0,
 "in_reply_to_status_id_str": null,
 "geo": null,
 "retweeted": false,
 "in_reply_to_user_id": null,
 "place": null,
 "source": "OAuth Dancer Reborn",
 "user": {
 "name": "OAuth Dancer",
 "profile_sidebar_fill_color": "DDEEF6",
 "profile_background_tile": true,
 "profile_sidebar_border_color": "C0DEED",
 "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
 "created_at": "Wed Mar 03 19:37:35 +0000 2010",
 "location": "San Francisco, CA",
 "follow_request_sent": false,
 "id_str": "119476949",
 "is_translator": false,
 "profile_link_color": "0084B4",
 "entities": {
 "url": {
 "urls": [
 {
 "expanded_url": null,
 "url": "http://bit.ly/oauth-dancer",
 "indices": [
 0,
 26
 ],
 "display_url": null
 }
 ]
 },
 "description": null
 }
 */

@Parcel
public class Tweet {
    public String body;
    public long uid; ///tweet id
    public User user;
    public String createdAt;
    public long retweetCount; ///retweet_count
    public long favouritesCount; ///favourites_count
    public boolean retweeted; ///retweeted
    public boolean favorited; ///favorited


    public Tweet() {
    }

    public Tweet(String body, long uid, User user, String createdAt, long retweetCount, long favouritesCount) {
        this.body = body;
        this.uid = uid;
        this.user = user;
        this.createdAt = createdAt;
        this.retweetCount = retweetCount;
        this.favouritesCount = favouritesCount;
    }

    public Tweet(String body, long uid, User user, String createdAt, long retweetCount, long favouritesCount, boolean retweeted, boolean favorited ) {
        this.body = body;
        this.uid = uid;
        this.user = user;
        this.createdAt = createdAt;
        this.retweetCount = retweetCount;
        this.favouritesCount = favouritesCount;
        this.retweeted = retweeted;
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public long getFavouritesCount() {
        return favouritesCount;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getTweetAge() {
        String retVal = getRelativeTimeAgo(createdAt);
        retVal = retVal.replace(" second ago", "s");
        retVal = retVal.replace(" seconds ago", "s");
        retVal = retVal.replace(" minute ago", "m");
        retVal = retVal.replace(" minutes ago", "m");
        retVal = retVal.replace(" hour ago", "h");
        retVal = retVal.replace(" hours ago", "h");
        retVal = retVal.replace(" day ago", "d");
        retVal = retVal.replace(" days ago", "d");
        retVal = retVal.replace(" week ago", "w");
        retVal = retVal.replace(" weeks ago", "w");
        retVal = retVal.replace(" year ago", "y");
        retVal = retVal.replace(" years ago", "y");
        retVal = retVal.replace("Yesterday", "1d");

        return retVal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();

        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.retweetCount = jsonObject.getLong("retweet_count");
            tweet.favouritesCount = jsonObject.getLong("favorite_count");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.favorited = jsonObject.getBoolean("favorited");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray){
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject tweetJSON = jsonArray.getJSONObject(i);
                    Tweet tweet = Tweet.fromJSON(tweetJSON);
                    if(tweet != null){
                        tweets.add(tweet);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
            }

        return tweets;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

            if (!(relativeDate).contains(" ago")){
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS).toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setFavouritesCount(long favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}
