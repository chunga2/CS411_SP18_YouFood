package com.cs411.droptableuser.youfood_android_app.responses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by raajesharunachalam on 3/21/18.
 */

public class GETRestaurantResponse implements Parcelable {
    @SerializedName("address")
    private String address;

    @SerializedName("categories")
    private String[] categories;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("pricerange")
    private String priceRange;

    public String getAddress() {
        return address;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPriceRange() {
        return priceRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeStringArray(this.categories);
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.priceRange);
    }

    public GETRestaurantResponse() {
    }

    protected GETRestaurantResponse(Parcel in) {
        this.address = in.readString();
        this.categories = in.createStringArray();
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.priceRange = in.readString();
    }

    public static final Parcelable.Creator<GETRestaurantResponse> CREATOR = new Parcelable.Creator<GETRestaurantResponse>() {
        @Override
        public GETRestaurantResponse createFromParcel(Parcel source) {
            return new GETRestaurantResponse(source);
        }

        @Override
        public GETRestaurantResponse[] newArray(int size) {
            return new GETRestaurantResponse[size];
        }
    };
}
