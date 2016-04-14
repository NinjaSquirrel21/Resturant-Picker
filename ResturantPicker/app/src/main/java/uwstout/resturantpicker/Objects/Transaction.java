package uwstout.resturantpicker.Objects;

import java.util.Date;
import java.util.Vector;

public class Transaction{
    private String customer;
    private String vendor;
    private String vendorGoogleId;
    private Date transactionTime;
    private double finalPrice;
    private Vector<Food> itemsSold;

    public Transaction(String customer, String vendor, String vendorGoogleId, Date transactionTime, double finalPrice, Vector<Food> itemsSold){
        this.customer = customer;
        this.vendor = vendor;
        this.transactionTime = transactionTime;
        this.finalPrice = finalPrice;
        this.itemsSold = itemsSold;
    }

    //returns the average values for all food items of the Transaction in the food spectrum
    public int[] getAverageFoodSpectrumValues(){
        int[] avgFlavorSpectrum = new int[Food.NUMBER_OF_SPECTRUM_VALUES];

        for(int i = 0; i < this.itemsSold.size(); i++){
            int[] tempFlavorSpectrum = this.itemsSold.get(i).getFlavorSpectrum();
            for(int j = 0; j < Food.NUMBER_OF_SPECTRUM_VALUES; j++){
                avgFlavorSpectrum[j] += tempFlavorSpectrum[j];
            }
        }

        for(int k = 0; k < Food.NUMBER_OF_SPECTRUM_VALUES; k++) {
            avgFlavorSpectrum[k] /= Food.NUMBER_OF_SPECTRUM_VALUES;
        }

        return avgFlavorSpectrum;
    }

    //returns the most occuring genre in the sale
    public RestaurantDatabase.Genres getMaxGenre(){
        int[] genreCount = new int[RestaurantDatabase.Genres.NUMBEROFGENRES.getValue()];
        for(int i = 0; i < this.itemsSold.size(); i++){
            genreCount[this.itemsSold.get(i).getGenre().getValue()]++;
        }

        int maxValue = -1;
        int maxPosition = -1;
        for(int j = 0; j < genreCount.length; j++){
            if(genreCount[j] > maxValue){
                maxValue = genreCount[j];
                maxPosition = j;
            }
        }
        return RestaurantDatabase.Genres.values()[maxPosition];
    }

    public String getCustomer(){return this.customer;}
    public Date getTransactionTime(){return this.transactionTime;}
    public String getVendor(){return this.vendor;}
    public double getFinalPrice(){return this.finalPrice;}
}
