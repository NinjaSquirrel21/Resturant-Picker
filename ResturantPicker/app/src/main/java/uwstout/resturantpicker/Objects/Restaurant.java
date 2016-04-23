package uwstout.resturantpicker.Objects;

import android.util.Log;

import java.util.Vector;

/**
 * //front-facing data structure designed to interface with the user experience.
 * //when information is needed from the restaurant's sensitive data related to
 * //it's user account, the use of a CredentialsManager is required.
 */
//TODO: perhaps merge with food RestaurantData class
public class Restaurant {
    private RestaurantUserData restaurantUserAccount;
    private String googlePlacesID;
    private String name;
    private String distance;
    private RestaurantDatabase.Genres genre;
    private Vector<Food> menu;
    private Food menuCharacteristics;
    private double cost;
    private CredentialsManager.ServiceType serviceType;
    private int congestionLevel;
    private String address;
    private long rating;
    private String pictureID;

    public Restaurant(){
        this.name = "Test1";
        this.distance = "0";
        this.cost = 0;
    }

    //simple constructor
    //TODO: FIX
    public Restaurant(String name, String distance, double cost) {
        this.menu = new Vector<Food>();
        this.name = name;
        this.distance = distance;
        this.cost = cost;
        this.menuCharacteristics = null;
        this.googlePlacesID = null;
    }

    public Restaurant(String name, String distance, String googlePlacesID, RestaurantDatabase.Genres genre, Vector<Food> menu, double cost, CredentialsManager.ServiceType serviceType){
        this.name = name;
        this.distance = distance;
        this.cost = cost;
        this.googlePlacesID = googlePlacesID;
        this.genre = genre;
        this.serviceType = serviceType;
        this.menu = menu;
    }

    public Restaurant(String name, String address, String googlePlacesID, long rating, String pictureID){
        this.menu = new Vector<Food>();
        this.name = name;
        this.address = address;
        this.googlePlacesID = googlePlacesID;
        this.rating = rating;
        this.pictureID = pictureID;
    }

    //returns the average values for all food items in the food spectrum
    private void calculateMenuCharacteristics(){

        int[] avgMenuFlavorSpectrum = new int[Food.NUMBER_OF_SPECTRUM_VALUES];

        for(int i = 0; i < this.menu.size(); i++){
            int[] tempFlavorSpectrum = this.menu.get(i).getFlavorSpectrum();
            for(int j = 0; j < Food.NUMBER_OF_SPECTRUM_VALUES; j++){
                avgMenuFlavorSpectrum[j] += tempFlavorSpectrum[j];
            }
        }

        for(int k = 0; k < Food.NUMBER_OF_SPECTRUM_VALUES; k++) {
            avgMenuFlavorSpectrum[k] /= Food.NUMBER_OF_SPECTRUM_VALUES;
        }

    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public long getRating() {
        return this.rating;
    }

    public String getdistance(){
        return this.distance;
    }

    public double getcost(){
        return this.cost;
    }

    public String getPictureID(){
        return this.pictureID;
    }

    public RestaurantDatabase.Genres getGenre(){
        return this.genre;
    }

    public void setGenre(RestaurantDatabase.Genres genre){this.genre = genre;}

    public void updateCongestionLevel(int congestionLevel){
        this.congestionLevel = congestionLevel;
    }

    public String getGooglePlacesID(){
        return this.googlePlacesID;
    }

    public boolean existsInDB(){
        return this.restaurantUserAccount != null;
    }

    //returns the Food object correlating to the passed in name. returns null if it fails
    public Food getFoodFromMenuByName(String foodName){
        if(this.menu != null){
            for(int i = 0; i < this.menu.size(); i++) {
                if(this.menu.get(i).getDescription().equals(foodName)){
                    return this.menu.get(i);
                }
            }
        }
        return null;
    }

    //adds a new menu item to the restaurant, checking if the item exists in the menu already
    public void addMenuItem(Food newMenuItem){
        if(getFoodFromMenuByName(newMenuItem.getDescription()) == null){this.menu.add(newMenuItem);}
    }

    //adds a new menu items to the restaurant, checking if the item exists in the menu already
    public void addMenuItems(Vector<Food> newMenuItems){
        for(int i = 0; i < newMenuItems.size(); i++){
            if(getFoodFromMenuByName(newMenuItems.get(i).getDescription()) == null){this.menu.add(newMenuItems.get(i));}
        }
    }

}
