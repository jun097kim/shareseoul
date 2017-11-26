package kr.or.hanium.shareseoul.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.adapters.SearchAdapter;
import kr.or.hanium.shareseoul.interfaces.SearchService;
import kr.or.hanium.shareseoul.models.Search;
import kr.or.hanium.shareseoul.models.Search.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        overridePendingTransition(0, 0);    // 전환 애니메이션 없애기

        final RecyclerView rvSearch = findViewById(R.id.rv_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSearch.setLayoutManager(linearLayoutManager);

        final FloatingSearchView mSearchView = findViewById(R.id.sv_bike);

        AppBarLayout mAppBar = findViewById(R.id.appbar);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSearchView.setTranslationY(verticalOffset);
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SearchService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SearchService searchService = retrofit.create(SearchService.class);

                Call<Search> searchCall = searchService.getPlaces("37.56,126.97", "50000", currentQuery, getString(R.string.google_maps_key));
                searchCall.enqueue(new Callback<Search>() {
                    @Override
                    public void onResponse(Call<Search> call, Response<Search> response) {
                        List<Results> resultList = response.body().getResults();
                        SearchAdapter searchAdapter = new SearchAdapter(resultList, SearchActivity.this);
                        rvSearch.setAdapter(searchAdapter);
                    }

                    @Override
                    public void onFailure(Call<Search> call, Throwable t) {
                        Toast.makeText(SearchActivity.this, "인터넷 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                finish();
            }
        });
    }
}
