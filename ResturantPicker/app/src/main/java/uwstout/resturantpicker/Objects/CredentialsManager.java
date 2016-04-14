package uwstout.resturantpicker.Objects;

import android.accounts.Account;

import java.util.Vector;

/**
 * Created by Barrett on 4/8/2016.
 * Used by LoginActivity and its' Fragments to keep track of userbase.
 * TODO: Implement using actual database for users.
 */
public class CredentialsManager {
    public enum AccountType{CUSTOMER, RESTAURANT}
    public enum ServiceType{DELIVERY, PICKUP}
    private Vector<UserData> users;
    private static int NUMBER_OF_USERS = 0;

    public CredentialsManager(){
        this.users = new Vector<UserData>();
    }

    //checks if user already exists in userbase before adding new user
    public boolean addNewUser(AccountType accountType, String username, String password,  Object args){
        if(!usernameExists(username)){
            if(accountType == AccountType.CUSTOMER)this.users.add(new CustomerUserData(username, password, (int)args));
            else if(accountType == AccountType.RESTAURANT)this.users.add(new RestaurantUserData(username, password, (Restaurant)args));
            this.NUMBER_OF_USERS++;
            return true;
        }
        else return false;
    }

    //public function written to get access of "tableDepth" most recent transactions for each genre
    public Vector<Vector<Transaction>> fetchMostRecentFromEachGenreByUser(int tableDepth, String username, AccountType accountType){
        if(accountType != AccountType.CUSTOMER){return null;}
        UserData user = fetchUserDataByName(username, accountType);

        if(user != null){
            return ((CustomerUserData)user).fetchMostRecentFromEachGenre(tableDepth);
        }else return null;
    }

    public Transaction fetchMostRecentFromSpecificGenreByUser(String username, AccountType accountType, RestaurantDatabase.Genres genre){
        if(accountType != AccountType.CUSTOMER){return null;}
        UserData user = fetchUserDataByName(username, accountType);

        if(user != null){
            return ((CustomerUserData)user).fetchMostRecentFromSpecificGenre(genre);
        }else return null;

    }

    //private function written to have access to user's data
    private UserData fetchUserDataByName(String username, AccountType accountType){
        if(this.users != null) {
            for(int i = 0; i < NUMBER_OF_USERS; i++){
                UserData tempUser = users.get(i);
                if(tempUser.usernameMatch(username)){return tempUser;}
            }
        }
        return null;
    }
    //Checks if username exists in userbase
    public boolean usernameExists(String username) {
        if (this.users != null) {
            for (int i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).usernameMatch(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getUsernamePosition(String username) {
        if (this.users != null) {
            for (int i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).usernameMatch(username)) {
                    return i;
                }
            }
        }
        return -1;
    }

    //attempt to authenticate user login, called from DataManager
    boolean login(String username, String password){
        boolean success = false;

        if(this.users != null){
            for(int i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).authenticate(username, password)){
                    success = true;
                }
            }
        }
        return success;
    }

    //used to complete transaction from the UI, logging it both in the vendor and the customer's UserData
    public boolean commitTransaction(Transaction transaction){
        int customerPos = getUsernamePosition(transaction.getCustomer());
        int vendorPos = getUsernamePosition(transaction.getVendor());
        if((customerPos > -1) && (vendorPos > -1)){
            ((CustomerUserData)users.get(customerPos)).recordPurchase(transaction);
            ((RestaurantUserData)users.get(vendorPos)).recordSale(transaction);
            return true;
        }else return false;
    }
}

//parent class used to hold user data
class UserData{
    private String username;
    private String password;
    private CredentialsManager.AccountType accountType;

    public UserData(String username, String password, CredentialsManager.AccountType accountType){
        this.username = username;
        this.password = password;
        this.accountType = accountType;
    }

    public boolean authenticate(String username, String password){
        if((!this.username.equals(username)) || (!this.password.equals(password))){return false;}
        else{return true;}
    }

