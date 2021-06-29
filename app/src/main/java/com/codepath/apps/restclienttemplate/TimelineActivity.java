package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements View.OnClickListener{
    //make a debug custom tag
    public static final String TAG = "TimelineActivity";

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //
        client = TwitterApp.getRestClient(this);
        //find recycler view
        rvTweets = findViewById(R.id.rvTweets);

        button = findViewById(R.id.button2);

        //initialize tweets in adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        //configure recycler view : layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeline();
    }

    //Get all Tweets, and notify the adapter that they have been added
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    //add tweets pulled
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
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

    //Log out the user currently logged in when LogOutButton Clicked
    @Override
    public void onClick(View v) {
        client.clearAccessToken();
        Intent i = new Intent(TimelineActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
    }