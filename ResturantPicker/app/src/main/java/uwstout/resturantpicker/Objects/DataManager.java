package uwstout.resturantpicker.Objects;

import android.provider.ContactsContract;

import java.util.Date;
import java.util.Vector;

/**
 * Created by Barrett on 4/13/2016.
 */
public class DataManager{

    private static DataManager ourInstance = null;
    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    private CredentialsManager credentialsManager;
    private PreferenceCache preferenceCache;
    private RestaurantDatabase restaurantDatabase;
    private String currentUser;

    private DataManager(){
        this.credentialsManager = new CredentialsManager();
        this.restaurantDatabase = new RestaurantDatabase();
    }

    public boolean login(String username, String password){
        if(this.credentialsManager.login(username, password)){
            if(this.credentialsManager.getAccountTypeByUsername(username) == CredentialsManager.AccountType.CUSTOMER) {
                this.preferenceCache = new PreferenceCache(username, credentialsManager);
            }
            return true;
        }
        return false;
    }

    public boolean createAccount(CredentialsManager.AccountType accountType, String username, String password, Object args){
        if(accountType == CredentialsManager.AccountType.RESTAURANT){
            if(this.credentialsManager.addNewUser(accountType, username, password, args)){
                //if restaurant account and adding new user successful
                this.restaurantDatabase.addRestaurant((Restaurant) args);
            }
            return false;
        }
        if((accountType == CredentialsManager.AccountType.CUSTOMER) && this.credentialsManager.addNewUser(accountType, username, password, args)) {
            //if customer account and adding new user successful
            return true;
        }else return false;
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
