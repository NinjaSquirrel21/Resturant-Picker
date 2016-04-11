package uwstout.resturantpicker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import java.util.ArrayList;
import java.util.List;

import uwstout.resturantpicker.Adapters.RestaurantAdapter;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.R;




public class UserActivity extends AppCompatActivity implements OnConnectionFailedListener{

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
            PlaceholderFragment placeholder = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.card_fragment_container, placeholder)
                    .commit();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

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

            demoData = new ArrayList<Restaurant>();

            Restaurant rest1 = new Restaurant("Rest1", "Party Time", 5);
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

            PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(Place place) {
                    Log.i( "Place: ","" + place.toString());
                }

                @Override
                public void onError(Status status) {
                    Log.i("An error occurred: ", "" + status);
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
}
