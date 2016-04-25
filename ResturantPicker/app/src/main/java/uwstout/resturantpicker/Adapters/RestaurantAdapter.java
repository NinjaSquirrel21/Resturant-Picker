package uwstout.resturantpicker.Adapters;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;

import uwstout.resturantpicker.Objects.CredentialsManager;
import uwstout.resturantpicker.Objects.DataManager;
import uwstout.resturantpicker.Objects.Food;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.Objects.RestaurantDatabase;
import uwstout.resturantpicker.Objects.Transaction;
import uwstout.resturantpicker.R;

/**
 * Created by caval on 4/4/2016.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{

    public List<Restaurant> restaurants;

    private final String URLPART1 = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    private final String URLPART2 = "&key=AIzaSyAujea38TpoRvapEhrt36y1HEpXGckPqT8";
    private static URL url;
    private Drawable d;
    public Context cntx;
    public static FragmentManager fragman;




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView titleText;
        protected TextView contentText;
        protected ImageView foodPic;
        protected Button buyButton;
        protected Button menuButton;
        protected Restaurant restaurant;

        protected Vector<Food> currentRestuarntsFood;


        //added a click listener to act when "buy" is pressed, creating a new Transaction and running it through the data model. WIP
        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.restaurant_title);
            contentText = (TextView) itemView.findViewById(R.id.data);
            foodPic = (ImageView) itemView.findViewById(R.id.foodImage);
            buyButton = (Button) itemView.findViewById(R.id.buy_button);
            menuButton = (Button) itemView.findViewById(R.id.view_menu_button);




            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentRestuarntsFood = ViewHolder.this.restaurant.getMenu();
                    final MenuFragment menuFragment= new MenuFragment((String) titleText.getText(), currentRestuarntsFood, restaurant);
                    menuFragment.show(fragman, "Sample Fragment");
                }
            });
/*
            MOVED TO FRAGMENT

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //load up a Transaction object
                    String customer = DataManager.getInstance().getCurrentUser();
                    String vendorGoogleId = ViewHolder.this.restaurant.getGooglePlacesID();
                    String vendor = DataManager.getInstance().getCredentialsManager().getUsernameBasedOnGooglePlaceID(vendorGoogleId);
                    Date transactionTime = new Date();
                    Vector<Food> itemsSold = new Vector<Food>();
                    double finalPrice = 0;

                    //TODO: Make this more robust, perhaps iterate through a loop of possible options from the menu list.
                    //      for example, say that when the menu butt and prices, with check boxes next to them.
                    //      when the buy button is pressed, it wouldon is pressed, a Fragment pops up on top of the RestaurantCard and
                    //      contains a scrolling list of their menu simply iterate through the whole list of check boxes generated and
                    //      add all checked ones to a list of food items to add to the transaction

                    Food temp = ViewHolder.this.restaurant.getFoodFromMenuByName("test");
                    //ViewHolder.this.restaurant.setGenre(RestaurantDatabase.Genres.PIZZA);
                    Log.v("Rest. toString: ", ViewHolder.this.restaurant.toString());
                    if(temp != null) {
                        itemsSold.add(temp);
                        finalPrice += temp.getValue();
                    }

                    finalPrice *= 1.055; //tax rate

                    //String customer, String vendor, String vendorGoogleId, Date transactionTime, double finalPrice, Vector<Food> itemsSold
                    DataManager.getInstance().completeTransaction(new Transaction(customer, vendor, vendorGoogleId, transactionTime, finalPrice, itemsSold));

                    Log.e("Total sales: ", Integer.toString(DataManager.getInstance().getCredentialsManager().getTotalNumberOfTransactions(customer)));
                    //DataManager.getInstance().getPreferenceCache().printCache();
                    DataManager.getInstance().getRestaurantDatabase().dumpDB();
                }
            });
            */
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestaurantAdapter(List<Restaurant> myRestaurants, Context context, FragmentManager fm) {
        restaurants = myRestaurants;
        cntx = context;
        fragman = fm;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_cards, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        new populateCards(holder, position).execute();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public String getImageURL(int position){
        String imageURL = "";

        StringBuilder sb = new StringBuilder();

        if(restaurants.get(position).getPictureID().equals("")){
            return "";
        }

        sb.append(URLPART1);
        sb.append(restaurants.get(position).getPictureID());
        sb.append(URLPART2);

        imageURL = sb.toString();

        return imageURL;
    }

    private class populateCards extends AsyncTask<URL, Integer, Long> {
        InputStream content = null;
        int currentPosition = 0;
        ViewHolder holder;

        public populateCards(ViewHolder vh, int pos){
            super();
            holder = vh;
            currentPosition = pos;
        }

        protected Long doInBackground(URL... urls) {

            String imageURL = getImageURL(currentPosition);
            if(!imageURL.equals("")) {
                try {
                    url = new URL(imageURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    content = (InputStream) url.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                d = Drawable.createFromStream(content, "src");
            }
            else{
                    d = cntx.getResources().getDrawable(R.drawable.burger, cntx.getTheme());

            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result){
            holder.restaurant = restaurants.get(currentPosition);
            holder.titleText.setText(holder.restaurant.getName());
            holder.contentText.setText(holder.restaurant.getAddress());
            holder.foodPic.setImageDrawable(d);
        }
    }


    public static class MenuFragment extends DialogFragment {
        String title;
        Vector<Food> curFood;
        int orderCount = 0;
        Button buyButton;
        Restaurant restaurant;
        double finalPrice = 0;
        Vector<Food> itemsSold = new Vector<Food>();



        public MenuFragment(String restName, Vector<Food> currentFood, Restaurant restaurant){
            title = restName;
            curFood = currentFood;
             this.restaurant = restaurant;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            getDialog().setTitle(title);
            buyButton = (Button) rootView.findViewById(R.id.buy_button);

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //load up a Transaction object
                    String customer = DataManager.getInstance().getCurrentUser();
                    String vendorGoogleId = restaurant.getGooglePlacesID();
                    String vendor = DataManager.getInstance().getCredentialsManager().getUsernameBasedOnGooglePlaceID(vendorGoogleId);
                    Date transactionTime = new Date();

                    //TODO: Make this more robust, perhaps iterate through a loop of possible options from the menu list.
                    //      for example, say that when the menu butt and prices, with check boxes next to them.
                    //      when the buy button is pressed, it wouldon is pressed, a Fragment pops up on top of the RestauTrantCard and
                    //      contains a scrolling list of their menu simply iterate through the whole list of check boxes generated and
                    //      add all checked ones to a list of food items to add to the transaction



                  //  Food temp = restaurant.getFoodFromMenuByName("test");
                    //ViewHolder.this.restaurant.setGenre(RestaurantDatabase.Genres.PIZZA);
                    Log.v("Rest. toString: ", restaurant.toString());
              //      if(temp != null) {
              //          itemsSold.add(temp);
                //        finalPrice += temp.getValue();
                //    }

                    finalPrice *= 1.055; //tax rate

                    //String customer, String vendor, String vendorGoogleId, Date transactionTime, double finalPrice, Vector<Food> itemsSold
                    DataManager.getInstance().completeTransaction(new Transaction(customer, vendor, vendorGoogleId, transactionTime, finalPrice, itemsSold));

                    Log.e("Total sales: ", Integer.toString(DataManager.getInstance().getCredentialsManager().getTotalNumberOfTransactions(customer)));
                    //DataManager.getInstance().getPreferenceCache().printCache();
                    //DataManager.getInstance().getRestaurantDatabase().dumpDB();

                    dismiss();
                }
            });


            ListView lv = (ListView) rootView.findViewById(R.id.listView);

            MenuAdapter arrayAdapter = new MenuAdapter(
                    getActivity(),
                    R.layout.restaurant_list_item,
                    curFood );

            lv.setAdapter(arrayAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tv1 = (TextView) view.findViewById(R.id.order_count);
                    TextView tv2 = (TextView) view.findViewById(R.id.restaurant_name);
                    //Log.v("TextViewResult","" + tv2.getText());

                    int test = Integer.parseInt(""+tv1.getText());

                    test++;
                    tv1.setText(""+ test);


                    Food temp = restaurant.getFoodFromMenuByName(""+tv2.getText());
                    finalPrice += temp.getValue();
                    itemsSold.add(temp);
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView tv1 = (TextView) view.findViewById(R.id.order_count);
                    TextView tv2 = (TextView) view.findViewById(R.id.restaurant_name);

                    orderCount = -1;

                    tv1.setText("-1");

                    for(int i = 0; i < itemsSold.size(); i++){
                        if(itemsSold.get(i).equals(tv2.getText())){
                            itemsSold.remove(i);
                        }
                    }
                    return false;
                }
            });
            return rootView;
        }
    }

}


