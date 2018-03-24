package com.cs411.droptableuser.youfood_android_app.responses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETReviewResponse implements Parcelable {
    @SerializedName("date")
    private String date;

    @SerializedName("description")
    private String description;

    @SerializedName("name")
    private String name;

    @SerializedName("rating")
    private int rating;

    @SerializedName("restaurant_name")
    private String restaurantName;

    @SerializedName("restaurant_address")
    private String restaurantAddress;

    @SerializedName("useremail")
    private String email;

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
<<<<<<< HEAD

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.description);
        dest.writeString(this.name);
        dest.writeInt(this.rating);
        dest.writeString(this.restaurantName);
        dest.writeString(this.restaurantAddress);
        dest.writeString(this.email);
    }

    public GETReviewResponse() {
    }

    protected GETReviewResponse(Parcel in) {
        this.date = in.readString();
        this.description = in.readString();
        this.name = in.readString();
        this.rating = in.readInt();
        this.restaurantName = in.readString();
        this.restaurantAddress = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<GETReviewResponse> CREATOR = new Parcelable.Creator<GETReviewResponse>() {
        @Override
        public GETReviewResponse createFromParcel(Parcel source) {
            return new GETReviewResponse(source);
        }

        @Override
        public GETReviewResponse[] newArray(int size) {
            return new GETReviewResponse[size];
        }
    };
=======
>>>>>>> origin/master
}
