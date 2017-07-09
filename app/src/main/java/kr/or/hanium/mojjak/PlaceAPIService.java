package kr.or.hanium.mojjak;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceAPIService {
    String API_URL = "https://apis.daum.net/local/v1/search/"; // public static final 생략

    @GET("keyword.json")
    Call<PlaceAPIResponse> getSuccess(@Query("apikey")
                                           String apikey,
                                      @Query("query")
                                           String query,
                                      @Query("location")
                                           String location,
                                      @Query("radius")
                                           int radius);
}
