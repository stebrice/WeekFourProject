package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "Tymzw15CPvuVSMX03vsC1bTeA";       // Change this
	public static final String REST_CONSUMER_SECRET = "tgsxiwdyQaV6QfKsv0I2tJvfxpPSzBFZtTAmBit9evun0860rw"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	//public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";
	public static final String FALLBACK_URL = "oauth://sbsimpletweets";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//HOME TIMELINE METHOD
    //-----GET HOME TIMELINE
	//JSON URL : GET statuses/home_timeline.json
	//		count = 25
	//      since_id=1

	public void checkConnection() throws Exception {
		if (!isOnline() || !isNetworkAvailable()) {
			throw new Exception("Internet access unavailable. Please check your connection.");
		}
	}

	private Boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public boolean isOnline() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int     exitValue = ipProcess.waitFor();
			return (exitValue == 0);
		} catch (IOException e)          { e.printStackTrace(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		return false;
	}
	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getHomeTimelineFromMaxID(AsyncHttpResponseHandler handler, long maxID) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getHomeTimelineFromSinceID(AsyncHttpResponseHandler handler, long sinceID) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", sinceID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getAccountInformation(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getAccountInformationForUser(AsyncHttpResponseHandler handler, String screenName) {
		screenName = screenName.replace("@", "");
		String apiUrl = getApiUrl("users/show.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.add("screen_name", screenName);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void postStatusUpdate(AsyncHttpResponseHandler handler, String tweetMessage) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.add("status", tweetMessage);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	public void postStatusUpdateInReplyTo(AsyncHttpResponseHandler handler, String tweetMessage, long inReplyTo) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.add("status", tweetMessage);
		params.add("in_reply_to_status_id", String.valueOf(inReplyTo));
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	public void postRetweet(AsyncHttpResponseHandler handler, long tweetID) {
		String apiUrl = getApiUrl("statuses/retweet/" + String.valueOf(tweetID) + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	public void postUnRetweet(AsyncHttpResponseHandler handler, long tweetID) {
		String apiUrl = getApiUrl("statuses/unretweet/" + String.valueOf(tweetID) + ".json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	public void postFavoriteTweet(AsyncHttpResponseHandler handler, long tweetID) {
		String apiUrl = getApiUrl("favorites/create.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.add("id", String.valueOf(tweetID));
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	public void postUnFavoriteTweet(AsyncHttpResponseHandler handler, long tweetID) {
		String apiUrl = getApiUrl("favorites/destroy.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.add("id", String.valueOf(tweetID));
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.post(apiUrl, params, handler);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//MENTIONS TIMELINE METHOD
	//-----GET MENTIONS TIMELINE
	//JSON URL : GET statuses/mentions_timeline.json
	//		count = 25
	//      since_id=1

	public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimelineFromMaxID(AsyncHttpResponseHandler handler, long maxID) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getMentionsTimelineFromSinceID(AsyncHttpResponseHandler handler, long sinceID) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", sinceID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}



	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//USER TIMELINE METHOD
	//-----GET USER TIMELINE
	//JSON URL : GET statuses/user_timeline.json
	//		count = 25
	//      since_id=1

	public void getUserTimeline(AsyncHttpResponseHandler handler, long userID) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("user_id", userID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getUserTimelineFromMaxID(AsyncHttpResponseHandler handler, long userID, long maxID) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("user_id", userID);
		params.put("max_id", maxID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getUserTimelineFromSinceID(AsyncHttpResponseHandler handler, long userID, long sinceID) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("user_id", userID);
		params.put("since_id", sinceID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}


	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//DIRECT MESSAGES TIMELINE METHOD
	//-----GET DIRECT MESSAGES TIMELINE
	//JSON URL : GET statuses/direct_messages.json
	//		count = 25
	//      since_id=1

	public void getDirectMessagesTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getDirectMessagesTimelineFromMaxID(AsyncHttpResponseHandler handler, long maxID) {
		String apiUrl = getApiUrl("statuses/direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getDirectMessagesTimelineFromSinceID(AsyncHttpResponseHandler handler, long sinceID) {
		String apiUrl = getApiUrl("statuses/direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", sinceID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//FRIENDS TIMELINE METHOD
	//JSON URL : GET friends/list.json
	//		count = 25

	public void getFollowingTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getFollowingTimelineFromCursor(AsyncHttpResponseHandler handler, long cursorID) {
		String apiUrl = getApiUrl("statuses/direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("cursor", cursorID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	//FOLLOWERS TIMELINE METHOD
	//JSON URL : GET followers/list.json
	//		count = 25
	//      cursor=1

	public void getFollowersTimeline(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	public void getFollowersTimelineFromCursor(AsyncHttpResponseHandler handler, long cursorID) {
		String apiUrl = getApiUrl("statuses/direct_messages.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("cursor", cursorID);
		try{
			checkConnection();
		}
		catch (Exception e){
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		client.get(apiUrl, params, handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
