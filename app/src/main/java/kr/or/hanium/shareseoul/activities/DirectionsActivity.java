package kr.or.hanium.shareseoul.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.adapters.DirectionsAdapter;
import kr.or.hanium.shareseoul.interfaces.DirectionsService;
import kr.or.hanium.shareseoul.models.Direction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ORIGIN = 0;
    private static final int REQUEST_CODE_DESTINATION = 1;

    private TextView mOrigin;
    private TextView mDestination;
    private DirectionsService mDirectionsService;
    private RecyclerView rvDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 Up 버튼 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // 툴바 타이틀 없애기

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        ImageButton swapBtn = findViewById(R.id.swap_btn);
        mOrigin = findViewById(R.id.origin);
        mDestination = findViewById(R.id.destination);

        swapBtn.setOnClickListener(this);
        mOrigin.setOnClickListener(this);
        mDestination.setOnClickListener(this);

        // 구글 길찾기 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DirectionsService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        mDirectionsService = retrofit.create(DirectionsService.class);

        rvDirections = findViewById(R.id.rv_directions);

        // 레이아웃 매니저 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvDirections.setLayoutManager(linearLayoutManager);


        Intent intent = getIntent();

        String setType = intent.getStringExtra("setType");
        String placeName = intent.getStringExtra("placeName");

        if (setType != null) {
            switch (setType) {
                case "origin":
                    mOrigin.setText(placeName);
                    break;
                case "destination":
                    mDestination.setText(placeName);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SearchActivity.class);

        switch (v.getId()) {
            case R.id.origin:
                intent.putExtra("searchType", "origin");
                startActivityForResult(intent, REQUEST_CODE_ORIGIN);    // SearchActivity finish -> 결과 받는 onActivityResult 호출
                break;
            case R.id.destination:
                intent.putExtra("searchType", "destination");
                startActivityForResult(intent, REQUEST_CODE_DESTINATION);
                break;
            case R.id.swap_btn:
                String temp = mOrigin.getText().toString();
                mOrigin.setText(mDestination.getText());
                mDestination.setText(temp);
                if (!TextUtils.isEmpty(mOrigin.getText()) && !TextUtils.isEmpty(mDestination.getText()))
                    showDirection();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ORIGIN:
                    mOrigin.setText(data.getStringExtra("result"));
                    if (!TextUtils.isEmpty(mDestination.getText()))
                        showDirection();
                    break;
                case REQUEST_CODE_DESTINATION:
                    mDestination.setText(data.getStringExtra("result"));
                    if (!TextUtils.isEmpty(mOrigin.getText()))
                        showDirection();
            }
        }
    }

    private void showDirection() {
        Call<Direction> directionCall = mDirectionsService.getDirections(mOrigin.getText().toString(),
                mDestination.getText().toString(), "transit", "ko", getString(R.string.google_maps_key));
        directionCall.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                List<Direction.Routes> routes = response.body().getRoutes();

                if (routes.size() > 0) {
                    List<Direction.Steps> stepsList = routes.get(0).getLegs().get(0).getSteps();
                    DirectionsAdapter adapter = new DirectionsAdapter(stepsList, mDestination.getText().toString());
                    rvDirections.setAdapter(adapter);
                } else {
                    Toast.makeText(DirectionsActivity.this, "경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Toast.makeText(DirectionsActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
