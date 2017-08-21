package Launcher.MainControllerStage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

public class AproposController implements Initializable, EventHandler<ActionEvent> {

    @FXML private VBox vBox;
    @FXML private Button fermer;
    @FXML private Button auteur;
    @FXML private Button licence;
    @FXML private Label info;
    private long val = 0;
    private WebView webView;
    private WebEngine webEngine;
    final URL urlLicence = getClass().getResource("WebPage/licence.html");
    final URL urlAuteur = getClass().getResource("WebPage/auteur.html");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webView = new WebView();
        webEngine = webView.getEngine();
        fermer.setOnAction(this);
        licence.setOnAction(this);
        auteur.setOnAction(this);
    }

    @Override public void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource() == fermer){
            if (val > 0) {
                vBox.getChildren().remove(webView);
                vBox.getChildren().add(info);
                val = 0;
            }
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }else{
            if ( val == 0) {vBox.getChildren().remove(info); vBox.getChildren().add(webView); val++;}
            if(actionEvent.getSource() == auteur) {
                webEngine.load(String.valueOf(urlAuteur));
            }else{
                webEngine.load(String.valueOf(urlLicence));
            }
        }
    }
}
