package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bathroom extends Place {
    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("phone")
    @Expose
    private String phone;

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
