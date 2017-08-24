package kr.or.hanium.mojjak.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import kr.or.hanium.mojjak.interfaces.RatingService;
import kr.or.hanium.mojjak.models.Bathroom;
import kr.or.hanium.mojjak.models.BathroomMarker;
import kr.or.hanium.mojjak.models.Rating;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static kr.or.hanium.mojjak.R.id.map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, RatingBar.OnRatingBarChangeListener,
        OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<BathroomMarker> {

    private int userId;
    private String userEmail;

    private GoogleMap mMap;

    private BathroomsService mBathroomsService;

    private ClusterManager<BathroomMarker> mClusterManager;
    private Handler handler = new Handler();
    private List<BathroomMarker> bathroomMarkers = new ArrayList<>();
    private Set<String> visibleMarkers = new HashSet<>();

    private BathroomMarker bathroomMarker;  // 클릭한 마커
    private RatingBar rbMyRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabPick = (FloatingActionButton) findViewById(R.id.fab_pick);
        TextView searchBtn = (TextView) findViewById(R.id.btn_search);

        fabPick.setOnClickListener(this);
        searchBtn.setOnClickListener(this);

        rbMyRating = (RatingBar) findViewById(R.id.my_rating);
        rbMyRating.setOnRatingBarChangeListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        // 네비게이션 헤더(로그인 메뉴)에 리스너 설정
        RelativeLayout navLogin = headerView.findViewById(R.id.nav_login);
        navigationView.setNavigationItemSelectedListener(this);
        navLogin.setOnClickListener(this);

        // 로그인되어 있는지 확인
        TextView tvEmail = headerView.findViewById(R.id.tv_email);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userId = pref.getInt("userId", 0);
        userEmail = pref.getString("userEmail", "");
        if (!TextUtils.isEmpty(userEmail)) {
            tvEmail.setText(userEmail);
//            navLogin.setOnClickListener(null);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        handler.post(updateMarker);

        // 화장실 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BathroomsService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        mBathroomsService = retrofit.create(BathroomsService.class);
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
        }
        else if (id == R.id.nav_route) {
            intent = new Intent(this, RouteActivity.class);
        }
        else if (id == R.id.nav_restaurants) {
            intent = new Intent(this, RestaurantsActivity.class);
        }
        else if (id == R.id.nav_bikes) {
            intent = new Intent(this, BikesActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

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

        // 카메라 이동이 끝났을 때, 한 번만 호출
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                handler.post(updateMarker);
            }
        });

        mMap.setOnMarkerClickListener(mClusterManager);
    }

    class BathroomRenderer extends DefaultClusterRenderer<BathroomMarker> {
        public BathroomRenderer(Context context, GoogleMap map, ClusterManager<BathroomMarker> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(BathroomMarker item, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bike_marker));
            markerOptions.snippet(item.getSnippet());
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

    Runnable updateMarker = new Runnable() {
        @Override
        public void run() {
            // 현재 보이는 지도의 중심 좌표
            LatLng target = mMap.getCameraPosition().target;
            double latitude = target.latitude;
            double longitude = target.longitude;

            // 좌표를 주소로 변환
            Geocoder geoCoder = new Geocoder(MainActivity.this);
            List<Address> matches = null;
            try {
                matches = geoCoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address bestMatch = (matches == null) ? null : matches.get(0);

            TextView tvAddress = (TextView) findViewById(R.id.tv_address);

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

            } else {
                tvAddress.setText("주소를 확인할 수 없습니다.");
            }

            Call<List<Bathroom>> placesAPIResponseCall = mBathroomsService.getPlaces(latitude, longitude);
            placesAPIResponseCall.enqueue(new Callback<List<Bathroom>>() {
                @Override
                public void onResponse(Call<List<Bathroom>> call, final Response<List<Bathroom>> response) {
                    for (Bathroom bathroom : response.body()) {
                        String id = bathroom.getId();
                        String title = bathroom.getTitle();
                        double latitude = bathroom.getLatitude();
                        double longitude = bathroom.getLongitude();

                        bathroomMarkers.add(new BathroomMarker(id, new LatLng(latitude, longitude), title));
                    }

                    LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    for (final BathroomMarker item : bathroomMarkers) {
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
                public void onFailure(Call<List<Bathroom>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    public boolean onClusterItemClick(BathroomMarker bathroomMarker) {
        TextView toiletPlace = (TextView) findViewById(R.id.toilet_place);
        toiletPlace.setText(bathroomMarker.getTitle());
        this.bathroomMarker = bathroomMarker;

        // 마커를 눌렀을 때 가운데로 이동되지 않게 함
        for (Marker marker : mClusterManager.getMarkerCollection().getMarkers()) {
            if (marker.getPosition().latitude == bathroomMarker.getPosition().latitude &&
                    marker.getPosition().longitude == bathroomMarker.getPosition().longitude) {
                marker.showInfoWindow();
            }
        }
        return true;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String placeId = bathroomMarker.getId();
        int myRating = (int) rating;

        if (userId != 0) {
            // 평점 API
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RatingService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                    .build();
            RatingService ratingService = retrofit.create(RatingService.class);

            ratingService.getSuccess(userId, placeId, myRating).enqueue(new Callback<Rating>() {
                @Override
                public void onResponse(Call<Rating> call, Response<Rating> response) {
                    if (response.body().getSuccess()) {
                        Toast.makeText(MainActivity.this, "평점이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Rating> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.fab_pick:
                intent = new Intent(this, PickActivity.class);
                break;
            case R.id.btn_search:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("searchType", "normal");
                break;
            case R.id.nav_login:
                intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기
    }
}
