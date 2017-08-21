package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Restaurant {
    @SerializedName("channel")
    @Expose
    private Channel channel = null;

    public Channel getChannel() {
        return channel;
    }

    public class Channel {
        @SerializedName("item")
        @Expose
        private ArrayList<Item> item = null;

        public ArrayList<Item> getItem() {
            return item;
        }
    }

    public class Item {
        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("imageUrl")
        @Expose
        private String imageUrl;

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
