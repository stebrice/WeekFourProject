package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by STEPHAN987 on 8/13/2017.
 */

@Parcel
public class FriendFollowerModel {
    public long previousCursor;
    public long nextCursor;
    public ArrayList<User> users;

    public long getPreviousCursor() {
        return previousCursor;
    }

    public long getNextCursor() {
        return nextCursor;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public FriendFollowerModel() {
    }

    public FriendFollowerModel(long previousCursor, long nextCursor, ArrayList<User> users) {
        this.previousCursor = previousCursor;
        this.nextCursor = nextCursor;
        this.users = users;
    }

    public static FriendFollowerModel fromJSON(JSONObject jsonObject){
        FriendFollowerModel friendFollowerModel = new FriendFollowerModel();

        try {
            friendFollowerModel.previousCursor = jsonObject.getLong("previous_cursor");
            friendFollowerModel.nextCursor = jsonObject.getLong("next_cursor");
            friendFollowerModel.users = User.fromJSONArray(jsonObject.getJSONArray("users"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return friendFollowerModel;
    }
}
