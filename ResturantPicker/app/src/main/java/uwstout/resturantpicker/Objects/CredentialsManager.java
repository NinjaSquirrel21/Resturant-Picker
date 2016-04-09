package uwstout.resturantpicker.Objects;

import java.util.Vector;

/**
 * Created by Barrett on 4/8/2016.
 * Used by LoginActivity and its' Fragments to keep track of userbase.
 * TODO: Implement using actual database for users.
 */
public class CredentialsManager {
    private Vector<Credentials> users;

    public CredentialsManager(){
        this.users = new Vector<Credentials>();
    }

    //checks if user already exists in userbase before adding new user
    public boolean addNewUser(String username, String password){
        if(!usernameExists(username)){
            this.users.add(new Credentials(username, password));
        }
        return !usernameExists(username);
    }

    //Checks if username exists in userbase
    public boolean usernameExists(String username) {
        if (this.users != null) {
            for (int i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).usernameMatch(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    //attempt to authenticate user login
    public boolean login(String username, String password){
        boolean success = false;

        if(this.users != null){
            for(int i = 0; i < this.users.size(); i++) {
                if (this.users.get(i).authenticate(username, password)){
                    success = true;
                }
            }
        }
        return success;
    }
}

class Credentials{
    private String username;
    private String password;

    public Credentials(String username, String password){
        this.username = username;
        this.password = password;
    }

    public boolean authenticate(String username, String password){
        if((!this.username.equals(username)) || (!this.password.equals(password))){return false;}
        else{return true;}
    }

    public boolean usernameMatch(String username){
        return (username.equals(this.username));
    }
}
