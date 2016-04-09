package uwstout.resturantpicker.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import uwstout.resturantpicker.Fragments.AccountCreateFragment;
import uwstout.resturantpicker.Fragments.BlankFragment;
import uwstout.resturantpicker.Fragments.LoginFragment;

import uwstout.resturantpicker.Objects.CredentialsManager;
import uwstout.resturantpicker.R;

public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginFragmentInterface,
        AccountCreateFragment.AccountCreateFragmentInterface{

    private static Toast toast = null;
    private static CredentialsManager userbase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userbase = new CredentialsManager();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            //create an instance of our first fragment and commit it to the frag manager
            LoginFragment firstFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    //implemented through the LoginFragmentInterface
    public void swap(String fragment, String args){
        Fragment f;

        switch(fragment){
            case "login": f = new LoginFragment(); break;
            case "accountCreate": f = new AccountCreateFragment(); break;
            default: f = new BlankFragment(); break;
        }

        /*
        //debug
        if(f instanceof LoginFragment) {
            sendToast("Switching to LoginFragment");
        }else{
            sendToast("Switching to AccountCreateFragment");
        }
        */

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, f);
        transaction.addToBackStack(fragment);

        // Commit the transaction
        transaction.commit();
    }

    //callback used to go to the next activity when user authentication is successful
    public void transitionToNextIntent(){
        //TODO: make this go to the initial picks screen instead
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    //implemented through each fragment's interface, for using the same instance of Toast
    public void sendToast(String message){
        if (toast != null) {
            toast.cancel();
        }
        Context context = getApplicationContext();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    //implemented via AccountCreateFragmentInterface
    public boolean usernameExists(String username){
        if(userbase != null){ return userbase.usernameExists(username);}
        else return false;
    }

    //implemented via AccountCreateFragmentInterface
    public boolean createAccount(String username, String password){
        if(userbase != null) return userbase.addNewUser(username, password);
        else return false;
    }

    //implemented via LoginFragmentInterface
    public boolean login(String username, String password){
        if(userbase != null) return userbase.login(username, password);
        else return false;
    }
}
