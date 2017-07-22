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
    @SerializedName("duration")
    @Expose
    private Duration duration;

    @SerializedName("steps")
    @Expose
    private ArrayList<Steps> steps;

    public Duration getDuration() {return duration;}

    public ArrayList<Steps> getSteps() {
        return steps;
    }
}

class Steps { //Steps01
    @SerializedName("duration") //Steps로 몇분?
    @Expose
    private Duration duration;

    @SerializedName("distance")
    @Expose
    private Distance distance;

    @SerializedName("transit_details")
    @Expose
    private TransitDetails transitDetails;

    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;

    @SerializedName("steps") //WalkingSteps1
    @Expose
    private ArrayList<Steps> steps;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode;

    public Duration getDuration() {return duration;}

    public Distance getDistance() {return distance;}

    public TransitDetails getTransitDetails() {return transitDetails;}

    public String getHtmlInstructions() {return htmlInstructions;}

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public String getTravelmode() {return travelmode;}
}

class Duration {
    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }
}

class Distance extends Duration{
}

class TransitDetails {
    @SerializedName("departure_stop")
    @Expose
    private DepartureStop departureStop;

    @SerializedName("arrival_stop")
    @Expose
    private ArrivalStop arrivalStop;

    @SerializedName("line")
    @Expose
    private Line line;

    public DepartureStop getDepartureStop() {
        return departureStop;
    }

    public ArrivalStop getArrivalStop() {
        return arrivalStop;
    }

    public Line getLine() {return line;}
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

class Line {
    @SerializedName("short_name")
    @Expose
    private String short_name;

    public String getShort_name() {return short_name;}
}


class innerSteps {
    @SerializedName("distance")
    @Expose
    private innerDistance distance;

    @SerializedName("duration")
    @Expose
    private innerDuration duration;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode;

    public innerDistance getDistance() {
        return distance;
    }

    public innerDuration getDuration() {
        return duration;
    }

    public String getTravelmode() {return travelmode;}
}

class innerDistance extends Duration {
}

class innerDuration extends innerDistance {
}
