package uwstout.resturantpicker.Objects;

import java.util.Arrays;
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


    //prints all Restaurants in the DB to the error log
    public void dumpDB(){
        if(this.restaurants != null) {
            for (int i = 0; i < Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.restaurants.get(i) != null) {
                    for (int j = 0; j < this.restaurants.get(i).size(); j++) {
                        Log.e("(" + i + ", " + + j +")", this.restaurants.get(i).get(j).toString());
                    }
                }
            }
        }
    }

    //merges two restaurants with the same GID, retrusn merged Restaurant (current instance). will prefer attributes in the new one if they overlap.
    public Restaurant merge(Restaurant restaurant){
        String gid = restaurant.getGooglePlacesID();
        if(gid != null) {
            Restaurant result = fetchCopyOfRestaurantByID(gid);
            if(result != null){
                result.merge(restaurant);
                return result;
            }
        }
        return null;
    }

    public Restaurant[] restaurantSortByPreference(Restaurant[] toBeSorted){
        Log.e("TOBESORTED: ", Arrays.toString(toBeSorted));
        Vector<Vector<Restaurant>> tempSort = new Vector<Vector<Restaurant>>();

        //create 1st dimension, representing genre
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()) {
            tempSort.add(new Vector<Restaurant>());
        }

        //add the passed in restaurants into the temporary data structure
        for(Restaurant rest : toBeSorted){
            tempSort.get(rest.getGenre().getValue()).add(rest);
        }

        //-------------------
        //sort by food spectrum in cache
        //-------------------
        //for each genre...
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()){
            //if entries exist for a given genre...
            if(tempSort.get(genre.getValue()).size() > 0){

                //get avg flavor spectrum for this genre from the user's cache
                int[] preferenceCacheFlavorSpectrumByGenre = ((Food)DataManager.getInstance().getPreferenceCache().getAvgFoodSpectrumValueOfGivenGenreTable(genre)).getFlavorSpectrum();

                int numberOfRestaurantsToSortInThisGenre = tempSort.get(genre.getValue()).size();
                int[] deltaFromAveragePerRestaurant = new int[numberOfRestaurantsToSortInThisGenre];

                for(int i = 0; i < deltaFromAveragePerRestaurant.length; i++){
                    int totalDelta = 0;

                    //get avg flavor spectrum for the given restaurant we want to sort
                    int[] restaurantAvgFoodSpectrum = tempSort.get(genre.getValue()).get(i).getMenuCharacteristics().getFlavorSpectrum();
                    for(int j = 0; j < RestaurantDatabase.Genres.NUMBEROFGENRES.getValue() - 1; j++){
                        //calculate the diff between user preference and restaurant categorization per flavor spectrum value
                        //and add it to the deltas of all spectrum values
                        totalDelta += Math.abs(preferenceCacheFlavorSpectrumByGenre[j] - restaurantAvgFoodSpectrum[j]);
                    }
                    //
                    deltaFromAveragePerRestaurant[i] = totalDelta;
                }

                //sort them from lowest delta to highest
                int[] finalOrderForGenre = new int[numberOfRestaurantsToSortInThisGenre];
                Arrays.fill(finalOrderForGenre, -1);
                int prevMin = Integer.MIN_VALUE;
                int currMin = Integer.MAX_VALUE;
                int currIndex = -1;

                //generate a list of sorted indexes
                for(int h = 0; h < finalOrderForGenre.length; h++){
                    for(int g = 0; g < deltaFromAveragePerRestaurant.length; g++){
                        if((deltaFromAveragePerRestaurant[g] <= currMin) && (deltaFromAveragePerRestaurant[g] >= prevMin)){
                            boolean duplicateFound = false;

                            for(int m = 0; m < finalOrderForGenre.length; m++) {
                                if(finalOrderForGenre[h] == g) duplicateFound = true;
                            }

                            if(!duplicateFound) finalOrderForGenre[h] = g;
                        }
                    }
                }

                //use the sorted indexes to sort the original array of Restaurants
                Vector<Restaurant> newGenreOrder = new Vector<Restaurant>();
                for(int d = 0; d < finalOrderForGenre.length; d++){
                    //copy the Restaurants in a local vector to reorder them
                    newGenreOrder.add(tempSort.get(genre.getValue()).get(finalOrderForGenre[d]));
                }
                //remove original order
                tempSort.get(genre.getValue()).removeAllElements();

                //and replace it with the new order
                for(int y = 0; y < newGenreOrder.size(); y++){
                    tempSort.get(genre.getValue()).add(newGenreOrder.get(y));
                }
            }
        }

        int[] userPurchaseCountByGenre = DataManager.getInstance().getCredentialsManager().getTotalNumberOfTransactionsForAllGenres(DataManager.getInstance().getCurrentUser());
        Restaurant[] finalSorted = new Restaurant[toBeSorted.length];
        int finalSortedCurIndex = 0;

        //-------------------
        //sort by #transactions in all history for user
        //-------------------
        //for each genre
        for(int u = 0; u < userPurchaseCountByGenre.length; u++){
            int max = -1;
            int curIndex = -1;

            //find the index(genre) of highest occuring purchases
            for(int z = 0; z < userPurchaseCountByGenre.length; z++) {
                if (userPurchaseCountByGenre[z] > max) {
                    max = userPurchaseCountByGenre[z];
                    curIndex = z;
                }
            }

            //apply the restaurants from that genre to the results array (these are already sorted by food spectrum)
            for(Restaurant rest : tempSort.get(curIndex)) {
                finalSorted[finalSortedCurIndex++] = rest;
            }

            //remove genre from sort since it was already targeted
            userPurchaseCountByGenre[curIndex] = -1;
        }
        Log.e("SORT RESULT: ", Arrays.toString(finalSorted));
        return finalSorted;
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
