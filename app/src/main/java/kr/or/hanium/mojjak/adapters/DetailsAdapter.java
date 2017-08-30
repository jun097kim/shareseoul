package kr.or.hanium.mojjak.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.or.hanium.mojjak.R;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {
    String[] detailNames;
    String[] detailValues;

    public DetailsAdapter(String[] detailNames, String[] detailValues) {
        this.detailNames = detailNames;
        this.detailValues = detailValues;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        DetailsViewHolder detailsViewHolder = new DetailsViewHolder(view);
        return detailsViewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {
        holder.tvName.setText(detailNames[position]);
        holder.tvValue.setText(detailValues[position]);
    }

    @Override
    public int getItemCount() {
        return detailNames.length;
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvValue;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_detail_name);
            tvValue = itemView.findViewById(R.id.tv_detail_value);
        }
    }
}
