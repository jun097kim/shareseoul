package kr.or.hanium.mojjak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener {

    private DirectionsAPIService mDirectionsAPIService;
    private RecyclerView mRouteRecyclerView;
    private ArrayList<Route> mRouteList = new ArrayList<>();
    private RouteAdapter mRouteAdapter;
    private TextView mOrigin;
    private TextView mDestination;

    private static final int REQUEST_CODE_ORIGIN = 0;
    private static final int REQUEST_CODE_DESTINATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 툴바 Up 버튼 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // 툴바 타이틀 없애기

        ImageButton swapBtn = (ImageButton) findViewById(R.id.swap_btn);
        mOrigin = (TextView) findViewById(R.id.origin);
        mDestination = (TextView) findViewById(R.id.destination);

        swapBtn.setOnClickListener(this);
        mOrigin.setOnClickListener(this);
        mDestination.setOnClickListener(this);

        // 구글 길찾기 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DirectionsAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        mDirectionsAPIService = retrofit.create(DirectionsAPIService.class);

        initLayout();
        mRouteAdapter = new RouteAdapter(mRouteList, R.layout.route_list_item);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SearchActivity.class);

        switch (v.getId()) {

            case R.id.origin:
                intent.putExtra("searchType", "origin");
                startActivityForResult(intent, REQUEST_CODE_ORIGIN);
                // startActivityForResult() : SearchActivity 종료 → 결과 받는 메소드 onActivityResult() 호출

                break;

            case R.id.destination:
                intent.putExtra("searchType", "destination");
                startActivityForResult(intent, REQUEST_CODE_DESTINATION);
                break;

            case R.id.swap_btn:
                String temp = mOrigin.getText().toString();
                mOrigin.setText(mDestination.getText());
                mDestination.setText(temp);

                break;
        }

    }


    private void initLayout() {
        mRouteRecyclerView = (RecyclerView) findViewById(R.id.route_recycler_view);
    }

    // 데이터 초기화
    private void initData(String duration, String a, String b) {
        Route route = new Route();
        route.setTitle(a);
        route.setArtist(b);
        route.setDuration(duration);
        mRouteList.add(route);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ORIGIN:
                    mOrigin.setText(data.getStringExtra("result"));
                    if (!mDestination.getText().equals("")) {
                        showRouteList();
                    }
                    break;

                case REQUEST_CODE_DESTINATION:
                    mDestination.setText(data.getStringExtra("result"));
                    if (!mOrigin.getText().equals("")) {
                        showRouteList();
                    }
            }
        }
    }

    private void showRouteList() {
        int size = mRouteList.size();
        mRouteList.clear();
        mRouteAdapter.notifyItemRangeRemoved(0, size);

        mDirectionsAPIService.getDirections(mOrigin.getText().toString(), mDestination.getText().toString(), "transit", "AIzaSyCaZwAmlCPR6PtvluVU1AVuC8B0b8JkKak", "ko").enqueue(new Callback<DirectionsAPIResponse>() {
            @Override
            public void onResponse(Call<DirectionsAPIResponse> call, Response<DirectionsAPIResponse> response) {

                ArrayList<Steps> steps = response.body().getRoutes().get(0).getLegs().get(0).getSteps();

                Log.i("route", response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                for (Steps s : steps) {
                    switch (s.getTravelmode()) {

                        case "TRANSIT":
                            Log.i("route", "대중교통 경로");
//                            Log.i("route", "Distance: " + s.getDistance().getText()); //거리
//                            Log.i("route", "Duration" + s.getDuration().getText());   //시간
//                            Log.i("route", "HTML Instruc" + s.getHtmlInstructions()); // ~~까지 도보 / ~무슨 행
                    TransitDetails transitDetails = s.getTransitDetails();
                            Log.i("route","("+transitDetails.getArrivalStop().getName()+")");
                            Log.i("route","> "+transitDetails.getDepartureStop().getName()+"하차");
                            Log.i("route", "버스 "+transitDetails.getLine().getShort_name()+"탑승");

                            break;
                        case "WALKING":
//                            Log.i("route", "도보 경로");
//                            Log.i("route", "Distance: " + s.getDistance().getText());
                            Log.i("route", "\n도보 " + s.getDuration().getText());
//                            Log.i("route", "HTML Instruc" + s.getHtmlInstructions());

                            break;
                    }
                }
                //distance duration html_instructions travel_mode
                /*
                for (Routes routes : response.body().getRoutes()) {
                    for (Legs legs : routes.getLegs()) {
                        for (Steps steps : legs.getSteps()) {


                            //String duration = steps.getDuration().getText();



                            if (!(steps.getTransitDetails() == null)) { //travel mode 가 transit 일때
                                String departureStop = steps.getTravelmode();
//                                String departureStop = steps.getTransitDetails().getDepartureStop().getName();
                                String arrivalStop = steps.getTransitDetails().getArrivalStop().getName();

                                initData(duration, departureStop, arrivalStop);
                            } else { //travel mode 가 walking 일때
                                String walkingDistance = steps.getSteps().get(0).getDistance1().getText();
                                String walkingDuration = steps.getSteps().get(0).getDuration1().getText();

                                initData(duration, walkingDuration, walkingDistance);
                            }


                        }
                    }
                }
                */
                mRouteRecyclerView.setAdapter(mRouteAdapter);
                mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRouteRecyclerView.addItemDecoration(new DividerItemDecoration(RouteActivity.this, LinearLayoutManager.VERTICAL));  // 구분선
            }

            @Override
            public void onFailure(Call<DirectionsAPIResponse> call, Throwable t) {

            }
        });
    }
}
