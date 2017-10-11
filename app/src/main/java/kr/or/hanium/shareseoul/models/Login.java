package kr.or.hanium.shareseoul.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("result")
    @Expose
    private Boolean result;

    public int getId() {
        return id;
    }

    public Boolean getResult() {
        return result;
    }
}
