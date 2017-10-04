package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bike extends Place {
    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("distance")
    @Expose
    private double distance;

    public int getCount() {
        return count;
    }

    public double getDistance() {
        return distance;
    }
}
