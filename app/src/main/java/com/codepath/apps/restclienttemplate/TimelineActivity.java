package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity{
    //make a debug custom tag
    public static final String TAG = "TimelineActivity";
    public final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;
    public long max_id;

    //endless scrolling
    private EndlessRecyclerViewScrollListener scrollListener;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button btnReply;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        //find recycler view
        rvTweets = findViewById(R.id.rvTweets);

        btnReply = findViewById(R.id.btnReply);

        //initialize tweets in adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        //configure recycler view : layout manager and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);
        rvTweets.addItemDecoration(new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL));
        populateHomeTimeline();

        //Add endless scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);



        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    //used to load next data for endless scrolling
    private void loadNextDataFromApi() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        client.getNextTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("AddMore", "+ 25");
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                    //add tweets pulled
                    tweets.addAll(newTweets);
                    max_id = tweets.get(tweets.size()-1).getId();
                    //notify the adapter that the tweets were added
                    adapter.notifyDataSetChanged();
                    //update the max id for tweets
                } catch (JSONException e) {
                    Log.e("AddMore", "json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + response, throwable);
            }
        }, max_id
        );
    }


    //When the options menu is created, inflate the menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //When options item is selected, make sure it is the compose item, and open compose activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose){
            //Compose icon is tapped
            //navigate to the compose activity
            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        //if logout button selected, logout the user
        if (item.getItemId() == R.id.logOut){
            client.clearAccessToken();
            Intent i = new Intent(TimelineActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        //Make sure it is returning the same request we made earlier, and the result is ok
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //get data from the intent and unwrap parcel
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //update the recycler view with the new tweet
            //modify data source
            tweets.add(0, tweet);
            //update the adapter
            adapter.notifyItemInserted(0);
            //scroll to the top of the recycler view
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Get all Tweets, and notify the adapter that they have been added
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
                    //add tweets pulled
                    tweets.addAll(newTweets);
                    max_id = tweets.get(tweets.size()-1).getId();
                    //notify the adapter that the tweets were added
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "json exception");
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + response, throwable);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess fetch extra!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    adapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    Log.e(TAG, "json exception");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: ", throwable);

            }
        });
    }
    }