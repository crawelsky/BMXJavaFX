package Launcher;

import Launcher.Utilitaire.Hostpost;
import ServeurApplication.MVController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
*Created by Jean Delest on 24/06/2017.
*@author DJADJA D. Jean Delest - jeanlemirates@gmail.com
*@since 1.0
*
* Cette classe est appélé après l'affichage du splashscreen
*/
public class LoginController implements Initializable{
    /* Variables d'usages */
    private static String ssid;
    private static String wifipwd;
    /* Autres */
    @FXML private TextField ssidText;
    @FXML private TextField ssidpwdText;
    @FXML private Label info;
    @FXML private CheckBox wifi;
    @FXML private GridPane grille;


    /**
     * Fonction appelé lorqu'on clique sur le bouton envoyé
     */
    @FXML private void sendClick(){
        if(grille.isVisible()) {
            if (!ssidText.getText().toString().isEmpty() && !ssidpwdText.getText().toString().isEmpty()) {
                ssid = ssidText.getText().toString();
                wifipwd = ssidpwdText.getText().toString();
                if(MVController.verifString(ssid) && MVController.verifString(wifipwd))
                    Hostpost.start(ssid, wifipwd);
            } else {
                info.setText("Informations incorrectes !"); return;
            }
        }
        Main.changeStage();
    }

    /**
     * Comme sont nom l'indique cette fonction sert à
     * manager l'activation du point d'acces wifi
     * (Attention les codes utilisés pour l'activation du code wifi ne marche que pour les appareils sous windows
     * et l'application doit disposer des droits d'administarteur .
     */
    @FXML private void manageWifi(){
        if(wifi.isSelected()){
            grille.setVisible(true);
        }else{
            grille.setVisible(false);
        }
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        info.setTextFill(Color.FIREBRICK);
        wifi.selectedProperty().setValue(true);
    }
}
