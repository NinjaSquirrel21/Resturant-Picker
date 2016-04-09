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

public class AccountCreateFragment extends Fragment{

    private static Button createNewAccountButton = null;
    private static EditText usernameText = null;
    private static EditText passwordText = null;

    private AccountCreateFragmentInterface mListener;

    public AccountCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_account_create, container, false);

        createNewAccountButton = (Button) mainView.findViewById(R.id.createNewAccountButton);

        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameText = (EditText) getActivity().findViewById(R.id.editTextNewUsername);
                passwordText = (EditText) getActivity().findViewById(R.id.editTextNewPassword);

                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();

                if (username.equals("")) {
                    mListener.sendToast("No username entered! Please revise input.");

                } else if (password.equals("")) {
                    mListener.sendToast("No password entered! Please revise input.");

                } else if (mListener.usernameExists(username)) {
                    mListener.sendToast("Username already exists! Please revise input.");
                } else {
                    mListener.sendToast("Account creation successful!");
                    mListener.createAccount(username, password);

                    //getActivity().setContentView(R.layout.activity_login);
                    mListener.swap("login", "arg");

                }
            }
        });

        return mainView;
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(getActivity());

        //links the parent activity to the fragment via an interface reference
        if (a instanceof AccountCreateFragmentInterface) {
            mListener = (AccountCreateFragmentInterface) a;
        } else {
            throw new RuntimeException(a.toString()
                    + " must implement AccountCreateFragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface AccountCreateFragmentInterface {
        void sendToast(String message);
        boolean usernameExists(String username);
        boolean createAccount(String username, String password);
        void swap(String fragment, String args);
    }
}
