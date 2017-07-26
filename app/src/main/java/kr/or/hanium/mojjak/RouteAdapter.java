package kr.or.hanium.mojjak;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// Adapter: 아이템 데이터와 뷰 간의 처리
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {
    private List<Route> routeList;
    private int itemLayout;

    public RouteAdapter(List<Route> items, int itemLayout) {
        this.routeList = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Route item = routeList.get(position);
        viewHolder.textTitle.setText(item.getTitle());
        viewHolder.duration.setText(item.getDuration());
        viewHolder.artist.setText(item.getArtist());
        viewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView duration;
        public TextView textTitle;
        public TextView artist;

        public ViewHolder(View itemView) {
            super(itemView);

            duration = (TextView) itemView.findViewById(R.id.imgProfile);
            textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            artist = (TextView) itemView.findViewById(R.id.textArtist);
        }
    }
}
