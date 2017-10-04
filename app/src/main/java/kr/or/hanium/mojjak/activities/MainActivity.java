package kr.or.hanium.mojjak.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.interfaces.BathroomsService;
import kr.or.hanium.mojjak.interfaces.BikesService;
import kr.or.hanium.mojjak.models.Bathroom;
import kr.or.hanium.mojjak.models.Bike;
import kr.or.hanium.mojjak.models.Place;
import kr.or.hanium.mojjak.models.PlaceMarker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<PlaceMarker> {

    private GoogleMap mMap;

    private String userEmail;
    private String picked;

    private BikesService bikesService;
    private BathroomsService bathroomsService;

    private ClusterManager<PlaceMarker> mClusterManager;
    private Handler handler = new Handler();
    private List<PlaceMarker> placeMarkers = new ArrayList<>();
    private Set<String> visibleMarkers = new HashSet<>();

    private PlaceMarker placeMarker;  // 클릭한 마커

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        AddressFragment addressFragment = new AddressFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, addressFragment).commit();

        FloatingActionButton fabPick = (FloatingActionButton) findViewById(R.id.fab_pick);
        TextView tvSearch = (TextView) findViewById(R.id.tv_search);

        fabPick.setOnClickListener(this);
        tvSearch.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handler.post(updateMarker);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // 네비게이션 헤더(로그인 메뉴)에 리스너 설정
        RelativeLayout navLogin = headerView.findViewById(R.id.nav_login);
        navigationView.setNavigationItemSelectedListener(this);
        navLogin.setOnClickListener(this);

        // 로그인되어 있는지 확인
        TextView tvEmail = headerView.findViewById(R.id.tv_email);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userEmail = pref.getString("userEmail", "");
        if (!TextUtils.isEmpty(userEmail)) {
            tvEmail.setText(userEmail);
//            navLogin.setOnClickListener(null);
        }


        Intent intent = getIntent();
        picked = intent.getStringExtra("picked");

        Retrofit retrofit;
        switch (picked) {
            case "bikes":
                // 따릉이 대여소 API
                retrofit = new Retrofit.Builder()
                        .baseUrl(BikesService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                        .build();
                bikesService = retrofit.create(BikesService.class);
                break;
            case "restaurants":
                // TODO: 음식점 API
                break;
            case "bathrooms":
                // 화장실 API
                retrofit = new Retrofit.Builder()
                        .baseUrl(BathroomsService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                        .build();
                bathroomsService = retrofit.create(BathroomsService.class);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 네비게이션 메뉴 아이템을 눌렀을 때 호출
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        // 아이템에 해당하는 액티비티로 전환
        if (id == R.id.nav_search) {
            intent = new Intent(this, SearchActivity.class);
            intent.putExtra("searchType", "normal");
        } else if (id == R.id.nav_route) {
            intent = new Intent(this, DirectionsActivity.class);
        } else if (id == R.id.nav_restaurants) {
            intent = new Intent(this, RestaurantsActivity.class);
        }
        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 지도를 사용할 준비가 되면 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);   // Lat(Latitude): 위도, Lng(Longitude): 경도
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));  // 서울로 카메라 이동
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15)); // 줌 레벨 15으로 설정

        // 클러스터 매니저 생성
        mClusterManager = new ClusterManager<>(this, mMap);

        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(new BathroomRenderer(this, mMap, mClusterManager));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                placeMarker = null;

                showAddressFragment();

                handler.post(updateAddress);
            }
        });

        // 카메라 이동이 끝났을 때, 한 번만 호출
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                handler.post(updateMarker);
                if (placeMarker == null) handler.post(updateAddress);
            }
        });

        mMap.setOnMarkerClickListener(mClusterManager);
    }

    class BathroomRenderer extends DefaultClusterRenderer<PlaceMarker> {
        public BathroomRenderer(Context context, GoogleMap map, ClusterManager<PlaceMarker> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(PlaceMarker item, MarkerOptions markerOptions) {
            markerOptions.icon(item.getImage());
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    Runnable updateAddress = new Runnable() {
        @Override
        public void run() {
            LatLng target = mMap.getCameraPosition().target;
            double latitude = target.latitude;
            double longitude = target.longitude;

            // 좌표를 주소로 변환
            Geocoder geocoder = new Geocoder(MainActivity.this);
            List<Address> matches = null;
            try {
                matches = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address bestMatch = (matches == null) ? null : matches.get(0);

            TextView tvAddress = (TextView) findViewById(R.id.tv_place);

            if (bestMatch != null) {
                // getSubLocality(): 시군구, getThoroughfare(): 읍면동|도로명
                String[] strings = {bestMatch.getSubLocality(), bestMatch.getThoroughfare()};

                if (bestMatch.getAdminArea() == null) {
                    tvAddress.setText("");
                } else if (bestMatch.getAdminArea() != null) {
                    tvAddress.setText(bestMatch.getLocality() + " ");
                }

                for (String s : strings) {
                    if (s != null) tvAddress.append(s + " ");
                }

/*                tvAddress.setText(bestMatch.getAddressLine(0)
                        .replaceFirst("[가-힣 ]+" + bestMatch.getLocality() + " ", "")
                        .replaceFirst(" " + bestMatch.getFeatureName(), ""));*/
            } else {
                tvAddress.setText("주소를 확인할 수 없습니다.");
            }
        }
    };

    Runnable updateMarker = new Runnable() {
        @Override
        public void run() {
            // 현재 보이는 지도의 중심 좌표
            LatLng target = mMap.getCameraPosition().target;
            double latitude = target.latitude;
            double longitude = target.longitude;

            switch (picked) {
                case "bikes":
                    Call<List<Bike>> bikesCall = bikesService.getBikes(latitude, longitude);
                    markerCall(bikesCall);
                    break;
                case "restaurants":
                    break;
                case "bathrooms":
                    Call<List<Bathroom>> bathroomsCall = bathroomsService.getBathrooms(latitude, longitude);
                    markerCall(bathroomsCall);
            }
        }
    };

    public void markerCall(Call call) {
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                // response.body()는 List<Place> 타입만 반환한다.
                for (Place place : response.body()) {
                    String id = place.getId();
                    String name = place.getName();
                    double latitude = place.getLatitude();
                    double longitude = place.getLongitude();

                    placeMarkers.add(new PlaceMarker(id, new LatLng(latitude, longitude), name));
                }

                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                for (final PlaceMarker item : placeMarkers) {
                    if (bounds.contains(item.getPosition())) {
                        if (!visibleMarkers.contains(item.getId())) {
                            visibleMarkers.add(item.getId());
                            mClusterManager.addItem(item);
                        }
                    } else {
                        if (visibleMarkers.contains(item.getId())) {
                            visibleMarkers.remove(item.getId());
                            mClusterManager.removeItem(item);
                        }
                    }
                }

                // run 메소드 바로 아래에 추가하면 안 됨. 응답이 오지 않아도 클러스터될 수 있음
                mClusterManager.cluster();
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onClusterItemClick(PlaceMarker PlaceMarker) {
        this.placeMarker = PlaceMarker;

        LatLng position = PlaceMarker.getPosition();
        Geocoder geocoder = new Geocoder(this);

        List<Address> matches = null;
        try {
            matches = geocoder.getFromLocation(position.latitude, position.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address bestMatch = (matches == null) ? null : matches.get(0);

        // 주소에서 국가 이름 제거
        String address = bestMatch.getAddressLine(0).replaceFirst(bestMatch.getCountryName() + " ", "");

        showPlaceFragment(address);

        // 마커를 눌렀을 때 가운데로 이동되지 않게 함
        /*for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
            if (marker.getPosition().latitude == placeMarker.getPosition().latitude &&
                    marker.getPosition().longitude == placeMarker.getPosition().longitude) {
                marker.showInfoWindow();
            }
        }*/
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_search:
                LatLng target = mMap.getCameraPosition().target;
                double latitude = target.latitude;
                double longitude = target.longitude;

                intent = new Intent(this, BikesActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                break;
            case R.id.nav_login:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.fab_pick:
                intent = new Intent(this, PickActivity.class);
        }
        startActivity(intent);
    }

    public static class AddressFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_address, container, false);
            return rootView;
        }
    }

    public static class PlaceFragment extends Fragment implements View.OnClickListener {
        String placeId;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place, container, false);

            LinearLayout bottomSheet = rootView.findViewById(R.id.bottom_sheet);
            TextView tvPlace = rootView.findViewById(R.id.tv_place);
            TextView tvPlaceType = rootView.findViewById(R.id.tv_place_type);
            TextView tvAddress = rootView.findViewById(R.id.tv_address);

            bottomSheet.setOnClickListener(this);

            Bundle args = getArguments();

            String name = args.getString("name");
            String placeType = null;

            switch (args.getString("placeType")) {
                case "bikes":
                    placeType = "따릉이 대여소";
                    break;
                case "restaurants":
                    placeType = "음식점";
                    break;
                case "bathrooms":
                    placeType = "화장실";
            }

            String address = args.getString("address");
            placeId = args.getString("placeId");

            tvPlace.setText(name);
            tvPlaceType.setText(placeType);
            tvAddress.setText(address);

            return rootView;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), PlaceActivity.class);
            intent.putExtra("placeId", placeId);
            startActivity(intent);
        }
    }

    private void showAddressFragment() {
        AddressFragment addressFragment = new AddressFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addressFragment);
        transaction.commit();
    }

    private void showPlaceFragment(String address) {
        PlaceFragment placeFragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putString("name", placeMarker.getTitle());
        args.putString("placeType", picked);
        args.putString("placeId", placeMarker.getId());
        args.putString("address", address);
        placeFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, placeFragment);
        transaction.commit();
    }
}
