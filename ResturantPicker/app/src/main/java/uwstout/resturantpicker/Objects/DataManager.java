package uwstout.resturantpicker.Objects;

import android.provider.ContactsContract;

import java.util.Date;
import java.util.Vector;

/**
 * Created by Barrett on 4/13/2016.
 */
public class DataManager{
    /*
    private static DataManager ourInstance = new DataManager();
    public static DataManager getInstance() {
        return ourInstance;
    }
    */

    private CredentialsManager credentialsManager;
    private PreferenceCache preferenceCache;
    private RestaurantDatabase restaurantDatabase;
    private String currentUser;

    public DataManager(){
        this.credentialsManager = new CredentialsManager();
        this.restaurantDatabase = new RestaurantDatabase();
    }

    public boolean login(String username, String password){
        if(this.credentialsManager.login(username, password)){
            this.preferenceCache = new PreferenceCache(username, credentialsManager);
            return true;
        }
        return false;
    }

    public boolean completeTransaction(Transaction transaction){
        this.credentialsManager.commitTransaction(transaction);
        this.preferenceCache.updateGenreTable(transaction.getMaxGenre());
        return true;
    }

    public CredentialsManager getCredentialsManager(){
        return this.credentialsManager;
    }

    public PreferenceCache getPreferenceCache(){
        return this.preferenceCache;
    }

    public RestaurantDatabase getRestaurantDatabase(){
        return this.restaurantDatabase;
    }
}
