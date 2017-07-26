package kr.or.hanium.mojjak;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by soyeon on 2017. 7. 26..
 */

public class RatingAPIResponse {
    @SerializedName("success")
    @Expose
    Boolean success;

    public Boolean getSuccess() {
        return success;
    }
}
