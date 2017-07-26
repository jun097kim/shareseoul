package kr.or.hanium.mojjak;

import android.os.Parcel;

/**
 * Created by soyeon on 2017. 7. 22..
 */

public class SearchSuggestion implements com.arlib.floatingsearchview.suggestions.model.SearchSuggestion {
    @Override
    public String getBody() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
