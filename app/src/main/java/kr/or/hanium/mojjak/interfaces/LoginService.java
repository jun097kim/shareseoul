package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.Login;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginService {
    String BASE_URL = "http://jun0.kim:8080/";  // public static final 생략

    @FormUrlEncoded
    @POST("login_proc.jsp")
    Call<Login> getLogin(@Field("email") String email, @Field("password") String password);
}
