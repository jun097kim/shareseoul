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

class Steps { //Steps01
    @SerializedName("duration") //Steps로 몇분?
    @Expose
    private Duration1 duration1;

    @SerializedName("duration") //두번째 {}
    @Expose
    private Duration2_tra duration2_tra;

    @SerializedName("duration")
    @Expose
    private Duration3 duration3;

    @SerializedName("distance")
    @Expose
    private Distance1 distance1;

    @SerializedName("distance")
    @Expose
    private Distance2 distance2;

    @SerializedName("distance")
    @Expose
    private Distance3 distance3;

    @SerializedName("transit_details")
    @Expose
    private TransitDetails transitDetails;

    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions1_wal;

    @SerializedName("html_instructions") //두번째 {}
    @Expose
    private String htmlinstructions2_tra;

    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions3;

    @SerializedName("steps") //WalkingSteps1
    @Expose
    private ArrayList<WalkingSteps> steps;

    @SerializedName("steps") //WalkingSteps2
    @Expose
    private ArrayList<WalkingSteps2> steps2;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode1_wal;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode2_tra;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode3_wal;

    public Duration1 getDuration1() {return duration1;}

    public Duration2_tra getDuration2_tra() {return duration2_tra;} //두번째 {}

    public Duration3 getDuration3() {return duration3;}

    public Distance1 getDistance1() {return distance1;}

    public Distance2 getDistance2() {return distance2;}

    public Distance3 getDistance3() {return distance3;}

    public TransitDetails getTransitDetails() {return transitDetails;}

    public String getHtmlInstructions() {return htmlInstructions1_wal;}

    public String getTransit_htmlinstructions() {return htmlinstructions2_tra;} //두번째 {}

    public String getHtmlInstructions3() {return htmlInstructions3;}

    public ArrayList<WalkingSteps> getSteps() {
        return steps;
    }

    public ArrayList<WalkingSteps2> getSteps2s() {return steps2;}

    public String getTravelmode() {return travelmode1_wal;}

    public String getTravelmode2() {return travelmode2_tra;}

    public String getTravelmode3_wal() {return travelmode3_wal;}
}

class Duration1 {
    @SerializedName("text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }
}

class Duration2_tra extends Duration1 { //두번째 {} 아니면 Duration1 상속 받던가..
}

class Duration3 extends Duration1 {
}

class Distance1 extends Duration1{
}

class Distance2 extends Duration1{
}

class Distance3 extends Duration1{
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
    @SerializedName("distance1")
    @Expose
    private WalkingDistance distance1;

    @SerializedName("duration1")
    @Expose
    private WalkingDuration duration1;

    @SerializedName("travel_mode")
    @Expose
    private String travelmode1;

    public WalkingDistance getDistance1() {
        return distance1;
    }

    public WalkingDuration getDuration1() {
        return duration1;
    }

    public String getTravelmode1() {return travelmode1;}
}

class WalkingSteps2 {
    @SerializedName("distance")
    @Expose
    private WalkingDistance2 distance2;

    @SerializedName("duration")
    @Expose
    private WalkingDuration2 duration2;

    @SerializedName("travel_mode")
    @Expose
    private String travel_mode2;

    public WalkingDistance2 getDistance2() {return distance2;}

    public WalkingDuration2 getDuration2() {return duration2;}

    public String getTravel_mode2() {return travel_mode2;}
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

class WalkingDuration2 extends WalkingDistance {
}

class WalkingDistance2 extends WalkingDistance {
}
