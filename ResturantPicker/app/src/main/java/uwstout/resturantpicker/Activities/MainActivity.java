package uwstout.resturantpicker.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import uwstout.resturantpicker.Fragments.BlankFragment;
import uwstout.resturantpicker.R;

public class MainActivity extends AppCompatActivity{

/*
    public void onFragmentInteraction(Uri uri){

    }
*/
    private static Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View fragView = findViewById(R.id.fragment_container);
                if(fragView != null) {
                    if (fragView.getVisibility() == View.VISIBLE) {
                        fragView.setVisibility(View.INVISIBLE);
                    }else{
                        fragView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            BlankFragment firstFragment = new BlankFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }
}
