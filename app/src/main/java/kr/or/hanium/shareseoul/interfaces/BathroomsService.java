package kr.or.hanium.shareseoul.interfaces;

import java.util.List;

import kr.or.hanium.shareseoul.models.Bathroom;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BathroomsService {
    String BASE_URL = "http://jun0.kim:8080/";  // public static final 생략

    @GET("bathrooms")
    Call<List<Bathroom>> getBathrooms(@Query("latitude") double latitude, @Query("longitude") double longitude);
}
