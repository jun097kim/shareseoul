package kr.or.hanium.shareseoul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.adapters.RestaurantsAdapter;
import kr.or.hanium.shareseoul.interfaces.RestaurantsService;
import kr.or.hanium.shareseoul.models.Restaurants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantsActivity extends AppCompatActivity {
    RecyclerView rvRestaurants;
    RecyclerView.Adapter adapter;//어댑터
    ArrayList<Restaurants.Item> restaurantsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        initViews();
    }

    private void initViews() {
        rvRestaurants = findViewById(R.id.recyclerView);
        rvRestaurants.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRestaurants.setLayoutManager(linearLayoutManager);
        loadJson();
    }

    private void loadJson() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestaurantsService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        RestaurantsService restaurantsService = retrofit.create(RestaurantsService.class);

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        Call<Restaurants> recommendationAPIResponseCall = restaurantsService.getRestaurants(
                10, 1, 1, "AND", "shareseoul",
                'A', 39, longitude, latitude,
                500, 'Y', "json");
        recommendationAPIResponseCall.enqueue(new Callback<Restaurants>() {
            @Override
            public void onResponse(Call<Restaurants> call, Response<Restaurants> response) {
                restaurantsItems = (ArrayList<Restaurants.Item>) response.body().getResponse().getBody().getItems().getItem();
                adapter = new RestaurantsAdapter(restaurantsItems, RestaurantsActivity.this);
                rvRestaurants.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Restaurants> call, Throwable t) {
                Toast.makeText(RestaurantsActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
