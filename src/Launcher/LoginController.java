package Launcher;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jean Delest on 24/06/2017.
 */
public class LoginController implements Initializable{
    /* Variables d'usages */
    static private String log;
    static private String pass;
    static private String ssid;
    static private String wifipwd;
    /* Autres */
    @FXML private TextField login;
    @FXML private TextField pwd;
    @FXML private TextField ssidText;
    @FXML private TextField ssidpwdText;
    @FXML private Label info;

    @FXML private void sendClick(){
        if(!login.getText().toString().isEmpty() && !pwd.getText().toString().isEmpty() &&
                !ssidText.getText().toString().isEmpty() && !ssidpwdText.getText().toString().isEmpty()){
            log = login.getText().toString();
            pass = pwd.getText().toString();
            ssid = ssidText.getText().toString();
            wifipwd = ssidpwdText.getText().toString();
            //Hostpost.start(ssid, wifipwd);
            Main.changeStage();
        }else{
            info.setText("Informations incorrectes !");
        }
    }



    @Override public void initialize(URL location, ResourceBundle resources) {
        info.setTextFill(Color.FIREBRICK);
        login.setText("log");
        pwd.setText("pwd");
        ssidText.setText("BMX");
        ssidpwdText.setText("BMXCourse2017");
    }

    public static String getLog() {
        return log;
    }

    public static String getPass() {
        return pass;
    }

    public static String getSsid() {
        return ssid;
    }

    public static String getWifipwd() {
        return wifipwd;
    }
}
