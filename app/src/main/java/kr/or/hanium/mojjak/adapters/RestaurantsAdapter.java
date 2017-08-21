package kr.or.hanium.mojjak.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.models.Restaurant;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.RestaurantsViewHolder> {
    private ArrayList<Restaurant.Item> mItem;
    private Context mContext;

    public RestaurantsAdapter(ArrayList<Restaurant.Item> mItem, Context mContext) {
        this.mItem = mItem;
        this.mContext = mContext;
    }

    @Override
    public RestaurantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_restaurants, parent, false);
        RestaurantsViewHolder restaurantsViewHolder = new RestaurantsViewHolder(view);
        return restaurantsViewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantsViewHolder holder, int position) {
        holder.mTextView.setText(mItem.get(position).getTitle());
        Picasso.with(mContext).load(mItem.get(position).getImageUrl()).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return (mItem == null) ? 0 : mItem.size();
    }

    public static class RestaurantsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTextView;

        public RestaurantsViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.food_img);
            mTextView = (TextView) view.findViewById(R.id.food_title);
        }
    }
}

