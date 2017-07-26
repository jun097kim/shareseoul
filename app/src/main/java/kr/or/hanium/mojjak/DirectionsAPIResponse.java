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

    @SerializedName("warnings")
    @Expose
    private String warnings;

    public ArrayList<Legs> getLegs() {
        return legs;
    }

    public String getWarnings() {return warnings;}
}

class Legs {
    @SerializedName("duration")
    @Expose
    private Duration duration;

    @SerializedName("distance")
    @Expose
    private Distance distance;

    @SerializedName("start_address")
    @Expose
    private String start_address;

    @SerializedName("end_address")
    @Expose
    private String  end_address;

    @SerializedName("steps")
    @Expose
    private ArrayList<Steps> steps;

    public Duration getDuration() {return duration;}

    public Distance getDistance() {return distance;}

    public String getStart_address() {return start_address;}

    public String getEnd_address() {return end_address;}

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

    @SerializedName("headsign")
    @Expose
    private String headsign;

    @SerializedName("line")
    @Expose
    private Line line;

    public Duration getDuration() {return duration;}

    public Distance getDistance() {return distance;}

    public TransitDetails getTransitDetails() {return transitDetails;}

    public String getHtmlInstructions() {return htmlInstructions;}

    public ArrayList<Steps> getSteps() {
        return steps;
    }

    public String getTravelmode() {return travelmode;}

    public String getHeadsign() {return headsign;}

    public Line getLine() {return line;}
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
    @SerializedName("short_name") //몇호선
    @Expose
    private String short_name;

    @SerializedName("name") //예) 서울지하철, 인천 광역버스
    @Expose
    private String name;

    @SerializedName("vehicle")
    @Expose
    private Vehicle vehicle;

    public String getShort_name() {return short_name;}

    public String getName() {return name;}

    public Vehicle getVehicle() {return vehicle;}
}

class Vehicle {
    @SerializedName("name") //예) 지하철 or 버스
    @Expose
    private String name;

    @SerializedName("type") //(영어로) SUBWAY / BUS
    @Expose
    private String type;

    public String getName() {return name;}

    public String getType() {return type;}
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