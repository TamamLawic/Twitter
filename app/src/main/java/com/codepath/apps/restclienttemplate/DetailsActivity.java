package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    Tweet tweet;

    ImageView ivProfileDetails;
    TextView tvTweetDetails;
    ImageView ivTweetPhoto;
    Button btnRespond;
    Button btnRetweets;
    TextView tvRetweetsDetails;
    Button btnLike;
    TextView tvLikesDetails;
    TextView tvScreenNameDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //make references to each
        ivProfileDetails = findViewById(R.id.ivProfileDetails);
        tvTweetDetails = findViewById(R.id.tvTweetDetails);
        ivTweetPhoto = findViewById(R.id.ivTweetPhoto);
        btnRespond = findViewById(R.id.btnRespond);
        btnRetweets = findViewById(R.id.btnRetweets);
        tvRetweetsDetails = findViewById(R.id.tvRetweetsDetails);
        btnLike = findViewById(R.id.btnLike);
        tvLikesDetails = findViewById(R.id.tvLikesDetails);
        tvScreenNameDetails = findViewById(R.id.tvScreenNameDetails);

        //unwrap tweet data from pass
        //unwrap the movie that was passed in, use the simple key
        tweet = (Tweet) Parcels
                .unwrap(getIntent()
                        .getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d("DetailsActivity", "got tweet");

        tvLikesDetails.setText(tweet.getLikes());
        tvRetweetsDetails.setText(tweet.getRetweets());
        tvTweetDetails.setText(tweet.getBody());
        tvScreenNameDetails.setText(tweet.getUser().screenName);

        //set profile image and tweet photo with glide
        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileDetails);
        Glide.with(this).load(tweet.getUrls().get(0)).override(Target.SIZE_ORIGINAL, 800).into(ivTweetPhoto);



    }

}