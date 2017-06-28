package Launcher;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jean Delest on 24/06/2017.
 */
public class LoginController implements Initializable {
    /* Variables d'usages */
    static private String log;
    static private String pass;
    static private String ssid;
    static private String wifipwd;
    /* Autres */
    @FXML private TextField login;
    @FXML private TextField pwd;

    @FXML private void sendClick(){
        if(!login.getText().toString().isEmpty() || !pwd.getText().toString().isEmpty()){
            log = login.getText().toString();
            pass = pwd.getText().toString();
            Main.changeStage();
        }
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        login.setText("log");
        pwd.setText("pwd");
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
