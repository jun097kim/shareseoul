package kr.or.hanium.shareseoul.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("percentage")
    @Expose
    public int percentage;

    @SerializedName("success")
    @Expose
    Boolean success;

    public int getPercentage() {
        return percentage;
    }

    public Boolean getSuccess() {
        return success;
    }
}
