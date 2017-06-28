package Launcher;

import ServeurApplication.Emission;
import ServeurApplication.ServeurApplication;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable, EventHandler<MouseEvent> {

    private final Map<ImageView, String> fileIndex = new HashMap<>();// Pas ordonner
    private List<String> fileOrder = new ArrayList<String>();
    private String currentKey = null;
    private boolean isSupressing;
    public static final String CHUTE = "CHUTE";
    public static final String START = "START";

    /* Les layouts */
    public static final ObservableList data = FXCollections.observableArrayList();
    @FXML private ListView<String> listConnecter;
    @FXML private HBox imageList;
    @FXML public Label info;
    @FXML public TextField numeroManche;
    @FXML public TextField numeroCourse;
    @FXML public TextField texte;
    @FXML public Button deconnexion;
    @FXML public Button start_button;
    @FXML public Button chute_button;
    public static Label messageAfficher;

    /* Methodes FXML */
    @FXML public void sendNumeroClick(){
        if(!numeroCourse.getText().isEmpty() && !numeroManche.getText().isEmpty()) {
            Thread t = new Thread(new Emission("course : " + numeroCourse.getText() + " , manche : " + numeroManche.getText()));
            t.setDaemon(true);
            t.start();
            numeroCourse.setText("");
            numeroManche.setText("");
        }
    }

    @FXML public void sendTexteClick(){
        if(!texte.getText().isEmpty()) {
            Thread t = new Thread(new Emission(texte.getText().toString()));
            t.setDaemon(true);
            t.start();
            texte.setText("");
        }
    }

    @FXML public void openFileChooser(ActionEvent event){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisissez une image !");
        final File fichier = chooser.showOpenDialog(null);
        if(fichier != null){
            fileIndex.clear();
            fileOrder.clear();
            final String name = fichier.getName().toLowerCase();
            if(name.endsWith(".jpg") || name.endsWith(".png")){
                final String fileName = fichier.toURI().toString();
                // Creation des miniatures
                Image image = new Image(fileName, 0, 90, true, true);
                ImageView imageView = new ImageView(image);
                imageList.getChildren().add(0, imageView);
                fileIndex.put(imageView, fileName);
                fileOrder.add(fileName);
                // Evenement ici car elle ne sont pasfbds dans le sceneBuilder
                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        }
    }

    @FXML public void chuteClick(){
        if(!ServeurApplication.getListClient().isEmpty()) {
            start_button.setDisable(false);
            chute_button.setDisable(true);
            Thread t = new Thread(new Emission(CHUTE));
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML public void startClick(){
        if(!ServeurApplication.getListClient().isEmpty()) {
            start_button.setDisable(true);
            chute_button.setDisable(false);
            Thread t = new Thread(new Emission(START));
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML public void exitClick(){
        Platform.exit();
    }

    /* Autres Méthodes */
    public void infoTouch(){
        if (Main.getServeurApp().isState())
            info.setText("Serveur demarré à " + "l'adresse " + Main.getServeurApp().getServer().getInetAddress() + " \n il écoute le port : " + Main.getServeurApp().getServer().getLocalPort());
        else
            info.setText("Erreur lors du lancement du serveur !");
    }

    public void sendImage(ActionEvent event, Image image){}

    public static void handleMessage(Socket s, String login, String msg){
        if(msg.equals(Main.STOPCLIENT)){
            data.remove(login);
            ServeurApplication.getListClient().remove(s);
        }else {
            MainController.messageAfficher.setText(login + " : " + msg);
        }
    }

    /* Override Méthodes */
    @Override public void initialize(URL location, ResourceBundle resources) {
        infoTouch();
        listConnecter.setItems(data);
        messageAfficher = info;
        start_button.setDisable(true);
    }


    @Override public void handle(MouseEvent event) {
        if(event.getSource() instanceof ImageView){
            String fileName = fileIndex.get((ImageView) event.getSource());
            if(fileName != null && !isSupressing){
                currentKey = fileName;
                Image image = new Image(fileName, false);
                sendImage(null, image);
            }else if (fileName != null && isSupressing){
                fileOrder.remove(fileName);
                fileIndex.remove((ImageView) event.getSource(),fileName);
                imageList.getChildren().remove((ImageView) event.getSource());
            }
        }
    }
}
