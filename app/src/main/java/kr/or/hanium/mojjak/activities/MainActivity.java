package kr.or.hanium.mojjak.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.interfaces.BathroomsService;
import kr.or.hanium.mojjak.interfaces.RatingAPIService;
import kr.or.hanium.mojjak.models.Bathroom;
import kr.or.hanium.mojjak.models.RatingAPIResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static kr.or.hanium.mojjak.R.id.map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, RatingBar.OnRatingBarChangeListener,
        OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private BathroomsService mBathroomsService;
    private RatingBar rbMyRating;
    private Marker marker;

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
        String email = pref.getString("email", "");
        if (!TextUtils.isEmpty(email)) {
            tvEmail.setText(email);
//            navLogin.setOnClickListener(null);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        // 다음 로컬 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BathroomsService.API_URL)
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
        mMap.setOnCameraIdleListener(this);

        LatLng seoul = new LatLng(37.56, 126.97);  // Lat(Latitude): 위도, Lng(Longitude): 경도
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));  // 서울로 카메라 이동
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));  // 줌 레벨 16으로 설정
    }

    // 카메라 이동이 끝났을 때, 한 번만 호출
    @Override
    public void onCameraIdle() {
        LatLng target = mMap.getCameraPosition().target;    // 현재 보이는 지도의 중심 좌표

        Geocoder geoCoder = new Geocoder(this); // 좌표 ↔ 주소 변환기
        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(target.latitude, target.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address bestMatch = (matches.isEmpty() ? null : matches.get(0));

        TextView reverseGeocoded = (TextView) findViewById(R.id.reverse_geocoded);

        if ((bestMatch.getLocality() != null) | (bestMatch.getThoroughfare() != null)) {
            reverseGeocoded.setText(bestMatch.getLocality() + " " + bestMatch.getThoroughfare());  // getLocality(): 시군구, getThoroughfare(): 읍면동|도로명
        } else {
            reverseGeocoded.setText("주소를 확인할 수 없습니다.");
        }

        mMap.clear();

        Call<List<Bathroom>> placesAPIResponseCall = mBathroomsService.getPlaces(target.latitude, target.longitude);
        placesAPIResponseCall.enqueue(new Callback<List<Bathroom>>() {
            @Override
            public void onResponse(Call<List<Bathroom>> call, Response<List<Bathroom>> response) {
                for (Bathroom bathroom : response.body()) {
                    String id = bathroom.getId();
                    String title = bathroom.getTitle();
                    double latitude = bathroom.getLatitude();
                    double longitude = bathroom.getLongitude();

                    LatLng wc = new LatLng(latitude, longitude);  // 화장실 좌표
                    Marker marker = mMap.addMarker(new MarkerOptions().position(wc).title(title));  // 화장실 마커 추가
                    marker.setTag(id);
                    mMap.setOnMarkerClickListener(MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Bathroom>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
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
    public boolean onMarkerClick(Marker marker) {
        TextView toiletPlace = (TextView) findViewById(R.id.toilet_place);
        toiletPlace.setText(marker.getTitle());

        this.marker = marker;
        return false;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String placeID = marker.getTag().toString();
        int myRating = (int) rbMyRating.getRating();

        // 평점 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RatingAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        RatingAPIService ratingAPIService = retrofit.create(RatingAPIService.class);

        ratingAPIService.getSuccess(placeID, myRating).enqueue(new Callback<RatingAPIResponse>() {
            @Override
            public void onResponse(Call<RatingAPIResponse> call, Response<RatingAPIResponse> response) {
                if (!response.body().getSuccess()) {
                    Toast.makeText(MainActivity.this, "평점 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "평점 등록에 성공했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RatingAPIResponse> call, Throwable t) {
            }
        });
    }
}
