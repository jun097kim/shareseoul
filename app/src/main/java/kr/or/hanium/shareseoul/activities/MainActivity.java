package kr.or.hanium.shareseoul.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.Share;
import kr.or.hanium.shareseoul.interfaces.BathroomsService;
import kr.or.hanium.shareseoul.interfaces.BikesService;
import kr.or.hanium.shareseoul.interfaces.RestaurantsService;
import kr.or.hanium.shareseoul.models.Bathroom;
import kr.or.hanium.shareseoul.models.Bike;
import kr.or.hanium.shareseoul.models.Place;
import kr.or.hanium.shareseoul.models.PlaceMarker;
import kr.or.hanium.shareseoul.models.Restaurants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<PlaceMarker>,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private final String TAG = "abcd";

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // 현재 위치를 가져오기 위한 Fused Location Provider
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // 위치 권한이 부여되지 않은 경우 사용할 기본 위치 (Seoul, Korea)와 기본 줌 레벨
    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // 장치의 현재 위치
    private Location mLastKnownLocation;

    // 액티비티 상태 저장을 위한 키
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int REQUEST_CHECK_SETTINGS = 100;


    private String userEmail;
    private String picked;

    private BikesService bikesService;
    private RestaurantsService restaurantsService;
    private BathroomsService bathroomsService;

    private ClusterManager<PlaceMarker> mClusterManager;
    private Handler handler = new Handler();
    private List<PlaceMarker> placeMarkers = new ArrayList<>();
    private Set<String> visibleMarkers = new HashSet<>();

    private PlaceMarker placeMarker;  // 클릭한 마커

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_main);

        // FusedLocationProviderClient 인스턴스 생성
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // 지도 만들기
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // 네비게이션 헤더(로그인 메뉴)에 리스너 설정
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        RelativeLayout navLogin = headerView.findViewById(R.id.nav_login);
        navigationView.setNavigationItemSelectedListener(this);
        navLogin.setOnClickListener(this);


        // 액티비티 전환 애니메이션 없애기
        overridePendingTransition(0, 0);

        // bottom sheet에 기본 address 프래그먼트 추가
        AddressFragment addressFragment = new AddressFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, addressFragment).commit();
        setFabMargin("address");

        // 뷰에 리스너 설정하기
        TextView tvSearch = findViewById(R.id.tv_search);
        FloatingActionButton fabMyLocation = findViewById(R.id.fab_my_location);

        tvSearch.setOnClickListener(this);
        fabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 위치 설정 다이얼로그 띄우기
                createLocationRequest();
            }
        });

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
                tvSearch.setText("따릉이 대여소 검색");

                // 따릉이 대여소 API
                retrofit = new Retrofit.Builder()
                        .baseUrl(BikesService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                bikesService = retrofit.create(BikesService.class);
                break;
            case "restaurants":
                tvSearch.setText("음식점 검색");

                // 음식점 API
                retrofit = new Retrofit.Builder()
                        .baseUrl(RestaurantsService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                restaurantsService = retrofit.create(RestaurantsService.class);
                break;
            case "bathrooms":
                tvSearch.setText("화장실 검색");

                // 화장실 API
                retrofit = new Retrofit.Builder()
                        .baseUrl(BathroomsService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                bathroomsService = retrofit.create(BathroomsService.class);
        }
    }

    /**
     * 액티비티가 일시정지될 때 지도의 상태 저장
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause()");

        if (mMap != null) {
            SharedPreferences mapStatePrefs = getSharedPreferences("mapStatePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = mapStatePrefs.edit();

            Gson gson = new Gson();

            String cameraPosition = gson.toJson(mMap.getCameraPosition());
            String lastKnownLocation = gson.toJson(mLastKnownLocation);

            Log.d(TAG, cameraPosition.toString() + lastKnownLocation.toString());

            editor.putString(KEY_CAMERA_POSITION, cameraPosition);
            editor.putString(KEY_LOCATION, lastKnownLocation);

            editor.apply();
        }
    }

    /**
     * 액티비티가 재개될 때 지도의 상태 복구
     */
    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        SharedPreferences mapStatePrefs = getSharedPreferences("mapStatePrefs", MODE_PRIVATE);

        String cameraPosition = mapStatePrefs.getString(KEY_CAMERA_POSITION, null);
        String lastKnownLocation = mapStatePrefs.getString(KEY_LOCATION, null);

        if (cameraPosition != null && lastKnownLocation != null) {
            Gson gson = new Gson();

            mCameraPosition = gson.fromJson(cameraPosition, CameraPosition.class);
            mLastKnownLocation = gson.fromJson(lastKnownLocation, Location.class);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 지도를 사용할 준비가 되면 호출
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }


        // 클러스터 매니저 생성
        mClusterManager = new ClusterManager<>(this, mMap);

        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(new ClusterRenderer(this, mMap, mClusterManager, picked));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                placeMarker = null;

                showAddressFragment();

                handler.post(updateAddress);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            /**
             * 카메라 이동이 끝났을 때, 한 번만 호출
             */
            @Override
            public void onCameraIdle() {
                handler.post(updateMarker);
                if (placeMarker == null) handler.post(updateAddress);
            }
        });

        mMap.setOnMarkerClickListener(mClusterManager);
    }

    /**
     * 장치의 현재 위치 얻어서 지도 카메라 위치시키기
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        /*
         * 장치의 가장 최근 위치를 가져오고, 위치를 사용할 수 없는 경우 null일 수 있다.
         */
        if (mLocationPermissionGranted) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setNumUpdates(1);

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // 지도의 카메라 위치를 장치의 현재 위치로 설정
                    mLastKnownLocation = locationResult.getLastLocation();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                }
            };

            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    /**
     * 런타임에 location 권한 요청
     */
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * location 권한 요청 결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // 요청을 취소한 경우, 결과는 빈 배열
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                }
            }
        }
    }


    /**
     * 유저가 location 권한을 부여한 경우 My Location 계층 활성화
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        // 위치 설정이 켜져있는 경우
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // 권한 요청 다이얼로그 띄우기
                getLocationPermission();

                // My Location 계층 활성화
                updateLocationUI();

                // 장치의 현재 위치를 얻고 지도에 설정하기
                getDeviceLocation();
            }
        });

        // 위치 설정이 꺼져있는 경우
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // startResolutionForResult()를 호출하여 다이얼로그를 띄우고,
                            // onActivityResult()에서 결과를 확인한다.
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // 위치 설정을 수정할 방법이 없어서 다이얼로그를 띄우지 않음
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // 권한 요청 다이얼로그 띄우기
                        getLocationPermission();

                        // My Location 계층 활성화
                        updateLocationUI();

                        // 장치의 현재 위치를 얻고 지도에 설정하기
                        getDeviceLocation();
                        break;
                }
                break;
        }
    }

    class ClusterRenderer extends DefaultClusterRenderer<PlaceMarker> {
        String picked;

        public ClusterRenderer(Context context, GoogleMap map, ClusterManager<PlaceMarker> clusterManager, String picked) {
            super(context, map, clusterManager);
            this.picked = picked;
        }

        @Override
        protected void onBeforeClusterItemRendered(PlaceMarker item, MarkerOptions markerOptions) {
            BitmapDescriptor bitmapDescriptor = null;

            switch (picked) {
                case "bikes":
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_bike);
                    break;
                case "restaurants":
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant);
                    break;
                case "bathrooms":
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_bathroom);
            }

            markerOptions.icon(bitmapDescriptor);
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

            Address bestMatch = null;
            if (matches != null && matches.size() > 0) {
                bestMatch = matches.get(0);
            }

            TextView tvAddress = findViewById(R.id.tv_place);

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
                    Call<Restaurants> restaurantsCall = restaurantsService.getRestaurants(100, 1, 1,
                            "AND", "shareseoul", 'A', 39, longitude, latitude, 2000, 'Y', "json");
                    restaurantsMarkerCall(restaurantsCall);
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

    public void restaurantsMarkerCall(Call<Restaurants> restaurantsCall) {
        restaurantsCall.enqueue(new Callback<Restaurants>() {
            @Override
            public void onResponse(Call<Restaurants> call, Response<Restaurants> response) {
                for (Restaurants.Item item : response.body().getResponse().getBody().getItems().getItem()) {
                    String id = item.getId();
                    String name = item.getName();
                    double latitude = item.getLatitude();
                    double longitude = item.getLongitude();

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
            public void onFailure(Call<Restaurants> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Toast.makeText(MainActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                }

                // TODO: Use custom JsonDeserializer
                // String 타입의 items 반환하는 경우
//                else if (t instanceof JsonSyntaxException) {
//
//                }
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


    /**
     * 네비게이션 메뉴 아이템을 눌렀을 때 호출
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = new Intent();
        String s = null;

        // 아이템에 해당하는 액티비티로 전환
        if (id == R.id.nav_search) {
            intent.putExtra("searchType", "normal");
            s = "Search";
        } else if (id == R.id.nav_route) {
            s = "Directions";
        }

        ComponentName name = new ComponentName("kr.or.hanium.shareseoul",
                "kr.or.hanium.shareseoul.activities." + s + "Activity");
        intent.setComponent(name);
        startActivity(intent);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        String s = null;

        switch (v.getId()) {
            case R.id.tv_search:
                LatLng target = mMap.getCameraPosition().target;
                double latitude = target.latitude;
                double longitude = target.longitude;

                if (picked.equals("bikes")) s = "Bikes";
                else if (picked.equals("restaurants")) s = "Restaurants";

                intent.putExtra("picked", picked);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                break;
            case R.id.nav_login:
                s = "Login";
                break;
        }

        ComponentName name = new ComponentName("kr.or.hanium.shareseoul",
                "kr.or.hanium.shareseoul.activities." + s + "Activity");
        intent.setComponent(name);
        startActivity(intent);
    }

    public static class AddressFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_address, container, false);
        }
    }

    public static class PlaceFragment extends Fragment implements View.OnClickListener {
        String placeType;
        String address;
        String placeId;
        String placeName;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_place, container, false);

            // 뷰에 리스너 연결하기
            LinearLayout bottomSheet = rootView.findViewById(R.id.bottom_sheet);
            TextView tvPlace = rootView.findViewById(R.id.tv_place);
            TextView tvPlaceType = rootView.findViewById(R.id.tv_place_type);
            TextView tvAddress = rootView.findViewById(R.id.tv_address);
            ImageButton ibShare = rootView.findViewById(R.id.ib_share);

            bottomSheet.setOnClickListener(this);
            ibShare.setOnClickListener(this);

            Bundle args = getArguments();

            placeType = args.getString("placeType");
            if (placeType != null) {
                switch (placeType) {
                    case "bikes":
                        tvPlaceType.setText("따릉이 대여소");
                        break;
                    case "restaurants":
                        tvPlaceType.setText("음식점");
                        break;
                    case "bathrooms":
                        tvPlaceType.setText("화장실");
                }
            }

            address = args.getString("address");
            placeId = args.getString("placeId");
            placeName = args.getString("name");


            tvPlace.setText(placeName);
            tvAddress.setText(address);

            return rootView;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bottom_sheet:
                    Intent intent = new Intent(getActivity(), PlaceActivity.class);
                    intent.putExtra("placeType", placeType);
                    intent.putExtra("placeId", placeId);
                    intent.putExtra("placeName", placeName);
                    startActivity(intent);
                    break;

                case R.id.ib_share:
                    new Share(getActivity(), placeName, address);
                    break;
            }
        }
    }


    private void showAddressFragment() {
        AddressFragment addressFragment = new AddressFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, addressFragment);
        transaction.commit();

        setFabMargin("address");
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

        setFabMargin("place");
    }


    private void setFabMargin(String type) {
        switch (type) {
            case "address":
                setFabMargin(160);
                break;
            case "place":
                setFabMargin(345);
                break;
        }
    }

    private void setFabMargin(int bottom) {
        FloatingActionButton fabMyLocation = findViewById(R.id.fab_my_location);

        CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) fabMyLocation.getLayoutParams();
        params1.setMargins(0, 0, 20, bottom);
    }
}
