package uwstout.resturantpicker.Objects;

import android.util.Log;

import java.util.Arrays;
import java.util.Vector;

/*
//Singleton class used to represent the model for the preference cache generated throughout the user experience
public class PreferenceCache {
    private static PreferenceCache ourInstance = new PreferenceCache("");

    public static PreferenceCache getInstance() {
        return ourInstance;
    }
    private String username;
    private static int TABLE_DEPTH = 5;
    private RestaurantDatabase restaurants;

    private PreferenceCache(String username){
        ourInstance.username = username;
        ourInstance.restaurants = new RestaurantDatabase(TABLE_DEPTH);
        ourInstance.populateTestData();
    }

    private void updateTableDepth(int tableDepth){
        if(ourInstance.TABLE_DEPTH > tableDepth){
            ourInstance.restaurants.updateTableDepth(tableDepth);
        }
        ourInstance.TABLE_DEPTH = tableDepth;
    }

    //String name, String distance, String googlePlacesID, RestaurantDatabase.Genres genre, int cost, CredentialsManager.ServiceType serviceType
    private void populateTestData(){
        Vector<Food> menu = new Vector<Food>();

        //TODO: update test cases
        //int sweetLevel, int sourLevel, int bitterLevel, int saltyLevel, int umamiLevel, String description, RestaurantDatabase.Genres genre
        menu.add(new Food(100, 200, 100, 200, 100, "taco", RestaurantDatabase.Genres.MEXICAN));

        ourInstance.restaurants.addRestaurant(new Restaurant("Culvers", "close", "abc123", RestaurantDatabase.Genres.FASTFOOD, menu, 0.5, CredentialsManager.ServiceType.PICKUP));
        ourInstance.restaurants.addRestaurant(new Restaurant("McDonalds", "far", "abc124", RestaurantDatabase.Genres.FASTFOOD, menu, 0.3, CredentialsManager.ServiceType.PICKUP));

        ourInstance.restaurants.addRestaurant(new Restaurant("Jeff's Pizza", "close", "abc125", RestaurantDatabase.Genres.PIZZA, menu, 0.6, CredentialsManager.ServiceType.DELIVERY));
        ourInstance.restaurants.addRestaurant(new Restaurant("Domino's", "far", "abc126", RestaurantDatabase.Genres.PIZZA, menu, 0.5, CredentialsManager.ServiceType.DELIVERY));
    }

    //flushes current cache and fills it with TABLE_DEPTH most recent entries in the live DB for each genre
    private boolean populateCache(RestaurantDatabase liveDatabase){
        //TODO: fetch TABLE_DEPTH most recent Transactions from CustomerUserData
        restaurants = new RestaurantDatabase(liveDatabase.fetchMostRecentFromEachGenre(TABLE_DEPTH), TABLE_DEPTH);
        if(restaurants == null){
            return true;
        }else return false;
    }
}
*/

public class PreferenceCache{
    private String username;
    private static int TABLE_DEPTH = 5;
    private CredentialsManager credentialsManager;
    private Vector<Vector<Transaction>> preferenceCache;

    public PreferenceCache(String username, CredentialsManager credentialsManager){
        this.username = username;
        this.credentialsManager = credentialsManager; //hopefully this is pass by reference...
        this.preferenceCache = this.credentialsManager.fetchMostRecentFromEachGenreByUser(TABLE_DEPTH, username, CredentialsManager.AccountType.CUSTOMER);
        //this.populateTestData();
    }

    //validates the current cache size based on a new table depth
    public void validateCacheSize(int desiredTableDepth){
        if(desiredTableDepth < 0){desiredTableDepth = this.TABLE_DEPTH;}
        if(this.TABLE_DEPTH > desiredTableDepth){
            //shrink the cache
            if(this.preferenceCache != null) {
                for (int i = 0; i < RestaurantDatabase.Genres.NUMBEROFGENRES.getValue(); i++) {
                    if (this.preferenceCache.get(i) != null) {
                        while (this.preferenceCache.get(i).size() < desiredTableDepth) {
                            //remove index at element 0 since it is the oldest
                            this.preferenceCache.get(i).remove(0);
                        }
                    }
                }
            }
        }else{
            //flushes current cache and fills it with desiredTableDepth most recent entries from the CredentialsManager's Transaction Database for each genre
            this.preferenceCache = this.credentialsManager.fetchMostRecentFromEachGenreByUser(desiredTableDepth, username, CredentialsManager.AccountType.CUSTOMER);
        }
        this.TABLE_DEPTH = desiredTableDepth;
    }

