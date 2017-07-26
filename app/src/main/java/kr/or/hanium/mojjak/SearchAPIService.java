package kr.or.hanium.mojjak;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by soyeon on 2017. 7. 22..
 */

public interface SearchAPIService {
    String API_URL = "https://apis.daum.net/local/v1/search/"; // public static final 생략

    @GET("keyword.json")
    Call<SearchAPIResponse> getPlaces(@Query("apikey")
                                              String apikey,
                                      @Query("query")
                                              String query);

}
