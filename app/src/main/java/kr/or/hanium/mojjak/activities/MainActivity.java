package kr.or.hanium.mojjak.activities;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

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

    List<BathroomMarker> bathroomMarkers = new ArrayList<>();
    BathroomMarker bathroomMarker;
    Handler handler = new Handler();
    int userId;
    String email;
    private GoogleMap mMap;
    private BathroomsService mBathroomsService;
    private RatingBar rbMyRating;
    private ClusterManager<BathroomMarker> mClusterManager;
    private Set<String> visibleMarkers = new HashSet<>();
    Runnable updateMarker = new Runnable() {
        @Override
        public void run() {
            LatLng target = mMap.getCameraPosition().target;  // 현재 보이는 지도의 중심 좌표

            Geocoder geoCoder = new Geocoder(MainActivity.this);  // 좌표 ↔ 주소 변환기
            List<Address> matches = null;
            try {
                matches = geoCoder.getFromLocation(target.latitude, target.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            String[] strings = {bestMatch.getSubLocality(), bestMatch.getThoroughfare()};

            TextView tvAddress = (TextView) findViewById(R.id.tv_address);

            if (bestMatch != null) {
                if (bestMatch.getAdminArea() == null) {
                    tvAddress.setText("");  // getSubLocality(): 시군구, getThoroughfare(): 읍면동|도로명
                } else if (bestMatch.getAdminArea() != null) {
                    tvAddress.setText(bestMatch.getLocality() + " ");
                }
                for (String s : strings) {
                    if (s != null) tvAddress.append(s + " ");
                }
            } else {
                tvAddress.setText("주소를 확인할 수 없습니다.");
            }

            Call<List<Bathroom>> placesAPIResponseCall = mBathroomsService.getPlaces(target.latitude, target.longitude);
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
                }

                @Override
                public void onFailure(Call<List<Bathroom>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                }
            });
            mClusterManager.cluster();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView searchBtn = (TextView) findViewById(R.id.search_btn);

        fab.setOnClickListener(this);
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
        email = pref.getString("email", "");
        if (!TextUtils.isEmpty(email)) {
            tvEmail.setText(email);
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
                .addConverterFactory(GsonConverterFactory.create())  // JSON Converter 지정
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 검색 메뉴를 눌렀을 때, SearchActivity 호출
        if (id == R.id.nav_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("searchType", "normal");
            startActivity(intent);

            overridePendingTransition(0, 0);// 전환 애니메이션 없애기
        }
        // 길찾기 메뉴를 눌렀을 때, RouteActivity 호출
        else if (id == R.id.nav_route) {
            Intent intent = new Intent(this, RouteActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        // 맛집 메뉴를 눌렀을 때, BikesActivity 호출
        else if (id == R.id.nav_restaurants) {
            Intent intent = new Intent(this, RestaurantsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        // 따릉이 대여소 메뉴를 눌렀을 때, BikesActivity 호출
        else if (id == R.id.nav_bikes) {
            Intent intent = new Intent(this, BikesActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 구글 지도 API 이벤트가 발생했을 때, 호출되는 콜백 함수들

    // 지도를 사용할 준비가 되면 호출
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);  // Lat(Latitude): 위도, Lng(Longitude): 경도
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));  // 서울로 카메라 이동
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));  // 줌 레벨 16으로 설정

        // 클러스터 매니저 생성
        mClusterManager = new ClusterManager<>(this, mMap);

        mClusterManager.setOnClusterItemClickListener(this);

        // 카메라 이동이 끝났을 때, 한 번만 호출
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                handler.post(updateMarker);
            }
        });

        mMap.setOnMarkerClickListener(mClusterManager);
    }

    @Override
    public boolean onClusterItemClick(BathroomMarker bathroomMarker) {
        TextView toiletPlace = (TextView) findViewById(R.id.toilet_place);
        toiletPlace.setText(bathroomMarker.getTitle());
        this.bathroomMarker = bathroomMarker;
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.fab:
                break;

            case R.id.search_btn:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("searchType", "normal");
                startActivity(intent);
                overridePendingTransition(0, 0);    // 전환 애니메이션 없애기
                break;

            case R.id.nav_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String placeID = bathroomMarker.getId();
        int myRating = (int) rating;
        Toast.makeText(this, placeID + "내 평점" + myRating, Toast.LENGTH_SHORT).show();

        // 평점 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RatingService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        RatingService ratingService = retrofit.create(RatingService.class);

        ratingService.getSuccess(userId, placeID, myRating).enqueue(new Callback<Rating>() {
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
    }
}
