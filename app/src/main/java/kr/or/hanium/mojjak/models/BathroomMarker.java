package kr.or.hanium.mojjak.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class BathroomMarker implements ClusterItem {
    private String id;
    private LatLng position;
    private String title;

    public BathroomMarker(String id, LatLng position, String title) {
        this.id = id;
        this.position = position;
        this.title = title;
    }

    public String getId() {
        return id;
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
}
