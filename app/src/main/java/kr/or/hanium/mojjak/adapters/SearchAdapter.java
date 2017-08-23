package kr.or.hanium.mojjak.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.models.Search.Results;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    List<Results> resultsList;

    public SearchAdapter(List<Results> resultsList) {
        this.resultsList = resultsList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(view);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        holder.tvSearch.setText(resultsList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearch;

        public SearchViewHolder(View itemView) {
            super(itemView);
            tvSearch = itemView.findViewById(R.id.tv_search);
        }
    }
}
