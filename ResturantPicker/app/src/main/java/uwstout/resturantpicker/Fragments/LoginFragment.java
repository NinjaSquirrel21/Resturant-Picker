package uwstout.resturantpicker.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import uwstout.resturantpicker.R;

public class LoginFragment extends Fragment {

    private static Button loginButton = null;
    private static Button createAccountButton = null;
    private static EditText usernameText = null;
    private static EditText passwordText = null;

    private LoginFragmentInterface mListener;

    public LoginFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button) mainView.findViewById(R.id.loginButton);
        createAccountButton = (Button) mainView.findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.sendToast("clicked!");
                usernameText = (EditText) getActivity().findViewById(R.id.editTextUsername);
                passwordText = (EditText) getActivity().findViewById(R.id.editTextPassword);

                if(usernameText.getText().toString().equals("")){
                    mListener.sendToast("No username entered! Please revise input.");

                }else if(passwordText.getText().toString().equals("")){
                    mListener.sendToast("No password entered! Please revise input.");

                }else if (mListener.login(usernameText.getText().toString(), passwordText.getText().toString())){
                    mListener.sendToast("Login successful!");
                    mListener.transitionToNextIntent();

                }else{
                    mListener.sendToast("Login failed. Please revise input.");
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.swap("accountCreate", "arg");
            }
        });

        return mainView;
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);

        //links the parent activity to the fragment via an interface reference
        if (a instanceof LoginFragmentInterface) {
            mListener = (LoginFragmentInterface) a;
        } else {
            throw new RuntimeException(a.toString()
                    + " must implement LoginFragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface LoginFragmentInterface {
        boolean login(String username, String password);
        void swap(String fragment, String args);
        void sendToast(String message);
        void transitionToNextIntent();
    }
}
