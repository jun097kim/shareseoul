package kr.or.hanium.shareseoul.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.activities.DirectionsActivity;
import kr.or.hanium.shareseoul.models.Search.Results;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    List<Results> resultsList;
    Activity activity;

    public SearchAdapter(List<Results> resultsList, Activity activity) {
        this.resultsList = resultsList;
        this.activity = activity;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(view, activity);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.tvSearch.setText(resultsList.get(position).getName());
        holder.cvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DirectionsActivity.class);
                intent.putExtra("result", resultsList.get(position).getName());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearch;
        CardView cvSearch;
        Activity activity;

        public SearchViewHolder(View view, Activity activity) {
            super(view);
            tvSearch = itemView.findViewById(R.id.tv_search);
            cvSearch = itemView.findViewById(R.id.cv_search);
            this.activity = activity;
        }
    }
}
