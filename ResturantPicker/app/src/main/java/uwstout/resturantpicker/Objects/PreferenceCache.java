package uwstout.resturantpicker.Objects;

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
            //remove index at element 0 since it is the oldest
            this.preferenceCache.get(genreValue).remove(0);
            this.preferenceCache.get(genreValue).add(credentialsManager.fetchMostRecentFromSpecificGenreByUser(this.username, CredentialsManager.AccountType.CUSTOMER, genre));
        }
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
