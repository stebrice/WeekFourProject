package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
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
 * Created by STEPHAN987 on 8/13/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>  {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImageU;
        public TextView tvUserNameU;
        public TextView tvDescriptionU;
        public TextView tvScreenNameU;
        public ToggleButton tbFollow;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivProfileImageU = (ImageView) itemView.findViewById(R.id.ivProfileImageU);
            tvUserNameU = (TextView) itemView.findViewById(R.id.tvUserNameU);
            tvDescriptionU = (TextView) itemView.findViewById(R.id.tvDescriptionU);
            tvScreenNameU = (TextView) itemView.findViewById(R.id.tvScreenNameU);
            tbFollow = (ToggleButton) itemView.findViewById(R.id.tbFollow);
        }
    }


    // Store a member variable for the contacts
    private List<User> mUsers;
    // Store the context for easy access
    private Context mContext;
    private TwitterClient client;
    private ProfileActivity pActivity;
    // Pass in the contact array into the constructor
    public UsersAdapter(Context context, List<User> users) {
        this.mUsers = users;
        this.mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }



    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_user, parent, false);

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
    public void onBindViewHolder(UsersAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        User user = mUsers.get(position);

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.tvDescriptionU.setText(user.getTagLine());
        viewHolder.tvScreenNameU.setText(user.getScreenName());
        viewHolder.tvUserNameU.setText(user.getName());
        viewHolder.tbFollow.setChecked(user.isFollowing());

        viewHolder.ivProfileImageU.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(user.getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(getContext(),5,5))).into(viewHolder.ivProfileImageU);

        if (pActivity == null){
            viewHolder.ivProfileImageU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User userToUse = mUsers.get(position);
                    manageImageClick(userToUse);
                }
            });
        }

    }

    private void manageImageClick(User userToUse) {
        setUserAccountInfos(userToUse.getScreenName());
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
        return mUsers.size();
    }

    // Returns the item at the specified position
    public User getItem(int position) {
        return mUsers.get(position);
    }

}
