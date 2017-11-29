package kr.or.hanium.shareseoul.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.activities.PlaceActivity;
import kr.or.hanium.shareseoul.models.Bathroom;

/**
 * Created by jun097kim on 17. 11. 28.
 */

public class BathroomsAdapter extends RecyclerView.Adapter<BathroomsAdapter.BathroomsViewHolder> {
    private List<Bathroom> bathrooms;
    private Activity activity;

    public BathroomsAdapter(List<Bathroom> bathrooms, Activity activity) {
        this.bathrooms = bathrooms;
        this.activity = activity;
    }

    @Override
    public BathroomsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new BathroomsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BathroomsViewHolder holder, final int position) {
        String firstLetter = String.valueOf(bathrooms.get(position).getName().trim().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color);

        int time = (int) (bathrooms.get(position).getDistance() * 15);

        holder.tvNumber.setText(Integer.toString(position + 1));
        holder.ivBike.setImageDrawable(drawable);
        holder.tvName.setText(bathrooms.get(position).getName().trim());
        holder.tvTime.setText("도보 약 " + time + "분");

        holder.cvBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PlaceActivity.class);
                intent.putExtra("placeType", "bathrooms");
                intent.putExtra("placeName", bathrooms.get(position).getName());
                intent.putExtra("placeId", bathrooms.get(position).getId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bathrooms.size();
    }

    static class BathroomsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout cvBike;
        TextView tvNumber;
        ImageView ivBike;
        TextView tvName;
        TextView tvTime;

        public BathroomsViewHolder(View itemView) {
            super(itemView);

            cvBike = itemView.findViewById(R.id.cv_bike);
            tvNumber = itemView.findViewById(R.id.tv_number);
            ivBike = itemView.findViewById(R.id.iv_bike);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
