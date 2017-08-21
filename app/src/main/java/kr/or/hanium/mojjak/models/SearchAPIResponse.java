package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by soyeon on 2017. 7. 22..
 */

public class SearchAPIResponse {
    @SerializedName("channel")
    @Expose
    private sChannel channel = null;

    public sChannel getChannel() {
        return channel;
    }

    public class sChannel {
        @SerializedName("item")
        @Expose
        private ArrayList<sItem> item = null;

        public ArrayList<sItem> getItem() {
            return item;
        }
    }

    public class sItem {
        @SerializedName("title")
        @Expose
        private String title;

        public String getTitle() {
            return title;
        }
    }
}
