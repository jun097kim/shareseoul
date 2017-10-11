package kr.or.hanium.shareseoul.interfaces;

import java.util.List;

import kr.or.hanium.shareseoul.models.Bike;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BikesService {
    String BASE_URL = "http://jun0.kim:8080/";  // public static final 생략

    @GET("bikes")
    Call<List<Bike>> getBikes(@Query("latitude") double latitude, @Query("longitude") double longitude);
}
