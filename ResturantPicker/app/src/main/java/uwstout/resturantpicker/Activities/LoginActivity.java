package uwstout.resturantpicker.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;


import uwstout.resturantpicker.Fragments.AccountCreateFragment;
import uwstout.resturantpicker.Fragments.BlankFragment;
import uwstout.resturantpicker.Fragments.LoginFragment;

import uwstout.resturantpicker.Objects.CredentialsManager;
import uwstout.resturantpicker.Objects.DataManager;
import uwstout.resturantpicker.Objects.Food;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.Objects.RestaurantDatabase;
import uwstout.resturantpicker.R;

public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginFragmentInterface,
        AccountCreateFragment.AccountCreateFragmentInterface{

    private static Toast toast = null;
    private static DataManager database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = DataManager.getInstance();
        loadDBWithTestRestaurants2();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            //create an instance of our first fragment and commit it to the frag manager
            LoginFragment firstFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    private void loadDBWithTestRestaurants(){
        RestaurantDatabase.Genres[] vals = RestaurantDatabase.Genres.values();
        for(int i = 0; i < vals.length; i++){
            Log.e("Genre value: ", Integer.toString(vals[i].getValue()));
        }
        //TODO: implement menus with food objects to each of these restaurants, and attempt to log transactions between them and users
        //NOTE: I know these restuarant objects are not representative. I wanted to only match places ID at the time


        //public Food(int sweetLevel, int sourLevel, int bitterLevel, int saltyLevel, int umamiLevel, String description, RestaurantDatabase.Genres genre, double value){
        Restaurant logjam = new Restaurant("Log Jam", "address", "ChIJZ8EG-aF7-IcRk6UJVSnQMOY", 1, "pictureID");
        logjam.setGenre(RestaurantDatabase.Genres.FASTFOOD);
        logjam.addMenuItem(new Food(50, 20, 0, 128, 240, "Burger", RestaurantDatabase.Genres.FASTFOOD, 7.99));
        logjam.addMenuItem(new Food(150, 20, 0, 20, 100, "Wrap", RestaurantDatabase.Genres.FASTFOOD, 6.99));
        logjam.addMenuItem(new Food(80, 0, 0, 50, 200, "Wings", RestaurantDatabase.Genres.FASTFOOD, 4.99));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "logjamUN", "password", logjam);

        Restaurant jeffs = new Restaurant("Jeff's Pizza", "address", "ChIJEerOHaJ7-IcRhvqsXhVLrrM", 1, "pictureID");
        jeffs.setGenre(RestaurantDatabase.Genres.PIZZA);
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Small Sausage", RestaurantDatabase.Genres.PIZZA, 8.99));
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Medium Sausage", RestaurantDatabase.Genres.PIZZA, 10.99));
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Large Sausage", RestaurantDatabase.Genres.PIZZA, 12.99));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "jeffsUN", "password", jeffs);

        Restaurant rawdeal = new Restaurant("Raw Deal", "address", "ChIJpzKPC6J7-IcRjaZaIa1HGGE", 1, "pictureID");
        rawdeal.setGenre(RestaurantDatabase.Genres.FASTFOOD);
        rawdeal.addMenuItem(new Food(0, 0, 0, 150, 40, "Coffee", RestaurantDatabase.Genres.FASTFOOD, 2.09));
        rawdeal.addMenuItem(new Food(180, 0, 0, 0, 80, "Muffins", RestaurantDatabase.Genres.FASTFOOD, 2.49));
        rawdeal.addMenuItem(new Food(0, 0, 50, 0, 50, "Beer", RestaurantDatabase.Genres.FASTFOOD, 3.49));

        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "rawDealUN", "password", rawdeal);

        //garbage entries to test performance problems with most filters being at least n^2 complexity
        Restaurant g1 = new Restaurant("g1", "address", "g1", 1, "pictureID");
        g1.setGenre(RestaurantDatabase.Genres.CHINESE);
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "g1UN", "password", g1);

        Restaurant g2 = new Restaurant("g2", "address", "g2", 1, "pictureID");
        g2.setGenre(RestaurantDatabase.Genres.CHINESE);
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "g1UN", "password", g2);

        Restaurant g3 = new Restaurant("Log Jam", "address", "g3", 1, "pictureID");
        g3.setGenre(RestaurantDatabase.Genres.CHINESE);
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "g1UN", "password", g3);

        Restaurant g4 = new Restaurant("Log Jam", "address", "g4", 1, "pictureID");
        g4.setGenre(RestaurantDatabase.Genres.CHINESE);
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "g1UN", "password", g4);
    }

    private void loadDBWithTestRestaurants2(){
        //public Food(int sweetLevel, int sourLevel, int bitterLevel, int saltyLevel, int umamiLevel, String description, RestaurantDatabase.Genres genre, double value){
        Restaurant jeffs = new Restaurant("Jeff's Pizza", "address", "ChIJEerOHaJ7-IcRhvqsXhVLrrM", 1, "pictureID");
        jeffs.setGenre(RestaurantDatabase.Genres.PIZZA);
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Small Sausage", RestaurantDatabase.Genres.PIZZA, 8.99));
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Medium Sausage", RestaurantDatabase.Genres.PIZZA, 10.99));
        jeffs.addMenuItem(new Food(20, 0, 0, 150, 200, "Large Sausage", RestaurantDatabase.Genres.PIZZA, 12.99));
        jeffs.addMenuItem(new Food(250, 0, 0, 0, 150, "Cinnamon Bites", RestaurantDatabase.Genres.PIZZA, 6.49));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "jeffsUN", "password", jeffs);

        Restaurant teds = new Restaurant("Ted's Pizza", "address", "ChIJB83FKpl7-IcR2-rdR8sXpoA", 1, "pictureID");
        teds.setGenre(RestaurantDatabase.Genres.PIZZA);
        teds.addMenuItem(new Food(20, 0, 0, 150, 200, "Small Sausage", RestaurantDatabase.Genres.PIZZA, 8.99));
        teds.addMenuItem(new Food(20, 0, 0, 150, 200, "Medium Sausage", RestaurantDatabase.Genres.PIZZA, 10.99));
        teds.addMenuItem(new Food(20, 0, 0, 150, 200, "Large Sausage", RestaurantDatabase.Genres.PIZZA, 12.99));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "tedsUN", "password", teds);

        Restaurant culvers = new Restaurant("Culver's", "address", "ChIJuXfu9IZ7-IcRCwPsEykvwh0", 1, "pictureID");
        culvers.setGenre(RestaurantDatabase.Genres.FASTFOOD);
        culvers.addMenuItem(new Food(10, 0, 0, 150, 256, "Butter Burger", RestaurantDatabase.Genres.FASTFOOD, 7.99));
        culvers.addMenuItem(new Food(10, 0, 0, 220, 100, "Fries", RestaurantDatabase.Genres.FASTFOOD, 2.49));
        culvers.addMenuItem(new Food(10, 0, 0, 80, 180, "Chicken Sandwich", RestaurantDatabase.Genres.FASTFOOD, 7.49));
        culvers.addMenuItem(new Food(250, 0, 0, 0, 120, "Custard", RestaurantDatabase.Genres.FASTFOOD, 3.29));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "culversUN", "password", culvers);

        Restaurant logjam = new Restaurant("Log Jam", "address", "ChIJZ8EG-aF7-IcRk6UJVSnQMOY", 1, "pictureID");
        logjam.setGenre(RestaurantDatabase.Genres.FASTFOOD);
        logjam.addMenuItem(new Food(50, 20, 0, 128, 240, "Burger", RestaurantDatabase.Genres.FASTFOOD, 7.99));
        logjam.addMenuItem(new Food(150, 20, 0, 20, 100, "Wrap", RestaurantDatabase.Genres.FASTFOOD, 6.99));
        logjam.addMenuItem(new Food(80, 0, 0, 50, 200, "Wings", RestaurantDatabase.Genres.FASTFOOD, 4.99));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "logjamUN", "password", logjam);

        Restaurant cancun = new Restaurant("Fortune Cookie", "address", "ChIJR_CLM3h8-IcR1_9XD8oA2ac", 1, "pictureID");
        cancun.setGenre(RestaurantDatabase.Genres.MEXICAN);
        cancun.addMenuItem(new Food(10, 30, 0, 150, 240, "Tacos", RestaurantDatabase.Genres.MEXICAN, 6.79));
        cancun.addMenuItem(new Food(0, 0, 0, 180, 200, "Cheese Quesadilla", RestaurantDatabase.Genres.MEXICAN, 4.79));
        cancun.addMenuItem(new Food(10, 0, 0, 175, 50, "Tortilla Chips", RestaurantDatabase.Genres.MEXICAN, 3.49));
        this.createAccount(CredentialsManager.AccountType.RESTAURANT, "cancunUN", "password", cancun);
    }

    //implemented through the LoginFragmentInterface
    public void swap(String fragment, String args){
        Fragment f;

        switch(fragment){
            case "login": f = new LoginFragment(); break;
            case "accountCreate": f = new AccountCreateFragment(); break;
            default: f = new BlankFragment(); break;
        }

        /*
        //debug
        if(f instanceof LoginFragment) {
            sendToast("Switching to LoginFragment");
        }else{
            sendToast("Switching to AccountCreateFragment");
        }
        */

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, f);
        transaction.addToBackStack(fragment);

        // Commit the transaction
        transaction.commit();
    }

    //callback used to go to the next activity when user authentication is successful
    public void transitionToNextIntent(){
        //TODO: make this go to the initial picks screen instead
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    //implemented through each fragment's interface, for using the same instance of Toast
    public void sendToast(String message){
        if (toast != null) {
            toast.cancel();
        }
        Context context = getApplicationContext();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    //implemented via AccountCreateFragmentInterface
    public boolean usernameExists(String username){
        //String pos = "" + userbase.usernameExists(username);
        if((database != null)&&(database.getCredentialsManager().usernameExists(username))){return true;}
        else return false;
    }

    //implemented via AccountCreateFragmentInterface
    public boolean createAccount(CredentialsManager.AccountType accountType, String username, String password, Object args){
        if(database != null) return database.createAccount(accountType, username, password, args);
        else return false;
    }

    //implemented via LoginFragmentInterface
    public boolean login(String username, String password){
        if(database != null) return database.login(username, password);
        else return false;
    }
}
