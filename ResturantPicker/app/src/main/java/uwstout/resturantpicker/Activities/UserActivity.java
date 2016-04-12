package uwstout.resturantpicker.Activities;

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

import uwstout.resturantpicker.Adapters.RestaurantAdapter;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class UserActivity extends AppCompatActivity implements OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static GoogleApiClient mGoogleApiClient;

    public Button queryBtn;

    public static final String URLPART1 = "https://maps.googleapis.com/maps/api/place/search/json?radius=";
    public static final String URLPART2 = "&sensor=false&key=AIzaSyAujea38TpoRvapEhrt36y1HEpXGckPqT8&location=";
    public static final String URLPART3 = "&type=restaurant";

    public URL url;
    public HttpURLConnection urlConnection;

    public Location mLastLocation;
    public String mLatitudeText;
    public String mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        /*
        Instantiates the 'PlaceholderFragment' which is the
        fragment which holds the restaurant cards
         */
            PlaceholderFragment placeholder = new PlaceholderFragment();
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
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        /*
        TEMP
        */
        queryBtn = (Button) findViewById(R.id.queryButton);



        /*
        THIS IS TEMPORARY
         */
        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    /*
    Placeholder fragment class

     This will set the proper xml layout as well as
     populate the cards with their respective data
     */
    public static class PlaceholderFragment extends Fragment {

        RestaurantAdapter adapter;
        RecyclerView recyclerView;
        private static List<Restaurant> demoData;

        public PlaceholderFragment() {
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
            Temporary demo data
             */
            demoData = new ArrayList<Restaurant>();

            Restaurant rest1 = new Restaurant("Rest1", "Party Time"  , 5);
            Restaurant rest2 = new Restaurant("Rest2", "Party Time 2", 6);
            Restaurant rest3 = new Restaurant("Rest3", "Party Time 3", 7);
            Restaurant rest4 = new Restaurant("Rest4", "Party Time 4", 8);
            Restaurant rest5 = new Restaurant("Rest5", "Party Time 5", 9);

            demoData.add(rest1);
            demoData.add(rest2);
            demoData.add(rest3);
            demoData.add(rest4);
            demoData.add(rest5);


            adapter = new RestaurantAdapter(demoData);
            recyclerView.setAdapter(adapter);

            /*
            Instnatiates the google search box and implements it's listener
             */
            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.v( "Place: ","" + place.toString());
                    Log.v("Place Rating: ", "" + Float.toString(place.getRating()));
                }

                @Override
                public void onError(Status status) {
                    Log.v("An error occurred: ", "" + status);
                }
            });
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("ERROR","" + connectionResult.getErrorMessage() + " Error Code: " + connectionResult.getErrorCode());
    }

    /*
    This is a background task that when ran will query for a JSON object with
    results and organize them properly
     */
    private class jsonRequest extends AsyncTask<URL, Integer, Long> {
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
            else{
                int len=resarray.length();
                for(int j=0;j<len;j++)
                {
                    try {
                        Log.v("Restaurant names", resarray.getJSONObject(j).getString("name"));
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


        }
    }


    private String convertStreamToString(InputStream in) {
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public String getCurrentURL(int radius){
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
