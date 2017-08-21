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
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    private List<Route> routeList;
    private int itemLayout;

    public RouteAdapter(List<Route> items, int itemLayout) {
        this.routeList = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder routeViewHolder, int position) {
        Route item = routeList.get(position);
        routeViewHolder.textTitle.setText(item.getTitle());
        routeViewHolder.duration.setText(item.getDuration());
        routeViewHolder.artist.setText(item.getArtist());
        routeViewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        public TextView duration;
        public TextView textTitle;
        public TextView artist;

        public RouteViewHolder(View itemView) {
            super(itemView);

            duration = (TextView) itemView.findViewById(R.id.imgProfile);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            artist = (TextView) itemView.findViewById(R.id.textArtist);
        }
    }
}
