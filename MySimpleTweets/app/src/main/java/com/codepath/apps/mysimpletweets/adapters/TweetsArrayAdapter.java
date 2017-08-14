package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.RoundedCornersTransformation;

import java.util.List;

import static com.codepath.apps.mysimpletweets.R.id.ivProfileImage;
import static com.codepath.apps.mysimpletweets.R.id.tvBody;
import static com.codepath.apps.mysimpletweets.R.id.tvScreenName;

/**
 * Created by STEPHAN987 on 8/5/2017.
 */

    public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {


        // View lookup cache
        private static class ViewHolder {
            ImageView ivProfileImage;
            TextView tvUserName;
            TextView tvBody;
            TextView tvCreatedAt;
            TextView tvScreenName;
        }


        public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
            super(context,0, tweets);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Tweet tweet = getItem(position);

            ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);

                viewHolder.ivProfileImage = (ImageView) convertView.findViewById(ivProfileImage);
                viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
                viewHolder.tvBody = (TextView) convertView.findViewById(tvBody);
                viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
                viewHolder.tvScreenName = (TextView) convertView.findViewById(tvScreenName);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);

            }
            else
            {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.tvBody.setText(tweet.getBody());
            viewHolder.tvScreenName.setText(tweet.getUser().getScreenName());
            viewHolder.tvUserName.setText(tweet.getUser().getName());
            viewHolder.tvCreatedAt.setText(tweet.getTweetAge());

            viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
            Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(getContext(),5,5))).into(viewHolder.ivProfileImage);

            // Return the completed view to render on screen
            return convertView;

        }
}
