package kr.or.hanium.mojjak;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionsAPIService {
    String API_URL = "https://maps.googleapis.com/maps/api/directions/"; // public static final 생략

    @GET("json")
    Call<DirectionsAPIResponse> getDirections(@Query("origin")
                                                      String origin,
                                              @Query("destination")
                                                      String destination,
                                              @Query("mode")
                                                      String mode,
                                              @Query("key")
                                                      String key,
                                              @Query("language")
                                                      String language);
}
