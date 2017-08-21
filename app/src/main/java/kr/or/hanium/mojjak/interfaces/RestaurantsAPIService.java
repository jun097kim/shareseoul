package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.Restaurant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantsAPIService {
    String API_URL = "https://apis.daum.net/local/v1/search/";  // public static final 생략

    @GET("category.json")
    Call<Restaurant> getPlaces(@Query("apikey")
                                        String apikey,
                               @Query("code")
                                        String code,
                               @Query("location")
                                        String location,
                               @Query("radius")
                                        String radius);
}
