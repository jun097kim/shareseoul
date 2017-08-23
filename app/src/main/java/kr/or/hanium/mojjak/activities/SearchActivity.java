package kr.or.hanium.mojjak.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.adapters.SearchAdapter;
import kr.or.hanium.mojjak.interfaces.SearchService;
import kr.or.hanium.mojjak.models.Search;
import kr.or.hanium.mojjak.models.Search.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 툴바 Up 버튼 추가

        final RecyclerView rvSearch = (RecyclerView) findViewById(R.id.rv_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSearch.setLayoutManager(linearLayoutManager);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SearchService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SearchService searchService = retrofit.create(SearchService.class);

                Call<Search> searchCall = searchService.getPlaces("37.56,126.97", "50000", query, getString(R.string.google_api_key));
                searchCall.enqueue(new Callback<Search>() {
                    @Override
                    public void onResponse(Call<Search> call, Response<Search> response) {
                        List<Results> resultList = response.body().getResults();
                        SearchAdapter searchAdapter = new SearchAdapter(resultList);
                        rvSearch.setAdapter(searchAdapter);
                    }

                    @Override
                    public void onFailure(Call<Search> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }
}
