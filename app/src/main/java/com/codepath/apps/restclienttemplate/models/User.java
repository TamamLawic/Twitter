package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public String name;
    public String screenName;
    public String userName;
    public String profileImageUrl;

    //Empty constructor needed for Parcel
    public User() {}

    //Converts into a USER Object in Java
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.userName = jsonObject.getString("name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
