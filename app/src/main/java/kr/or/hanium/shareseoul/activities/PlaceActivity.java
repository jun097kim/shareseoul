package kr.or.hanium.shareseoul.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.adapters.DetailsAdapter;
import kr.or.hanium.shareseoul.interfaces.RatingService;
import kr.or.hanium.shareseoul.models.Rating;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private int userId;

    private String placeName;
    private String placeId;

    private ArrayList<RadioButton> rbRating = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userId = pref.getInt("userId", 3);

        // 구글 지도 프래그먼트
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 라이트 모드에서 클릭 이벤트 비활성화
        mapFragment.getView().setClickable(false);


        // 뷰에 리스너 연결하기
        Button btnSetOrigin = findViewById(R.id.btn_set_origin);
        Button btnSetDestination = findViewById(R.id.btn_set_destination);
        TextView tvRating = findViewById(R.id.tv_rating);

        btnSetOrigin.setOnClickListener(this);
        btnSetDestination.setOnClickListener(this);


        Intent intent = getIntent();

        String placeType = intent.getStringExtra("placeType");
        placeName = intent.getStringExtra("placeName");
        placeId = intent.getStringExtra("placeId");

        TextView tvPlaceName = findViewById(R.id.place_name);
        tvPlaceName.setText(placeName);


        // 장소 세부정보 리사이클러뷰
        RecyclerView rvDetails = findViewById(R.id.rv_details);

        // 레이아웃 매니저 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvDetails.setLayoutManager(linearLayoutManager);

        Resources res = getResources();
        String[] detailNames = res.getStringArray(R.array.bike_detail_names);
        String[] detailValues = res.getStringArray(R.array.bike_detail_values);

        if (placeType != null) {
            StringBuilder text = new StringBuilder("이 ");

            switch (placeType) {
                case "bikes":
                    text.append("따릉이 대여소");

                    detailValues[0] = Integer.toString(intent.getIntExtra("placeId", 0));
                    detailValues[1] = Integer.toString(intent.getIntExtra("placeCount", 0));

                    break;
                case "restaurants":
                    text.append("음식점");
                    break;
                case "bathrooms":
                    text.append("화장실");
                    break;
            }

            text.append(" 어떠셨나요?");
            tvRating.setText(text);
        }

        DetailsAdapter adapter = new DetailsAdapter(detailNames, detailValues);
        rvDetails.setAdapter(adapter);

        RadioGroup rgRating = findViewById(R.id.rg_rating);
        rgRating.setOnCheckedChangeListener(this);

        rbRating.add((RadioButton) findViewById(R.id.rb_rating_good));
        rbRating.add((RadioButton) findViewById(R.id.rb_rating_fair));
        rbRating.add((RadioButton) findViewById(R.id.rb_rating_poor));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, DirectionsActivity.class);

        switch (view.getId()) {
            case R.id.btn_set_origin:
                intent.putExtra("setType", "origin");
                intent.putExtra("placeName", placeName);
                startActivity(intent);
                break;
            case R.id.btn_set_destination:
                intent.putExtra("setType", "destination");
                intent.putExtra("placeName", placeName);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_rating_poor:
                ratingCall(0);
                break;
            case R.id.rb_rating_fair:
                ratingCall(1);
                break;
            case R.id.rb_rating_good:
                ratingCall(2);
                break;
        }
    }

    public void ratingCall(final int myRating) {
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
                    Toast.makeText(PlaceActivity.this, "평점이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlaceActivity.this, "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                Toast.makeText(PlaceActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng seoul = new LatLng(37.56, 126.97);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        googleMap.addMarker(new MarkerOptions().position(seoul));

        // 툴바 비활성화
        googleMap.getUiSettings().setMapToolbarEnabled(false);
    }
}
