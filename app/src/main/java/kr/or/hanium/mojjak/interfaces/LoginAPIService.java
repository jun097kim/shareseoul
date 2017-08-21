package kr.or.hanium.mojjak.interfaces;

import kr.or.hanium.mojjak.models.LoginAPIResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginAPIService {
    String API_URL = "http://jun0.kim:8080/";

    @FormUrlEncoded
    @POST("login_proc.jsp")
    Call<LoginAPIResponse> getLogin(@Field("email")
                                            String email,
                                    @Field("password")
                                            String password);
}
