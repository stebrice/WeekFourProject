package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.utils.RoundedCornersTransformation;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private User user;
    private TwitterClient client;
    private Tweet lastTweet;


    ImageButton ibCancel;
    ImageView ivProfileImageTw;
    Button btTweet;
    EditText etCompose;
    TextView tvMessage;
    TextView tvUserNameCompose;
    TextView tvScreenNameCompose;
    TextView tvRemainingChars;
    long currentCharCount = 0;
    final long maxChars = 140;
    long charsLeft = 140;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_compose);
        //setSupportActionBar(toolbar);

        user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        client = TwitterApplication.getRestClient();
        try{
            client.checkConnection();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        lastTweet = new Tweet();

        setupViews();
    }

    private void setupViews(){
        ibCancel = (ImageButton) findViewById(R.id.ibCancel);
        ivProfileImageTw = (ImageView) findViewById(R.id.ivProfileImageTw);
        btTweet = (Button) findViewById(R.id.btTweet);
        etCompose = (EditText) findViewById(R.id.etCompose);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvUserNameCompose = (TextView) findViewById(R.id.tvUserNameCompose);
        tvScreenNameCompose = (TextView) findViewById(R.id.tvScreenNameCompose);
        tvRemainingChars = (TextView) findViewById(R.id.tvRemainingChars);
        tvRemainingChars.setText(String.valueOf(maxChars));
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentCharCount = s.toString().length();
                charsLeft = maxChars - currentCharCount;

                if(charsLeft < 0)
                {
                    btTweet.setEnabled(false);
                    tvRemainingChars.setTextColor(Color.RED);
                    tvMessage.setText("Maximum 140 characters allowed!");
                }
                else
                {
                    btTweet.setEnabled(true);
                    tvRemainingChars.setTextColor(Color.BLACK);
                    tvMessage.setText("");
                }
                tvRemainingChars.setText(String.valueOf(charsLeft));
            }
        });

        tvScreenNameCompose.setText(user.getScreenName());
        tvUserNameCompose.setText(user.getName());
        //Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).transform(new RoundedCornersTransformation(5,5)).into(ivProfileImageTw);
        Glide.with(this).load(user.getProfileImageUrl()).apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(this,5,5))).into(ivProfileImageTw);

    }

    public void onCancelTweet(View view) {
        finish();
    }

    public void onPostTweet(View view) {
        //Toast.makeText(getApplicationContext(),"Post attempt", Toast.LENGTH_LONG).show();
       client.postStatusUpdate(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                lastTweet = Tweet.fromJSON(response);
                Log.d("DEBUG", response.toString());
                Intent data = new Intent();
                data.putExtra("new_tweet", Parcels.wrap(lastTweet));

                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(),errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        }, etCompose.getText().toString());

    }
}
