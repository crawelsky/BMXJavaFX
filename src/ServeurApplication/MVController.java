package ServeurApplication;

import Launcher.LoginController;

/**
 * Created by didi on 23/06/17.
 */
public class MVController {

    public static boolean isValid(String login, String pass) {
        boolean connexion = false;
        if(login.equals(LoginController.getLog()) && pass.equals(LoginController.getPass())){
            connexion=true;
        }
        return connexion;
    }
}
