package kr.or.hanium.mojjak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener {

    private DirectionsAPIService directionsAPIService;
    private RecyclerView routeRecyclerView;
    private ArrayList<Album> albumList = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;
    private TextView origin;
    private TextView destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 Up 버튼 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // 툴바 타이틀 없애기

        Button searchRouteBtn = (Button) findViewById(R.id.search_route_btn);
        ImageButton swapBtn = (ImageButton) findViewById(R.id.swap_btn);
        origin = (TextView) findViewById(R.id.origin);
        destination = (TextView) findViewById(R.id.destination);

        searchRouteBtn.setOnClickListener(this);
        swapBtn.setOnClickListener(this);
        origin.setOnClickListener(this);
        destination.setOnClickListener(this);

        // 구글 길찾기 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DirectionsAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        directionsAPIService = retrofit.create(DirectionsAPIService.class);

        initLayout();
        recyclerAdapter = new RecyclerAdapter(albumList, R.layout.list_item);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SearchActivity.class);

        switch (v.getId()) {
            case R.id.origin:
                intent.putExtra("searchType","origin");
                startActivity(intent);
                break;
            case R.id.destination:
                intent.putExtra("searchType","destination");
                startActivity(intent);
                break;
            case R.id.search_route_btn:
                int size = albumList.size();
                albumList.clear();
                recyclerAdapter.notifyItemRangeRemoved(0, size);

                directionsAPIService.getDirections(origin.getText().toString(), destination.getText().toString(), "transit", "AIzaSyCaZwAmlCPR6PtvluVU1AVuC8B0b8JkKak", "ko").enqueue(new Callback<DirectionsAPIResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsAPIResponse> call, Response<DirectionsAPIResponse> response) {
                        for (Routes routes : response.body().getRoutes()) {
                            for (Legs legs : routes.getLegs()) {
                                for (Steps steps : legs.getSteps()) {
                                    String duration = steps.getDuration().getText();

                                    if (!(steps.getTransitDetails() == null)) {
                                        String departureStop = steps.getTransitDetails().getDepartureStop().getName();
                                        String arrivalStop = steps.getTransitDetails().getArrivalStop().getName();

                                        initData(duration, departureStop, arrivalStop);
                                    } else {
                                        String walkingDistance = steps.getSteps().get(0).getDistance().getText();
                                        String walkingDuration = steps.getSteps().get(0).getDuration().getText();

                                        initData(duration, walkingDuration, walkingDistance);
                                    }
                                }
                            }
                        }
                        routeRecyclerView.setAdapter(recyclerAdapter);
                        routeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        routeRecyclerView.addItemDecoration(new DividerItemDecoration(RouteActivity.this, LinearLayoutManager.VERTICAL));  // 구분선
                    }

                    @Override
                    public void onFailure(Call<DirectionsAPIResponse> call, Throwable t) {

                    }
                });
                break;

            case R.id.swap_btn:
                String temp = origin.getText().toString();
                origin.setText(destination.getText());
                destination.setText(temp);

                break;
        }

    }


    private void initLayout() {
        routeRecyclerView = (RecyclerView) findViewById(R.id.route_recycler_view);
    }

    // 데이터 초기화
    private void initData(String duration, String a, String b) {
        Album album = new Album();
        album.setTitle(a);
        album.setArtist(b);
        album.setDuration(duration);
        albumList.add(album);
    }
}
