package kr.or.hanium.shareseoul.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kr.or.hanium.shareseoul.R;
import kr.or.hanium.shareseoul.models.Direction;

// Adapter: 아이템 데이터와 뷰 간의 처리
public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.DirectionsViewHolder> {
    private List<Direction.Steps> stepsList;
    private String destination;
    private final static int TRANSIT = 0;
    private final static int WALKING = 1;

    public DirectionsAdapter(List<Direction.Steps> stepsList, String destination) {
        this.stepsList = stepsList;
        this.destination = destination;
    }

    @Override
    public int getItemViewType(int position) {
        switch (stepsList.get(position).getTravelMode()) {
            case "TRANSIT":
                return TRANSIT;
            default:    // "WALKING"
                return WALKING;
        }
    }

    @Override
    public DirectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutRes;
        switch (viewType) {
            case TRANSIT:
                layoutRes = R.layout.item_direction_transit;
                break;
            default:    // WALKING
                layoutRes = R.layout.item_direction_walking;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        DirectionsViewHolder directionsViewHolder = new DirectionsViewHolder(view);
        return directionsViewHolder;
    }

    @Override
    public void onBindViewHolder(DirectionsViewHolder holder, int position) {
        Direction.Steps steps = stepsList.get(position);
        Direction.TransitDetails transitDetails = steps.getTransitDetails();

        if (holder.tvDuration == null) { // 대중교통
            String depatureStop = transitDetails.getDepartureStop().getName();    // 출발지 정류장
            if (transitDetails.getLine().getVehicle().getType().equals("BUS"))
                depatureStop += " 정류장";
            depatureStop += " 승차";

            String shortName = transitDetails.getLine().getShortName();    // 지하철, 버스 노선 번호
            String color = transitDetails.getLine().getColor(); // 노선 색
            String headsign = transitDetails.getHeadsign() + "행"; // 행선지
            String arrivalStop = transitDetails.getArrivalStop().getName() + " 하차"; // 도착지 정류장

            holder.tvDepartureStop.setText(depatureStop);
            holder.tvShortName.setText(shortName);
            holder.tvShortName.setBackgroundColor(Color.parseColor(color));
            holder.tvHeadsign.setText(headsign);
            holder.tvArrivalStop.setText(arrivalStop);
        } else {    // 도보
            String htmlInstructions;
            if (position != getItemCount() - 1) {
                htmlInstructions = steps.getHtmlInstructions();
            } else {    // 마지막 position
                htmlInstructions = destination + "까지 도보";
            }
            String duration = " 약 " + steps.getDuration().getText();
            holder.tvDuration.setText(htmlInstructions + duration);
        }
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    public static class DirectionsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDepartureStop;
        public TextView tvShortName;
        public TextView tvHeadsign;
        public TextView tvArrivalStop;

        // 도보 경로에만 있음
        public TextView tvDuration;

        public DirectionsViewHolder(View view) {
            super(view);
            tvDepartureStop = view.findViewById(R.id.tv_departure_stop);
            tvShortName = view.findViewById(R.id.tv_short_name);
            tvHeadsign = view.findViewById(R.id.tv_headsign);
            tvArrivalStop = view.findViewById(R.id.tv_arrival_stop);
            tvDuration = view.findViewById(R.id.tv_duration);
        }
    }
}
