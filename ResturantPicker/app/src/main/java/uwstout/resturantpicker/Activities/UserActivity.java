package uwstout.resturantpicker.Activities;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import uwstout.resturantpicker.Adapters.RestaurantAdapter;
import uwstout.resturantpicker.Objects.DataManager;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class UserActivity extends AppCompatActivity{

    private static GoogleApiClient mGoogleApiClient;

    public static final String URLPART1 = "https://maps.googleapis.com/maps/api/place/search/json?radius=";
    public static final String URLPART2 = "&sensor=false&key=AIzaSyAujea38TpoRvapEhrt36y1HEpXGckPqT8&location=";
    public static final String URLPART3 = "&type=restaurant";

    public static URL url;
    public static HttpURLConnection urlConnection;

    public static Location mLastLocation;
    public static String mLatitudeText;
    public static String mLongitudeText;

    public static List<Restaurant> localRestaurants;
    JSONArray photoArray = null;

    static RestaurantAdapter adapter;
    private static List<Restaurant> demoData;
    static RecyclerView recyclerView;
    public Context context;
    private static DataManager database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        database = DataManager.getInstance();
        //database.getRestaurantDatabase().toStringIDs();

        context = getApplicationContext();

        //Instantiates Vector used to keep track of local restaurants
        localRestaurants = new Vector<Restaurant>();

        /*
        Instantiates the 'PlaceholderFragment' which is the
        fragment which holds the restaurant cards
         */
        PlaceholderFragment placeholder = new PlaceholderFragment(context);
        getFragmentManager().beginTransaction()
                .add(R.id.card_fragment_container, placeholder)
                .commit();

        /*
        Necessary to track/use google API uses
         */
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, placeholder)
                .addConnectionCallbacks(placeholder)
                .addOnConnectionFailedListener(placeholder)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    /*
    Placeholder fragment class

     This will set the proper xml layout as well as
     populate the cards with their respective data
     */
    public static class PlaceholderFragment extends Fragment  implements OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

        public static Context context;

        public PlaceholderFragment(Context cntx) {
            context = cntx;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main_user, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);

            /*
            Instnatiates the google search box and implements it's listener
             */
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.v("Place: ", "" + place.toString());
                    Log.v("Place Rating: ", "" + Float.toString(place.getRating()));
                }

                @Override
                public void onError(Status status) {
                    Log.v("An error occurred: ", "" + status);
                }
            });
        }

        public static void fetchRestaurants(){
            try {
                url = new URL(getCurrentURL(1500));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
            } catch (IOException e) {
                e.printStackTrace();
            }
            new jsonRequest().execute();

        }
        public static void createCards(){
            adapter = new RestaurantAdapter(localRestaurants, context);
            recyclerView.setAdapter(adapter);
        }

        /*
 This is a background task that when ran will query for a JSON object with
 results and organize them properly
  */
        private static class jsonRequest extends AsyncTask<URL, Integer, Long> {
            protected Long doInBackground(URL... urls) {
                try {
                    urlConnection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream in = null;
                JSONObject jsonobj = null;
                try {
                    in = urlConnection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    jsonobj=new JSONObject(convertStreamToString(in));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray resarray= null;
                try {
                    resarray = jsonobj.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(resarray.length()==0){
                    //No Results
                }

            /*
            JSON object creates Restaurant objects after parsing useful information
             */
                else{
                    int len=resarray.length();
                    long rating = 0;
                    String restName = "";
                    String address = "";
                    String id = "";
                    String pictureString = "";

                    for(int j=0;j<len;j++)
                    {
                        rating = 0;
                        restName = "";
                        address = "";
                        id = "";
                        pictureString = "";
                        try {
                            if(resarray.getJSONObject(j).has("name")) {
                                restName = resarray.getJSONObject(j).getString("name");
                            }
                            if(resarray.getJSONObject(j).has("vicinity")) {
                                address = resarray.getJSONObject(j).getString("vicinity");
                            }
                            if(resarray.getJSONObject(j).has("place_id")) {
                                id = resarray.getJSONObject(j).getString("place_id");
                            }
                            if(resarray.getJSONObject(j).has("rating")) {
                                rating = resarray.getJSONObject(j).getLong("rating");
                            }
                            if(resarray.getJSONObject(j).has("photos")) {
                                JSONArray photoArray = resarray.getJSONObject(j).getJSONArray("photos");
                                if(photoArray.getJSONObject(0).has("photo_reference")) {
                                    pictureString = photoArray.getJSONObject(0).getString("photo_reference");
                                }
                                // Log.v("Picture String", "" + pictureString);
                            }

                            //TODO: make a schnazzy filter!
                            //sample filter, adds restaurants to the screen only if they exist in DB (fetch by place ID)
                            //NOTE: The db is being populated with 3 hard-coded entries, which only have the place ID. Update the method in LoginActivity if you need to add more information to it
                            if(database != null){
                                Restaurant temp = new Restaurant(restName, address, id, rating, pictureString);
                                Restaurant mergedRestaurant = database.getRestaurantDatabase().merge(temp);
                                //will only add to list if merge is successful. merge will only be successful if the restaurant already exists in the service.
                                //NOTE: It is NOT the filter's responsibility to add these restaurants to the databases.
                                // If the Places ID is found within the merge search, it will mutate the existing entry with the values of the Restaurant passed into the merge, allowing the filter to update existing entries only.
                                if(mergedRestaurant != null){
                                    localRestaurants.add(mergedRestaurant);
                                    Log.v("Restaurant added:", mergedRestaurant.toString());
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            protected void onProgressUpdate(Integer... progress) {
            }

            protected void onPostExecute(Long result) {
                createCards();
            }
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.v("onConnected", "Success");

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.v("If Statement", "True");
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            }

            fetchRestaurants();
        }
        @Override
        public void onConnectionSuspended(int i) {

        }
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e("ERROR", "" + connectionResult.getErrorMessage() + " Error Code: " + connectionResult.getErrorCode());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       return super.onOptionsItemSelected(item);
    }


    /*
    This is a function that converts the in stream to a string to be parsed
     */
    private static String convertStreamToString(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        StringBuilder jsonstr=new StringBuilder();
        String line;
        try {
            while((line=br.readLine())!=null)
            {
                String t=line+"\n";
                jsonstr.append(t);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonstr.toString();
    }



    /*
    A string builder that will allow for flexibility in making JSON calls
     */
    public static String getCurrentURL(int radius){
        StringBuilder sb = new StringBuilder();
        sb.append(URLPART1);
        sb.append(radius);
        sb.append(URLPART2);
        sb.append(mLatitudeText);
        sb.append(",");
        sb.append(mLongitudeText);
        sb.append(URLPART3);

        Log.v("Location", mLatitudeText + " ~ " + mLongitudeText);
        Log.v("toString", sb.toString());
        return sb.toString();
    }

}
