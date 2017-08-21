package kr.or.hanium.mojjak.interfaces;

import java.util.List;

import kr.or.hanium.mojjak.models.Bike;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BikesAPI {
    String API_URL = "http://jun0.kim:8080/";  // public static final 생략

    @GET("bikes")
    Call<List<Bike>> getBike(@Query("latitude") String latitude, @Query("longitude") String longitude);
}
