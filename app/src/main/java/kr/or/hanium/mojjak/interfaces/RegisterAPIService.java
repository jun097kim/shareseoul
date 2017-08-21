package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.RegisterAPIResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPIService {
    String API_URL = "http://jun0.kim:8080/";

    @FormUrlEncoded
    @POST("register_proc.jsp")
    Call<RegisterAPIResponse> getRegister(@Field("email")
                                                  String email,
                                          @Field("password")
                                                  String password,
                                          @Field("password_confirm")
                                                  String passwordConfirm);
}
