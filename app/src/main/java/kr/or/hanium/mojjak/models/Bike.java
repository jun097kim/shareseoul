package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Bike {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("latitude")
    @Expose
    private BigDecimal latitude;

    @SerializedName("longitude")
    @Expose
    private BigDecimal longitude;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }
}
