package kr.or.hanium.mojjak.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.models.Bike;

public class BikesAdapter extends RecyclerView.Adapter<BikesAdapter.BikesViewHolder> {
    List<Bike> bikes;

    public BikesAdapter(List<Bike> bikes) {
        this.bikes = bikes;
    }

    @Override
    public BikesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bikes, parent, false);
        BikesViewHolder bikesViewHolder = new BikesViewHolder(viewItem);
        return bikesViewHolder;
    }

    @Override
    public void onBindViewHolder(BikesViewHolder holder, int position) {
        holder.tvBikes.setText(bikes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        // 모델 객체의 크기 전달
        return bikes.size();
    }

    public static class BikesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_bikes)
        CardView cvBikes;
        @BindView(R.id.iv_bikes_item)
        ImageView ivBikesItem;
        @BindView(R.id.tv_bikes)
        TextView tvBikes;

        BikesViewHolder(View viewItem) {
            super(viewItem);
            ButterKnife.bind(this, viewItem);
        }
    }
}
