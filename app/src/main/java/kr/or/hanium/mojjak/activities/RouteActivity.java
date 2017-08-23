package kr.or.hanium.mojjak.activities;

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
import android.widget.Toast;

import java.util.ArrayList;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.adapters.RoutesAdapter;
import kr.or.hanium.mojjak.interfaces.DirectionsService;
import kr.or.hanium.mojjak.models.Direction;
import kr.or.hanium.mojjak.models.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ORIGIN = 0;
    private static final int REQUEST_CODE_DESTINATION = 1;
    private DirectionsService mDirectionsService;
    private RecyclerView mRouteRecyclerView;
    private ArrayList<Route> mRouteList = new ArrayList<>();
    private RoutesAdapter mRoutesAdapter;
    private TextView mOrigin;
    private TextView mDestination;
    private ArrayList<String> durationList = new ArrayList<>(); // 총 걸리는 시간
    private ArrayList<Integer> walkingDurationList = new ArrayList<>(); // 총 도보 시간

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
                .baseUrl(DirectionsService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        mDirectionsService = retrofit.create(DirectionsService.class);

        initLayout();
        mRoutesAdapter = new RoutesAdapter(mRouteList, R.layout.route_list_item);

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
        mRouteRecyclerView = (RecyclerView) findViewById(R.id.rv_route);
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
        mRoutesAdapter.notifyItemRangeRemoved(0, size);

        Call<Direction> directionCall = mDirectionsService.getDirections(mOrigin.getText().toString(), mDestination.getText().toString(), "transit", "ko", getString(R.string.google_api_key));
        directionCall.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {

                ArrayList<Direction.Steps> steps = response.body().getRoutes().get(0).getLegs().get(0).getSteps();
                Integer WalkingDuration = 0;

                for (Direction.Steps s : steps) {
                    switch (s.getTravelmode()) {

                        case "TRANSIT":
                            Log.i("route", "대중교통 경로");
//                            Log.i("route", "Distance: " + s.getDistance().getText()); //거리
//                            Log.i("route", "Duration" + s.getDuration().getText());   //시간
//                            Log.i("route", "HTML Instruc" + s.getHtmlInstructions()); // ~~까지 도보 / ~무슨 행
                            Log.i("route", s.getTransitDetails().getArrivalStop().getName());
                            Log.i("route", "> " + s.getTransitDetails().getDepartureStop().getName() + "하차");
                            //Log.i("route", "버스 " + s.getTransitDetails().getLine().getShort_name() + "탑승");

                            break;
                        case "WALKING":
                            Log.i("route", "도보 ");
//                            Log.i("route", "Distance: " + s.getDistance().getText());
                            WalkingDuration += Integer.parseInt(s.getDuration().getText());
                            Log.i("route", WalkingDuration + "분");
//                            Log.i("route", "HTML Instruc" + s.getHtmlInstructions());

                            break;
                    }
                }

                String duration = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();

                durationList.add(duration); // 총 걸리는 시간
                walkingDurationList.add(WalkingDuration); // 총 도보 시간

                Toast.makeText(RouteActivity.this, "총 걸리는 시간"+ duration.indexOf(0), Toast.LENGTH_LONG).show();
                Toast.makeText(RouteActivity.this, "총 도보 시간"+ walkingDurationList.indexOf(0), Toast.LENGTH_LONG).show();



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
                mRouteRecyclerView.setAdapter(mRoutesAdapter);
                mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mRouteRecyclerView.addItemDecoration(new DividerItemDecoration(RouteActivity.this, LinearLayoutManager.VERTICAL));  // 구분선
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Toast.makeText(RouteActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
