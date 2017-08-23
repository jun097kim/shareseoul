package kr.or.hanium.mojjak.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

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
        String firstLetter = String.valueOf(bikes.get(position).getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();
        // generate color based on a key (same key returns the same color), useful for list/grid views
//        int color = generator.getColor(firstLetter);

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color);

        holder.ivBikesItem.setImageDrawable(drawable);
        holder.tvBikes.setText(bikes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        // 모델 객체의 크기 전달
        return bikes.size();
    }

    public static class BikesViewHolder extends RecyclerView.ViewHolder {
        public CardView cvBikes;
        public ImageView ivBikesItem;
        public TextView tvBikes;

        public BikesViewHolder(View view) {
            super(view);
            cvBikes = view.findViewById(R.id.cv_bikes);
            ivBikesItem = view.findViewById(R.id.iv_bikes_item);
            tvBikes = view.findViewById(R.id.tv_bikes);
        }
    }
}
