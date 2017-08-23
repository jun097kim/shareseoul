package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.Direction;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionsService {
    String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";  // public static final 생략

    @GET("json")
    Call<Direction> getDirections(@Query("origin")
                                          String origin,
                                  @Query("destination")
                                          String destination,
                                  @Query("mode")
                                          String mode,
                                  @Query("language")
                                          String language,
                                  @Query("key")
                                          String key);
}
