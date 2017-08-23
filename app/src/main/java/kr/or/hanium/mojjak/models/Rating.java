package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rating {
    @SerializedName("success")
    @Expose
    Boolean success;

    public Boolean getSuccess() {
        return success;
    }
}
