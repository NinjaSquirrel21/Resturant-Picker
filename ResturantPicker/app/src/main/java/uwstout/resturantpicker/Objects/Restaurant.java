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
    private int congestionLevel = -1;
    private String address;
    private long rating = -1;
    private String pictureID;
    private String url;

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

    public Restaurant(String name, String address, String googlePlacesID, long rating, String pictureID, String url){
        this.menu = new Vector<Food>();
        this.name = name;
        this.address = address;
        this.googlePlacesID = googlePlacesID;
        this.rating = rating;
        this.pictureID = pictureID;
        this.url = url;
    }

    //calculates the average values for all food items in the food spectrum
    private void calculateMenuCharacteristics(){

        int[] avgMenuFlavorSpectrum = new int[Food.NUMBER_OF_SPECTRUM_VALUES];

        for(int i = 0; i < this.menu.size(); i++){
            int[] tempFlavorSpectrum = this.menu.get(i).getFlavorSpectrum();
            for(int j = 0; j < Food.NUMBER_OF_SPECTRUM_VALUES; j++){
                avgMenuFlavorSpectrum[j] += tempFlavorSpectrum[j];
            }
        }

        for(int k = 0; k < Food.NUMBER_OF_SPECTRUM_VALUES; k++) {
            avgMenuFlavorSpectrum[k] /= this.menu.size();
        }

        //calculate the menuCharacteristics every time the menu is updated
        this.menuCharacteristics = new Food(avgMenuFlavorSpectrum, this.name + "Characteristics", this.genre, -1.0);
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
        if(this.menu == null)this.menu = new Vector<Food>();
        if(getFoodFromMenuByName(newMenuItem.getDescription()) == null){
            this.menu.add(newMenuItem);
            this.calculateMenuCharacteristics(); //update menu avg spectrum
        }
    }

    //adds a new menu items to the restaurant, only if the item does not exist in the menu already
    public boolean addMenuItems(Vector<Food> newMenuItems){
        if(this.menu == null)this.menu = new Vector<Food>();
        if(newMenuItems != null) {
            for (int i = 0; i < newMenuItems.size(); i++) {
                if (getFoodFromMenuByName(newMenuItems.get(i).getDescription()) == null) {
                    this.menu.add(newMenuItems.get(i));
                }else{
                    //failure if any menu item fails to be added
                    Log.e("Restaurant Error: ", "Failed to add Food item with addMenuItems(), attempting to add duplicate entry\n" + newMenuItems.get(i).toString());
                    return false;
                }
            }
            this.calculateMenuCharacteristics(); //update menu avg spectrum
            //success if all menu items are added successfully
            return true;
        }
        //failure if given garbage
        return false;
    }

    //merges two restaurants with the same GID, putting the result into this instance. will prefer attributes in the new one if they overlap.
    public Restaurant merge(Restaurant restaurant){
        //NOTE:: Not all attributes of Restaurant are mutated in this function. Check carefully to see if your variable is being changed or not with this call

        //never mutate gid. this should never be called unless theres a gid match to begin with
        if(!restaurant.getGooglePlacesID().equals(this.googlePlacesID)){return null;}

        //if the passed in restaurant has initialized values, write this instance's variables to them
        if(restaurant.getName() != null) this.name = restaurant.getName();
        if(restaurant.getDistance() != null) this.distance = restaurant.getDistance();
        if(restaurant.getGenre() != null) this.genre = restaurant.getGenre();
        if(restaurant.getCost() != 0) this.cost = restaurant.getCost();
        if(restaurant.getServiceType() != null) this.serviceType = restaurant.getServiceType();
        if(restaurant.getCongestionLevel() != -1) this.congestionLevel = restaurant.getCongestionLevel();
        if(restaurant.getAddress() != null) this.address = restaurant.getAddress();
        if(restaurant.getRating() != -1) this.rating = restaurant.getRating();
        if(restaurant.getPictureID() != null) this.pictureID = restaurant.getPictureID();
        if(restaurant.getURL() != null){this.url = restaurant.getURL();}
        if(restaurant.getMenu() != null){
            if(restaurant.getMenu().size() > 0) {
                //Log.v("Menu overridden with:", restaurant.getMenu().toString());
                this.menu = restaurant.getMenu();
            }
        }
        if(restaurant.getURL() != null){this.url = restaurant.getURL();}
        return this;
    }

    public String getName(){return this.name;}
    public String getAddress(){return this.address;}
    public long getRating(){return this.rating;}
    public String getDistance(){return this.distance;}
    public double getCost(){return this.cost;}
    public String getPictureID(){return this.pictureID;}
    public Vector<Food> getMenu(){return this.menu;}
    public CredentialsManager.ServiceType getServiceType(){return this.serviceType;}
    public RestaurantDatabase.Genres getGenre(){return this.genre;}
    public void setGenre(RestaurantDatabase.Genres genre){this.genre = genre;}
    public int getCongestionLevel(){return this.congestionLevel;}
    public void updateCongestionLevel(int congestionLevel){this.congestionLevel = congestionLevel;}
    public String getGooglePlacesID(){return this.googlePlacesID;}
    public Food getMenuCharacteristics(){return this.menuCharacteristics;}
    public String getURL(){return this.url;}

    public String toString(){
        String result = "";
        result += googlePlacesID + "\n";
        result += name + "\n";
        result += genre + "\n";
        result += menu.toString() + "\n";
        //private Food menuCharacteristics + "\n";
        result += cost + "\n";
        result += serviceType + "\n";
        result += congestionLevel + "\n";
        result += address + "\n";
        result += rating + "\n";
        result += pictureID + "\n";
        return result;
    }

}
