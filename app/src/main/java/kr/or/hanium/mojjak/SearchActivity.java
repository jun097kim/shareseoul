package kr.or.hanium.mojjak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class SearchActivity extends AppCompatActivity {

    String mSearchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 자동완성 위젯 추가
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        // 힌트 텍스트 설정
        mSearchType = getIntent().getExtras().getString("searchType");
        String hint = null;

        switch (mSearchType) {
            case "normal":
                hint = "장소, 버스, 지하철 검색";
                break;
            case "origin":
                hint = "출발지 검색";
                break;
            case "destination":
                hint = "도착지 검색";
        }

        autocompleteFragment.setHint(hint);


        // 한국으로 자동완성 결과 제한
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("KR")
                .build();

        autocompleteFragment.setFilter(typeFilter);


        // 검색 결과 아이템을 눌렀을 때 호출
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (mSearchType.equals("normal")) {
                    Toast.makeText(SearchActivity.this, place.getName() + "\n" + place.getAddress(), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("result", place.getName());

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onError(Status status) {
            }
        });

    }


    // Back 버튼을 눌렀을 때 호출
    @Override
    public void onBackPressed() {
        if (!mSearchType.equals("normal")) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
        }
        super.onBackPressed();
    }

}
