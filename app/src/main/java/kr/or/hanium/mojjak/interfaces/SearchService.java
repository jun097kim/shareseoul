package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.Search;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";  // public static final 생략

    @GET("json")
    Call<Search> getPlaces(@Query("location")
                                              String location,
                           @Query("radius")
                                              String radius,
                           @Query("keyword")
                                              String keyword,
                           @Query("key")
                                              String key);
}
