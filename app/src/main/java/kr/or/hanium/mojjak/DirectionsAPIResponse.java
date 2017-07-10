package kr.or.hanium.mojjak;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DirectionsAPIResponse {
    @SerializedName("routes")
    @Expose
    private ArrayList<Routes> routes;

    public ArrayList<Routes> getRoutes() {
        return routes;
    }
}

class Routes {
    @SerializedName("legs")
    @Expose
    private ArrayList<Legs> legs;

    public ArrayList<Legs> getLegs() {
        return legs;
    }
}

class Legs {
    @SerializedName("steps")
    @Expose
    private ArrayList<Steps> steps;

    public ArrayList<Steps> getSteps() {
        return steps;
    }
}

class Steps {
    @SerializedName("duration")
    @Expose
    private Duration duration;

    @SerializedName("transit_details")
    @Expose
    private TransitDetails transitDetails;

    @SerializedName("steps")
    @Expose
    private ArrayList<WalkingSteps> steps;

    public Duration getDuration() {
        return duration;
    }

    public TransitDetails getTransitDetails() {
        return transitDetails;
    }

    public ArrayList<WalkingSteps> getSteps() {
        return steps;
    }
}

class Duration {
    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }
}

class TransitDetails {
    @SerializedName("departure_stop")
    @Expose
    private DepartureStop departureStop;

    @SerializedName("arrival_stop")
    @Expose
    private ArrivalStop arrivalStop;

    public DepartureStop getDepartureStop() {
        return departureStop;
    }

    public ArrivalStop getArrivalStop() {
        return arrivalStop;
    }
}

class DepartureStop {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }
}

class ArrivalStop extends DepartureStop {
}


class WalkingSteps {
    @SerializedName("distance")
    @Expose
    private WalkingDistance distance;

    @SerializedName("duration")
    @Expose
    private WalkingDuration duration;

    public WalkingDistance getDistance() {
        return distance;
    }

    public WalkingDuration getDuration() {
        return duration;
    }
}

class WalkingDistance {
    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }
}

class WalkingDuration extends WalkingDistance {

}