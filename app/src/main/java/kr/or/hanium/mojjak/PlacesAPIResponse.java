package kr.or.hanium.mojjak;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlacesAPIResponse {
    @SerializedName("channel")
    @Expose
    private Channel channel = null;

    public Channel getChannel() {
        return channel;
    }
}

class Channel { // default: 같은 패키지
    @SerializedName("item")
    @Expose
    private ArrayList<Item> item;

    public ArrayList<Item> getItem() {
        return item;
    }
}

class Item {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
