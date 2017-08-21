package kr.or.hanium.mojjak.interfaces;

import java.util.List;

import kr.or.hanium.mojjak.models.Bathroom;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BathroomsService {
    String API_URL = "http://jun0.kim:8080/";  // public static final 생략

    @GET("bathrooms")
    Call<List<Bathroom>> getPlaces(@Query("latitude") double latitude, @Query("longitude") double longitude);
}
