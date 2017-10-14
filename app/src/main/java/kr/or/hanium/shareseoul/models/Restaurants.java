package kr.or.hanium.shareseoul.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurants {
    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public class Response {
        @SerializedName("body")
        @Expose
        private Body body;

        public Body getBody() {
            return body;
        }
    }

    public class Body {
        @SerializedName("items")
        @Expose
        private Items items;

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }
    }

    public class Items {
        @SerializedName("item")
        @Expose
        private List<Item> item;

        public List<Item> getItem() {
            return item;
        }

        public void setItem(List<Item> item) {
            this.item = item;
        }
    }

    public class Item {
        @SerializedName("addr1")
        @Expose
        private String addr1;

        @SerializedName("contenttypeid")
        @Expose
        private String id;

        @SerializedName("firstimage2")
        @Expose
        private String firstimage2;

        @SerializedName("mapx")
        @Expose
        private double longitude;

        @SerializedName("mapy")
        @Expose
        private double latitude;

        @SerializedName("title")
        @Expose
        private String name;

        public String getAddr1() {
            return addr1;
        }

        public String getId() {
            return id;
        }

        public String getFirstimage2() {
            return firstimage2;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public String getName() {
            return name;
        }
    }
}