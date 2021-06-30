package com.codepath.apps.restclienttemplate.models;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public List<String> urls;


    //empty constructor needed for Parcels
    public Tweet() {}

    //Build the Tweet Per the fields in the JSONObject
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
        //must convert into a java USER Class
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        //Get Urls for the media/photos
        Log.i("TWEEET DATA", jsonObject.toString());
        //get the media URL for the tweet if it exists
        if(jsonObject.has("extended_entities")){
            Log.i("TWEEET DATA", "it has media!");
            tweet.urls = getImageUrlArray(jsonObject.getJSONObject("extended_entities").getJSONArray("media"));
            Log.i("TWEEET DATA", tweet.urls.toString());
        }
        else {
            tweet.urls = new ArrayList<>();
        }
        return tweet;
    }

    //Get Array of Strings, of all the urls for the media/pictures in the tweet
    private static List<String> getImageUrlArray(JSONArray jsonArray) throws JSONException {
        List<String> urls = new ArrayList<>();
        //traverse all media and add into list of URLS for the tweet
        for (int i = 0; i < jsonArray.length(); i++) {
            urls.add(jsonArray.getJSONObject(i).getString("media_url_https"));
        }
        return urls;
    }


    //return a list of tweet objects from a json Array
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    //Changes twitterAPI format for timestamp to show in "8m" format
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i("TweetsAdapter", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }
        return "";
    }

}