    public boolean usernameMatch(String username){
        return (username.equals(this.username));
    }

}

//child class of UserData that holds sensitive user data for customer accounts
class CustomerUserData extends UserData{
    private Vector<Vector<Transaction>> purchaseHistory;
    private int age;

    public CustomerUserData(String username, String password, int age){
        super(username, password, CredentialsManager.AccountType.CUSTOMER);
        this.age = age;
        this.purchaseHistory = new Vector<Vector<Transaction>>();
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()) {
            this.purchaseHistory.add(new Vector<Transaction>());
        }
    }

    //optional constructor used if user does not want to define age
    public CustomerUserData(String username, String password){
        super(username, password, CredentialsManager.AccountType.CUSTOMER);
        this.age = -1;
        this.purchaseHistory = new Vector<Vector<Transaction>>();
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()) {
            this.purchaseHistory.add(new Vector<Transaction>());
        }
    }

    //adds the sale to the 2d array, sorted by genre
    public boolean recordPurchase(Transaction sale){
        int i = 0;
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()){
            if(sale.getMaxGenre().getValue() == genre.getValue()){
                this.purchaseHistory.get(i).add(sale);
                return true;
            }
            i++;
        }
        return false;
    }

    public int getUserAge(){
        return this.age;
    }

    public Transaction fetchMostRecentFromSpecificGenre(RestaurantDatabase.Genres genre){
        int genreIndex = genre.getValue();
        int genreSize = purchaseHistory.get(genreIndex).size();
        return this.purchaseHistory.get(genreIndex).get(genreSize - 1);
    }

    //returns a subset of the live DB that contains the "newTableDepth" most recent entries for each genre
    public Vector<Vector<Transaction>> fetchMostRecentFromEachGenre(int newTableDepth){

        //empty vector to start
        Vector<Vector<Transaction>> mostRecentTransactions = new Vector<Vector<Transaction>>();
        for(int o = 0; o < RestaurantDatabase.Genres.NUMBEROFGENRES.getValue(); o++){
            mostRecentTransactions.add(new Vector<Transaction>());
        }

        if(this.purchaseHistory != null) {
            //for each genre,
            for (int i = 0; i < RestaurantDatabase.Genres.NUMBEROFGENRES.getValue(); i++) {
                if(this.purchaseHistory.get(i) != null) {
                    int size = this.purchaseHistory.get(i).size();
                    int j = newTableDepth;

                    //if the new subset is smaller than the liveDB, get the most recent values
                    if(j < size) {
                        while (j > 0) {
                            mostRecentTransactions.get(i).add(this.purchaseHistory.get(i).get(size - j));
                            j--;
                        }
                    }else{
                        //else, copy the whole table for the genre
                        for(int k = 0; k < size; k++){
                            mostRecentTransactions.get(i).add(this.purchaseHistory.get(i).get(k));
                        }
                    }
                }
            }
        }
        return mostRecentTransactions;
    }
}

//child class of UserData that holds sensitive user data for restaurant accounts
class RestaurantUserData extends UserData{
    //sales history, sorted by genre
    private Vector<Vector<Transaction>> salesHistory;
    private Restaurant restaurant;

    public RestaurantUserData(String username, String password, Restaurant restaurant){
        super(username, password, CredentialsManager.AccountType.RESTAURANT);
        this.salesHistory = new Vector<Vector<Transaction>>();
        for(int i = 0; i < RestaurantDatabase.Genres.NUMBEROFGENRES.getValue(); i++){
            this.salesHistory.add(new Vector<Transaction>());
        }
        this.restaurant = restaurant;
    }

    //adds the sale to the 2d array, sorted by genre
    public boolean recordSale(Transaction sale){
        int i = 0;
        for(RestaurantDatabase.Genres genre : RestaurantDatabase.Genres.values()){
            if(sale.getMaxGenre().getValue() == genre.getValue()){
                this.salesHistory.get(i).add(sale);
                return true;
            }
            i++;
        }
        return false;
    }
}
