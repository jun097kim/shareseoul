package kr.or.hanium.mojjak.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.List;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.adapters.BikesAdapter;
import kr.or.hanium.mojjak.interfaces.BikesService;
import kr.or.hanium.mojjak.models.Bike;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BikesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        final RecyclerView rvBikes = (RecyclerView) findViewById(R.id.rv_bikes);

        // 리사이클러뷰에 레이아웃 매니저 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvBikes.setLayoutManager(linearLayoutManager);

        // 리사이클러뷰 어댑터 만들기. 그 전에 리스트 아이템을 위한 레이아웃 만들기

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BikesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BikesService bikesService = retrofit.create(BikesService.class);

        Call<List<Bike>> bikeCall = bikesService.getBikes(latitude, longitude);
        bikeCall.enqueue(new Callback<List<Bike>>() {
            @Override
            public void onResponse(Call<List<Bike>> call, Response<List<Bike>> response) {
                List<Bike> bikes = response.body();
                BikesAdapter adapter = new BikesAdapter(bikes, BikesActivity.this);
                rvBikes.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Bike>> call, Throwable t) {
                Toast.makeText(BikesActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final FloatingSearchView svBike = (FloatingSearchView) findViewById(R.id.sv_bike);
        AppBarLayout mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                svBike.setTranslationY(verticalOffset);
            }
        });

        svBike.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                finish();
            }
        });
    }

    // 새로운 좌표로 리스트를 생성하려면 onCreate()가 호출되어야 하므로 액티비티 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
