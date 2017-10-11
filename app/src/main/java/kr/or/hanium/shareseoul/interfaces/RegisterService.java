package kr.or.hanium.shareseoul.interfaces;

import kr.or.hanium.shareseoul.models.Register;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterService {
    String BASE_URL = "http://jun0.kim:8080/";  // public static final 생략

    @FormUrlEncoded
    @POST("register_proc.jsp")
    Call<Register> getRegister(@Field("email")
                                                  String email,
                               @Field("password")
                                                  String password,
                               @Field("password_confirm")
                                                  String passwordConfirm);
}
