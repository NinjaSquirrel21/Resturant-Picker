package uwstout.resturantpicker.Objects;

/**
 * Created by caval on 4/4/2016.
 */
public class Restaurant {

    public String name;
    public String distance;
    public int cost;

    public Restaurant(){
        name = "Test1";
        distance = "0";
        cost = 0;
    }

    public Restaurant(String name, String distance, int cost) {
        this.name = name;
        this.distance = distance;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getdistance() {
        return distance;
    }

    public int getcost() {
        return cost;
    }
}
