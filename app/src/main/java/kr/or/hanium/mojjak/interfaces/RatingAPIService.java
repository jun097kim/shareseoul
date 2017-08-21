package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.RatingAPIResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by soyeon on 2017. 7. 26..
 */

public interface RatingAPIService {
    String API_URL = "http://jun0.kim:8080/";

    @FormUrlEncoded
    @POST("rating_proc.jsp")
    Call<RatingAPIResponse> getSuccess(@Field("place_id")
                                               String placeID,
                                       @Field("my_rating")
                                               int myRating);
}


