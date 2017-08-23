package kr.or.hanium.mojjak.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Search {
    @SerializedName("results")
    @Expose
    private List<Results> results;

    public List<Results> getResults() {
        return results;
    }

    public class Results {
        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }
    }
}
