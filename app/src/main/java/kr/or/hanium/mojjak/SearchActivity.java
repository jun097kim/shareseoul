package kr.or.hanium.mojjak;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends Activity implements TextWatcher {

    ArrayList<sItem> list;
    ArrayAdapter<String> adapter;
    AutoCompleteTextView autoCompleteTextView;
    private SearchAPIService mSearchAPIService;
    private TextView mSearchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pracitce);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.addTextChangedListener(this);
        String text = autoCompleteTextView.getText().toString();
        // 다음 로컬 API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchAPIService.API_URL)
                .addConverterFactory(GsonConverterFactory.create()) // JSON Converter 지정
                .build();
        mSearchAPIService = retrofit.create(SearchAPIService.class);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mSearchAPIService.getPlaces("12b7b22a1af7d9e2173ac88f2af8654f", s.toString()).enqueue(new Callback<SearchAPIResponse>() {
            @Override
            public void onResponse(Call<SearchAPIResponse> call, Response<SearchAPIResponse> response) {
                if (response.body().getChannel() != null) {
                    list = response.body().getChannel().getItem();
                    ArrayList<String> resultList = new ArrayList<String>();
                    for (sItem sItem : list) {
                        resultList.add(sItem.getTitle());
                    }

                    autoCompleteTextView.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_dropdown_item_1line, resultList));
                }
            }

            @Override
            public void onFailure(Call<SearchAPIResponse> call, Throwable t) {
                Log.i("info", t.getMessage());

            }
        });
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


}
