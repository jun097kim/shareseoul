package kr.or.hanium.shareseoul.interfaces;

import kr.or.hanium.shareseoul.models.Restaurants;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantsService {
    String BASE_URL = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/";  // public static final 생략

    @GET("locationBasedList?ServiceKey=YFJzrf1KjCXsqBIcJSLnVd21BLUav7pKq8G6iaK9wHJU2j0HAme%2FQhUnCV5DG3Z2CA1oVP9Sojz5Kd6suef1Kw%3D%3D")
    Call<Restaurants> getRestaurants(@Query("numOfRows")
                                             int numOfRows,
                                     @Query("pageNo")
                                             int pageNo,
                                     @Query("startPage")
                                             int startPage,
                                     @Query("MobileOS")
                                             String MobileOS,
                                     @Query("MobileApp")
                                             String MobileApp,
                                     @Query("arrange")
                                             char arrange,
                                     @Query("contentTypeId")
                                             int contentTypeId,
                                     @Query("mapX")
                                             double mapX,
                                     @Query("mapY")
                                             double mapY,
                                     @Query("radius")
                                             int radius,
                                     @Query("listYN")
                                             char listYN,
                                     @Query("_type")
                                             String _type);
}
