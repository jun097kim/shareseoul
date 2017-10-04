package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jun097kim on 2017-09-26.
 */

public class BathroomDetail {
    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("value")
    @Expose
    String value;
}
