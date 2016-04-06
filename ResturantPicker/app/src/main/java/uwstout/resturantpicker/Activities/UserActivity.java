package uwstout.resturantpicker.Activities;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uwstout.resturantpicker.Adapters.RestaurantAdapter;
import uwstout.resturantpicker.Objects.Restaurant;
import uwstout.resturantpicker.R;

public class UserActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
            PlaceholderFragment placeholder = new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.card_fragment_container, placeholder)
                    .commit();
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        RestaurantAdapter adapter;
        RecyclerView recyclerView;
        private static List<Restaurant> demoData;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
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
            /*String c = "Restaurant";
            for (byte i = 0; i < 5; i++) {
                Restaurant rest = new Restaurant();
                rest.name = c.concat("a");
                rest.cost = (byte) (20 + i);
                demoData.add(rest);
            }*/
            Restaurant rest1 = new Restaurant("Rest1","Party Time",5);
            Restaurant rest2 = new Restaurant("Rest2","Party Time2",6);
            Restaurant rest3 = new Restaurant("Rest3","Party Time3",7);
            Restaurant rest4 = new Restaurant("Rest4","Party Time4",8);
            Restaurant rest5 = new Restaurant("Rest5","Party Time5",9);
            demoData.add(rest1);
            demoData.add(rest2);
            demoData.add(rest3);
            demoData.add(rest4);
            demoData.add(rest5);


            adapter = new RestaurantAdapter(demoData);
            recyclerView.setAdapter(adapter);
        }
    }
}
