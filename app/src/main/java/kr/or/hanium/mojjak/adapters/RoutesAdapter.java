package kr.or.hanium.mojjak.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.or.hanium.mojjak.R;
import kr.or.hanium.mojjak.models.Route;

// Adapter: 아이템 데이터와 뷰 간의 처리
public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RoutesViewHolder> {
    private List<Route> routeList;
    private int itemLayout;

    public RoutesAdapter(List<Route> items, int itemLayout) {
        this.routeList = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public RoutesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new RoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoutesViewHolder routesViewHolder, int position) {
        Route item = routeList.get(position);
        routesViewHolder.textTitle.setText(item.getTitle());
        routesViewHolder.duration.setText(item.getDuration());
        routesViewHolder.artist.setText(item.getArtist());
        routesViewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RoutesViewHolder extends RecyclerView.ViewHolder {
        public TextView duration;
        public TextView textTitle;
        public TextView artist;

        public RoutesViewHolder(View itemView) {
            super(itemView);

            duration = itemView.findViewById(R.id.imgProfile);
            textTitle = itemView.findViewById(R.id.textTitle);
            artist = itemView.findViewById(R.id.textArtist);
        }
    }
}
