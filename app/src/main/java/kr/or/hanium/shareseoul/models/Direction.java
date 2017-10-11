package kr.or.hanium.shareseoul.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Direction {
    @SerializedName("routes")
    @Expose
    private List<Routes> routes;

    public List<Routes> getRoutes() {
        return routes;
    }

    public class Routes {
        @SerializedName("legs")
        @Expose
        private List<Legs> legs;

        public List<Legs> getLegs() {
            return legs;
        }
    }

    public class Legs {
        @SerializedName("steps")
        @Expose
        private List<Steps> steps;

        public List<Steps> getSteps() {
            return steps;
        }
    }

    public class Steps {
        @SerializedName("travel_mode")
        @Expose
        private String travelMode;

        @SerializedName("duration")
        @Expose
        private Duration duration;

        @SerializedName("transit_details")
        @Expose
        private TransitDetails transitDetails;

        @SerializedName("html_instructions")
        @Expose
        private String htmlInstructions;

        public String getTravelMode() {
            return travelMode;
        }

        public Duration getDuration() {
            return duration;
        }

        public TransitDetails getTransitDetails() {
            return transitDetails;
        }

        public String getHtmlInstructions() {
            return htmlInstructions;
        }
    }

    public class Duration {
        @SerializedName("text")
        @Expose
        private String text;

        public String getText() {
            return text;
        }
    }

    public class TransitDetails {
        @SerializedName("line")
        @Expose
        private Line line;

        @SerializedName("departure_stop")
        @Expose
        private DepartureStop departureStop;

        @SerializedName("arrival_stop")
        @Expose
        private ArrivalStop arrivalStop;

        @SerializedName("headsign")
        @Expose
        private String headsign;

        public Line getLine() {
            return line;
        }

        public DepartureStop getDepartureStop() {
            return departureStop;
        }

        public ArrivalStop getArrivalStop() {
            return arrivalStop;
        }

        public String getHeadsign() {
            return headsign;
        }
    }

    public class Line {
        @SerializedName("vehicle")
        @Expose
        private Vehicle vehicle;

        @SerializedName("short_name")
        @Expose
        private String shortName;

        @SerializedName("color")
        @Expose
        private String color;

        public Vehicle getVehicle() {
            return vehicle;
        }

        public String getShortName() {
            return shortName;
        }

        public String getColor() {
            return color;
        }
    }

    public class DepartureStop {
        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }
    }

    public class ArrivalStop extends DepartureStop {
    }

    public class Vehicle {
        @SerializedName("type")
        @Expose
        private String type;

        public String getType() {
            return type;
        }
    }
}
