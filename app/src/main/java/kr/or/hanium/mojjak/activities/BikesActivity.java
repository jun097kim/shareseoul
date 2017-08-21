package kr.or.hanium.mojjak.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.adapters.BikesAdapter;
import kr.or.hanium.mojjak.interfaces.BikesAPI;
import kr.or.hanium.mojjak.models.Bike;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BikesActivity extends AppCompatActivity {
    @BindView(R.id.rv_bikes)
    RecyclerView rvBikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        // listview를 위한 레이아웃 매니저 설정
        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvBikes.setLayoutManager(mStaggeredGridLayoutManager);

        // recyclerview 어댑터 만들기. 그 전에 리스트 아이템을 위한 레이아웃 만들기

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BikesAPI.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BikesAPI bikesAPI = retrofit.create(BikesAPI.class);

        Call<List<Bike>> bikeCall = bikesAPI.getBike("37.56", "126.97");
        bikeCall.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                List<Bike> bikes = response.body();
                BikesAdapter adapter = new BikesAdapter(bikes);
                rvBikes.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
            }
        });
    }
}
