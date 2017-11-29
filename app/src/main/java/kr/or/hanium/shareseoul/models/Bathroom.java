package kr.or.hanium.shareseoul.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bathroom extends Place {
    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("distance")
    @Expose
    private double distance;

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public double getDistance() {
        return distance;
    }
}
