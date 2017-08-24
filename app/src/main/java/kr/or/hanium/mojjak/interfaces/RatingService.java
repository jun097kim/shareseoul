package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.Rating;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RatingService {
    String BASE_URL = "http://jun0.kim:8080/";  // public static final 생략

    @FormUrlEncoded
    @POST("rating_proc.jsp")
    Call<Rating> getSuccess(@Field("user_id")
                                    int userId,
                            @Field("place_id")
                                    String placeId,
                            @Field("my_rating")
                                    int myRating);
}
