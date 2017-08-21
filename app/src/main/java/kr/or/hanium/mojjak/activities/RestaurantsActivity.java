package kr.or.hanium.mojjak.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.adapters.RestaurantsAdapter;
import kr.or.hanium.mojjak.interfaces.RestaurantsAPIService;
import kr.or.hanium.mojjak.models.Restaurant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestaurantsActivity extends AppCompatActivity {
    RecyclerView rvRestaurants;
    RecyclerView.Adapter adapter;//어댑터
    ArrayList<Restaurant.Item> restaurantsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {
        rvRestaurants = (RecyclerView) findViewById(R.id.recyclerView);
        rvRestaurants.setHasFixedSize(true);

        RecyclerView.LayoutManager rLayoutManager = new GridLayoutManager(this, 2);
        rvRestaurants.setLayoutManager(rLayoutManager);
        loadJson();
    }

    private void loadJson() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestaurantsAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        RestaurantsAPIService restaurantsAPIService = retrofit.create(RestaurantsAPIService.class);

        Call<Restaurant> recommendationAPIResponseCall = restaurantsAPIService.getPlaces("12b7b22a1af7d9e2173ac88f2af8654f", "FD6", "37.56331,126.97590", "10000");
        recommendationAPIResponseCall.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                restaurantsItems = response.body().getChannel().getItem();
                adapter = new RestaurantsAdapter(restaurantsItems, RestaurantsActivity.this);
                rvRestaurants.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
            }
        });
    }
}
