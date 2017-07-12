package ServeurApplication;

import Launcher.LoginController;

/**
 * Created by didi on 23/06/17.
 */
public class MVController {

    public static boolean isValid(String login, String pass) {
        boolean connexion = false;
        if(LoginController.getLog().equals(login) && LoginController.getPass().equals(pass)){
            connexion=true;
        }
        return connexion;
    }
}
