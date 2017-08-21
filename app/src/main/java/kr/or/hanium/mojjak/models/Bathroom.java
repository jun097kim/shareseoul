package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bathroom {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String title;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
