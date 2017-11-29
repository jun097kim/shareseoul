package kr.or.hanium.shareseoul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.adapters.BathroomsAdapter;
import kr.or.hanium.shareseoul.interfaces.BathroomsService;
import kr.or.hanium.shareseoul.models.Bathroom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BathroomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathrooms);

        overridePendingTransition(0, 0);

        Intent intent = getIntent();
        String picked = intent.getStringExtra("picked");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        final RecyclerView rvBathrooms = findViewById(R.id.rv_bathrooms);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvBathrooms.setLayoutManager(linearLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BathroomsService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BathroomsService bathroomsService = retrofit.create(BathroomsService.class);

        Call<List<Bathroom>> bathroomsCall = bathroomsService.getBathrooms(latitude, longitude);
        bathroomsCall.enqueue(new Callback<List<Bathroom>>() {
            @Override
            public void onResponse(Call<List<Bathroom>> call, Response<List<Bathroom>> response) {
                List<Bathroom> bathrooms = response.body();
                BathroomsAdapter adapter = new BathroomsAdapter(bathrooms, BathroomsActivity.this);
                rvBathrooms.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Bathroom>> call, Throwable t) {
                Toast.makeText(BathroomsActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final FloatingSearchView svBathroom = findViewById(R.id.sv_bathroom);

        switch (picked) {
            case "bikes":
                svBathroom.setSearchHint("따릉이 대여소 검색");
                break;
            case "restaurants":
                svBathroom.setSearchHint("음식점 검색");
                break;
            case "bathrooms":
                svBathroom.setSearchHint("화장실 검색");
                break;
        }

        AppBarLayout appBar = findViewById(R.id.appbar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                svBathroom.setTranslationY(verticalOffset);
            }
        });

        svBathroom.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
