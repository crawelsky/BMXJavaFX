package Launcher;

import ServeurApplication.Emission;
import ServeurApplication.ServeurApplication;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

public class MainController implements Initializable, EventHandler<MouseEvent> {

    private final Map<ImageView, String> fileIndex = new HashMap<>();// Pas ordonner
    private List<String> fileOrder = new ArrayList<String>();
    private String currentKey = null;
    private boolean isSupressing;
    public static final String CHUTE = "CHUTE";
    public static final String START = "START";

    /* Les layouts */
    @FXML private ListView<String> listConnecter;
    @FXML private HBox imageList;
    @FXML public Label info;
    @FXML public TextField numeroManche;
    @FXML public TextField numeroCourse;
    @FXML public TextField texte;
    @FXML public Button start_button;
    @FXML public Button chute_button;
    @FXML public VBox rVbox;
    public static Label messageAfficher;
    public static final ObservableList data = FXCollections.observableArrayList();

    /* Methodes FXML */
    @FXML public void sendNumeroClick(){
        if(!numeroCourse.getText().isEmpty() && !numeroManche.getText().isEmpty()) {
            Thread t = new Thread(new Emission("course : " + numeroCourse.getText() + ", manche : " + numeroManche.getText()));
            t.setDaemon(true);
            t.start();
            info.setText("course : " + numeroCourse.getText() + ", manche : " + numeroManche.getText());
            numeroCourse.setText("");
            numeroManche.setText("");
        }
    }

    @FXML public void sendTexteClick(){
        if(!texte.getText().isEmpty()) {
            Thread t = new Thread(new Emission(texte.getText().toString()));
            t.setDaemon(true);
            t.start();
            info.setText(texte.getText());
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
        if(Main.getServeurApp().isState()) {
            Thread t = new Thread(new Emission(Main.SERVERCLOSE));
            t.setDaemon(true);
            t.start();
            Main.getServeurApp().getThreadConnexion().stop();
            try {
                Main.getServeurApp().getServer().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public static void handleMessage(Socket s, String id, String msg){
        if(msg.equals(Main.STOPCLIENT)){
            data.remove(id);
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ServeurApplication.getListClient().remove(s);
        }else {
            String msgToSend = id + " : " + msg;
            Thread t = new Thread(new Emission(msgToSend));
            t.setDaemon(true);
            t.start();
            MainController.messageAfficher.setText(msgToSend);
        }
    }

    /* Override Méthodes */
    @Override public void initialize(URL location, ResourceBundle resources) {
        infoTouch();
        listConnecter.setItems(data);
        messageAfficher = info;
        start_button.setDisable(true);
        int width = 100;
        int height = 100;
        final String hre = "log-pwd-"+Main.getWifiAdresse().toString().substring(1);
        BufferedImage bufferedImage = null;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(hre, BarcodeFormat.QR_CODE.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        ImageView qrView = new ImageView();
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        rVbox.getChildren().add(qrView);
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
