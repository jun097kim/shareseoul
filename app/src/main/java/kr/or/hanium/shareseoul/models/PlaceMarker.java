package kr.or.hanium.shareseoul.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PlaceMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String id;
    private int count;

    public PlaceMarker(String id, LatLng position, String title) {
        this.id = id;
        this.position = position;
        this.title = title;
    }

    public PlaceMarker(String id, LatLng position, String title, int count) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.count = count;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
