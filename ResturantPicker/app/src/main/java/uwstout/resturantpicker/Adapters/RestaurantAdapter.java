package uwstout.resturantpicker.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import uwstout.resturantpicker.Objects.Restaurant;
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




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView titleText;
        protected TextView contentText;
        protected ImageView foodPic;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.restaurant_title);
            contentText = (TextView) itemView.findViewById(R.id.data);
            foodPic = (ImageView) itemView.findViewById(R.id.foodImage);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RestaurantAdapter(List<Restaurant> myRestaurants, Context context) {
        restaurants = myRestaurants;
        cntx = context;
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
            else{d = cntx.getResources().getDrawable(R.drawable.burger, cntx.getTheme());}

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {


            Restaurant restaurant = restaurants.get(currentPosition);
            holder.titleText.setText(restaurant.getName());
            holder.contentText.setText(restaurant.getAddress());
            holder.foodPic.setImageDrawable(d);
        }
    }


}


