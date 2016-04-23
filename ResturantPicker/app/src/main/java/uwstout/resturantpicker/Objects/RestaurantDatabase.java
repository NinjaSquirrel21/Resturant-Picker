package uwstout.resturantpicker.Objects;

import java.util.Vector;
import android.util.Log;

/**
 * Created by Barrett on 4/12/2016.
 */
public class RestaurantDatabase{
    public enum Genres{
        PIZZA(0), ITALIAN(1), CHINESE(2), MEXICAN(3), FASTFOOD(4), SUBS(5), NUMBEROFGENRES(6);
        private final int value;
        Genres(int value){this.value = value;}
        public int getValue(){return this.value;}
    }

    //2d vector, with the first dimension being the enumerated type Genres (defined above)
    //and the second dimension being restaurants within those genres
    private Vector<Vector<Restaurant>> restaurants;
    private int tableDepth;

    public RestaurantDatabase(){
        this.tableDepth = Integer.MAX_VALUE;
        this.restaurants = new Vector<Vector<Restaurant>>();
        for(int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++){
            this.restaurants.add(new Vector<Restaurant>());
            //this.restaurants.get(i).setSize(tableDepth);
        }
    }

    public RestaurantDatabase(int tableDepth){
        this.tableDepth = tableDepth;
        this.restaurants = new Vector<Vector<Restaurant>>();
        for(int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++){
            this.restaurants.add(new Vector<Restaurant>());
        }
    }

    public RestaurantDatabase(Vector<Vector<Restaurant>> data, int tableDepth){
        this.tableDepth = tableDepth;
        this.restaurants = data;
    }

    //function used for the database used in the PreferenceCache
    public void updateTableDepth(int tableDepth){
        if(this.tableDepth > tableDepth){
            if(this.restaurants != null) {
                for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                    if(this.restaurants.get(i) != null){
                        while(this.restaurants.get(i).size() < tableDepth){
                            //remove index at element 0 since it is the oldest
                            this.restaurants.get(i).remove(0);
                        }
                    }
                }
            }
        }
    }

    //function that adds a restaurant to the 2d array, returning true or false indicating success
    //no duplicate googlePlacesIDs are allowed
    public boolean addRestaurant(Restaurant restaurant){
        int i = 0;

        Genres tempGenre = restaurant.getGenre();
        if(tempGenre == null) return false;

        for(Genres genre : Genres.values()){
            if(genre.getValue() == tempGenre.getValue()){

                Vector<Restaurant> genreTable = this.restaurants.get(i);
                for(int j = 0; j < genreTable.size(); j++){
                    //if ID exists in DB, return false
                    if(genreTable.get(j).getGooglePlacesID().equals(restaurant.getGooglePlacesID())){return false;}
                }
                if(genreTable.size() >= tableDepth){ //if the table is full, remove oldest entry
                    this.restaurants.get(i).remove(tableDepth);
                }
                this.restaurants.get(i).add(restaurant);
                Log.e("Restaurant added: ", restaurant.getGooglePlacesID());
                return true;
            }
            i++;
        }
        //returns false if the desired restaurant does not match the genre
        return false;
    }

    public Restaurant fetchCopyOfRestaurantByID(String googlePlacesID){
        if(this.restaurants != null) {
            for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.restaurants.get(i) != null) {
                    for (int j = 0; j < this.restaurants.get(i).size(); j++) {
                        Restaurant currRestaurant = this.restaurants.get(i).get(j);
                        Log.e("currRest : ", currRestaurant.getGooglePlacesID() +  " " + googlePlacesID);
                        if (currRestaurant.getGooglePlacesID().equals(googlePlacesID) /*&& currRestaurant.existsInDB()*/) {
                            return currRestaurant;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Restaurant fetchCopyOfRestaurantByName(String name){
        if(this.restaurants != null) {
            for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.restaurants.get(i) != null) {
                    for (int j = 0; j < this.restaurants.get(i).size(); j++) {
                        Restaurant currRestaurant = this.restaurants.get(i).get(j);
                        if (currRestaurant.getName().equals(name) && currRestaurant.existsInDB()) {
                            return currRestaurant;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void toStringIDs(){
        if(this.restaurants != null) {
            for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.restaurants.get(i) != null) {
                    for (int j = 0; j < this.restaurants.get(i).size(); j++) {
                        Log.e("Place ID: ", this.restaurants.get(i).get(j).getGooglePlacesID());
                    }
                }
            }
        }
    }

    /*
    //returns a subset of the live DB that contains the "newTableDepth" most recent entries for each genre
    public Vector<Vector<Restaurant>> fetchMostRecentFromEachGenre(int newTableDepth){

        //empty vector to start
        Vector<Vector<Restaurant>> mostRecentFromDB = new Vector<Vector<Restaurant>>();
        for(int o = 0; o < Genres.NUMBEROFGENRES.getValue(); o++){
            mostRecentFromDB.add(new Vector<Restaurant>());
        }

        if(this.restaurants != null) {
            //for each genre,
            for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.restaurants.get(i) != null) {
                    int size = this.restaurants.get(i).size();
                    int j = newTableDepth;

                    //if the new subset is smaller than the liveDB, get the most recent values
                    if(j < size) {
                        while (j > 0) {
                            mostRecentFromDB.get(i).add(this.restaurants.get(i).get(size - j));
                            j--;
                        }
                    }else{
                        //else, copy the whole table for the genre
                        for(int k = 0; k < size; k++){
                            mostRecentFromDB.get(i).add(this.restaurants.get(i).get(k));
                        }
                    }
                }
            }
        }
        return mostRecentFromDB;
    }
    */
}
