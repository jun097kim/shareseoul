package kr.or.hanium.mojjak;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, View.OnClickListener {

    private GoogleMap mMap;
    private PlaceAPIService placeAPIService;

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 다음 로컬 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlaceAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        placeAPIService = retrofit.create(PlaceAPIService.class);
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
        // 지하철 노선도 메뉴를 눌렀을 때, SubwayActivity 호출
        else if (id == R.id.nav_train) {
            Intent intent = new Intent(this, SubwayActivity.class);
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

        LatLng seoul = new LatLng(37.56, 126.97);   // Lat(Latitude): 위도, Lng(Longitude): 경도
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));  // 서울로 카메라 이동
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15)); // 줌 레벨 15로 확대
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null); // 2초간 줌 레벨 15로 확대
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                LatLng target = mMap.getCameraPosition().target;

                placeAPIService.getPlaces("12b7b22a1af7d9e2173ac88f2af8654f", "화장실", target.latitude + "," + target.longitude, 10000).enqueue(new Callback<PlaceAPIResponse>() {

                    @Override
                    public void onResponse(Call<PlaceAPIResponse> call, Response<PlaceAPIResponse> response) {
                        if (response.code() == 200) {   // request.code(): HTTP 상태 코드
                            for (Item item : response.body().getChannel().getItem()) {
                                String title = item.getTitle();
                                Double latitude = Double.parseDouble(item.getLatitude());
                                Double longitude = Double.parseDouble(item.getLongitude());

                                LatLng wc = new LatLng(latitude, longitude);                     // 화장실 좌표
                                mMap.addMarker(new MarkerOptions().position(wc).title(title));   // 화장실 마커 추가
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "API 트래픽이 초과되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlaceAPIResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case R.id.search_btn:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("searchType", "normal");
                startActivity(intent);

                overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

                break;
        }
    }
}