    void updateGenreTable(RestaurantDatabase.Genres genre){
        int genreValue = genre.getValue();
        if((this.preferenceCache != null) && (this.preferenceCache.get(genreValue) != null)) {
            if(this.preferenceCache.get(genreValue).size() == TABLE_DEPTH){
                //remove index at element 0 since it is the oldest, only if the table depth reaches its limit
                this.preferenceCache.get(genreValue).remove(0);
            }
            this.preferenceCache.get(genreValue).add(credentialsManager.fetchMostRecentFromSpecificGenreByUser(this.username, CredentialsManager.AccountType.CUSTOMER, genre));
        }
    }

    public void printCache(){
        RestaurantDatabase.Genres[] genres = RestaurantDatabase.Genres.values();

        for(int i = 0; i < genres.length-2; i++){
            Vector<Transaction> curGenre = this.preferenceCache.get(i);
            for(int j = 0; j < curGenre.size(); j++){
                Log.e("Cache dump: ", Integer.toString(i) + ", " + Integer.toString(j) + ": " + /*curGenre.get(j).getMaxGenre().getValue()*/ curGenre.get(j).toString());
            }
        }
    }

    public Food getAvgFoodSpectrumValueOfGivenGenreTable(RestaurantDatabase.Genres genre){
        int genreIndex = genre.getValue();

        if(this.preferenceCache != null){
            Vector<Transaction> genreTable = this.preferenceCache.get(genreIndex);

            if(genreTable != null){
                int[] genreTotalFoodSpectrum = new int[Food.NUMBER_OF_SPECTRUM_VALUES];
                Arrays.fill(genreTotalFoodSpectrum, 0);

                for(int i = 0; i < genreTable.size(); i++){
                    int[] transactionFoodSpectrum = genreTable.get(i).getAverageFoodSpectrumValues();
                    for(int j = 0; j < Food.NUMBER_OF_SPECTRUM_VALUES; j++){
                        genreTotalFoodSpectrum[j] += transactionFoodSpectrum[j];
                    }
                }

                for(int k = 0; k < Food.NUMBER_OF_SPECTRUM_VALUES; k++){
                    if(genreTable.size() > 0) genreTotalFoodSpectrum[k] /= genreTable.size();
                }

                //public Food(int[] flavorSpectrum, String description, RestaurantDatabase.Genres genre, double value){
                return new Food(genreTotalFoodSpectrum, "genreAvg", genre, -1);
            }
        }
        int[] garbageValuesSpectrum = new int[RestaurantDatabase.Genres.NUMBEROFGENRES.getValue()];
        return new Food(garbageValuesSpectrum, "genreAvgGarbage", genre, -1);
    }

    /*
    //String name, String distance, String googlePlacesID, RestaurantDatabase.Genres genre, int cost, CredentialsManager.ServiceType serviceType
    private void populateTestData(){
        Vector<Food> menu = new Vector<Food>();

        //TODO: update test cases
        //int sweetLevel, int sourLevel, int bitterLevel, int saltyLevel, int umamiLevel, String description, RestaurantDatabase.Genres genre
        menu.add(new Food(100, 200, 100, 200, 100, "taco", RestaurantDatabase.Genres.MEXICAN));

        this.restaurants.addRestaurant(new Restaurant("Culvers", "close", "abc123", RestaurantDatabase.Genres.FASTFOOD, menu, 0.5, CredentialsManager.ServiceType.PICKUP));
        this.restaurants.addRestaurant(new Restaurant("McDonalds", "far", "abc124", RestaurantDatabase.Genres.FASTFOOD, menu, 0.3, CredentialsManager.ServiceType.PICKUP));

        this.restaurants.addRestaurant(new Restaurant("Jeff's Pizza", "close", "abc125", RestaurantDatabase.Genres.PIZZA, menu, 0.6, CredentialsManager.ServiceType.DELIVERY));
        this.restaurants.addRestaurant(new Restaurant("Domino's", "far", "abc126", RestaurantDatabase.Genres.PIZZA, menu, 0.5, CredentialsManager.ServiceType.DELIVERY));
    }
    */
}
