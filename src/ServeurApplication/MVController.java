package ServeurApplication;

import Launcher.LoginController;
import Launcher.Main;

/**
 * Created by didi on 23/06/17.
 */
public class MVController {

    public static boolean isValid(String login, String pass) {
        boolean connexion = false;
        if(Main.LOG.equals(login) && Main.PASS.equals(pass)){
            connexion=true;
        }else if(Main.LOG_ARDUINO.equals(login) && Main.PASS_ARDUINO.equals(pass)){
            connexion = true;
        }
        return connexion;
    }

    public static boolean verifString(String str){
        if(str.isEmpty()) return false;

        return true;
    }

    public static boolean verifNumber(String str){
        if(str.isEmpty()) return false;
        try{

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
