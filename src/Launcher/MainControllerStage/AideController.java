package Launcher.MainControllerStage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AideController implements Initializable, EventHandler<ActionEvent> {
    @FXML private WebView webView;
    @FXML private Button fermer;

    @FXML private void fermerClick(){
        fermer.setOnAction(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final WebEngine webEngine = webView.getEngine();
        URL url = getClass().getResource("WebPage/aide.html");
        webEngine.load(String.valueOf(url));
    }


    @Override public void handle(ActionEvent actionEvent) {
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
