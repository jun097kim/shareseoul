package kr.or.hanium.mojjak.models;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import kr.or.hanium.mojjak.R;

public class PlaceMarker implements ClusterItem {
    private LatLng position;
    private String title;
    private String id;
    private BitmapDescriptor image;

    public PlaceMarker(String id, LatLng position, String title) {
        this.id = id;
        this.position = position;
        this.title = title;
        image = BitmapDescriptorFactory.fromResource(R.drawable.marker_bike);
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

    public BitmapDescriptor getImage() {
        return image;
    }

    public void setImage(BitmapDescriptor image) {
        this.image = image;
    }
}
