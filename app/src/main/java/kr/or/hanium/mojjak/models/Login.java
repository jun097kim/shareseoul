package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("result")
    @Expose
    private Boolean result;

    public Boolean getResult() {
        return result;
    }
}
