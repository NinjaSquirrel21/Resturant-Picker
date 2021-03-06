package uwstout.resturantpicker.Objects;

/**
 * Created by Barrett on 4/11/2016.
 */
public class Food {
    public static final int NUMBER_OF_SPECTRUM_VALUES = 5;
    private int sweetLevel;
    private int sourLevel;
    private int bitterLevel;
    private int saltyLevel;
    private int umamiLevel;
    private String description;
    private RestaurantDatabase.Genres genre;
    private double value;

    public Food(int sweetLevel, int sourLevel, int bitterLevel, int saltyLevel, int umamiLevel, String description, RestaurantDatabase.Genres genre, double value){
        this.sweetLevel = sweetLevel;
        this.sourLevel = sourLevel;
        this.bitterLevel = bitterLevel;
        this.saltyLevel = saltyLevel;
        this.umamiLevel = umamiLevel;
        this.description = description;
        this.genre = genre;
        this.value = value;
    }

    public Food(int[] flavorSpectrum, String description, RestaurantDatabase.Genres genre, double value){
        if(flavorSpectrum.length != NUMBER_OF_SPECTRUM_VALUES){return;}
        this.setFlavorSpectrum(flavorSpectrum);
        this.description = description;
        this.genre = genre;
        this.value = value;
    }

    public boolean setFlavorSpectrum(int[] flavorSpectrum){
        if(flavorSpectrum.length != NUMBER_OF_SPECTRUM_VALUES){return false;}
        this.sweetLevel = flavorSpectrum[0];
        this.sourLevel = flavorSpectrum[1];
        this.bitterLevel = flavorSpectrum[2];
        this.saltyLevel = flavorSpectrum[3];
        this.umamiLevel = flavorSpectrum[4];
        return true;
    }
    public int[] getFlavorSpectrum(){return new int[]{sweetLevel, sourLevel, bitterLevel, saltyLevel, umamiLevel};}
    public String getDescription(){return this.description;}
    public RestaurantDatabase.Genres getGenre(){return this.genre;}
    public double getValue(){ return this.value;}

    public String toString(){
        String result = "";
        result += this.sweetLevel + "\n";
        result += this.sourLevel + "\n";
        result += this.bitterLevel + "\n";
        result += this.saltyLevel + "\n";
        result += this.umamiLevel + "\n";
        result += this.description + "\n";
        result += this.genre + "\n";
        result += this.value + "\n";
        return result;
    }
}
