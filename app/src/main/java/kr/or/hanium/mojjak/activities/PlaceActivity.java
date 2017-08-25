package kr.or.hanium.mojjak.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RatingBar;
import android.widget.Toast;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.interfaces.RatingService;
import kr.or.hanium.mojjak.models.Rating;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {
    private RatingBar rbMyRating;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 툴바 Up 버튼 추가

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userId = pref.getInt("userId", 0);

        rbMyRating = (RatingBar) findViewById(R.id.my_rating);
        rbMyRating.setOnRatingBarChangeListener(this);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        String placeId = getIntent().getStringExtra("placeId");
        int myRating = (int) rating;

        if (userId != 0) {
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
        } else {
            Toast.makeText(this, "로그인 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
