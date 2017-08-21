package Launcher;

import Launcher.Utilitaire.Property;
import ServeurApplication.Emission;
import ServeurApplication.MVController;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
* Created by Jean Delest on 24/06/2017.
* @author DJADJA D. Jean Delest - jeanlemirates@gmail.com
* @version 1.0
 *
 * Fenêtre principal appélée après le login
*/

public class MainController implements Initializable, EventHandler<MouseEvent> {

    private final static String AIDE_FXML = "MainControllerStage/FxmlFile/aide.fxml";
    private final static String APROPOS_FXML = "MainControllerStage/FxmlFile/apropos.fxml";
    private final String code = Main.LOG + "-" + Main.PASS + "-" + Main.getWifiAdresse().toString().substring(1);
    private final Map<ImageView, String> fileIndex = new HashMap<>();// Pas ordonner
    private List<String> fileOrder = new ArrayList<String>();
    private String currentKey = null;
    private boolean isSupressing;
    public static final String CHUTE = Property.getProperty("CHUTE");
    public static final String START = Property.getProperty("START");
    public static final String ADMIN_ID = Property.getProperty("ADMIN_ID");

    /* Stages */
    public static Stage aideStage;
    public static Stage aproposStage;
    /* Les layouts */
    @FXML private ListView<String> listConnecter;
    @FXML private HBox imageList;
    @FXML private Label info;
    @FXML private TextField numeroManche;
    @FXML private TextField numeroCourse;
    @FXML private TextField texte;
    @FXML private Button start_button;
    @FXML private Button chute_button;
    @FXML private Button course_button;
    @FXML private Button texte_button;
    @FXML private VBox rVbox;
    @FXML private CheckBox maskQrCode;
    @FXML private ComboBox<Couleur> comboBox1;
    private ImageView qrView;
    public static Label messageAfficher;
    /* Clients */
    private static Map<Socket, String> listClient = new HashMap<>();
    public static final ObservableList data = FXCollections.observableArrayList();

    public enum Couleur {
        MUTIPLE, ROUGE, BLEU, JAUNE, VERT
    }
    /* Methodes FXML */
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
                Image image = new Image(fileName, 100, 160, true, true);
                ImageView imageView = new ImageView(image);
                imageList.getChildren().add(0, imageView);
                fileIndex.put(imageView, fileName);
                fileOrder.add(fileName);
                // Evenement ici car elle ne sont pas dans le sceneBuilder
                imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        }
    }

    @FXML public void exitClick(){
        if(Main.getServeurApp().isState()) {
            Thread t = new Thread(new Emission(Main.SERVERCLOSE, ADMIN_ID));
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

    @FXML public void aideClick(){
        if(!aideStage.isShowing()) aideStage.show();
    }
    @FXML public void aproposClick(){
        if(!aproposStage.isShowing()) aproposStage.show();
    }

    @FXML public void maskQrCodeClick(){
        if(maskQrCode.isSelected()){
            qrView.setVisible(false);
        }else{
            qrView.setVisible(true);
        }
    }
    /* Autres Méthodes */
    public void infoTouch(){
        if (Main.getServeurApp().isState())
            info.setText("Serveur demarré à " + "l'adresse " + Main.getWifiAdresse().toString().substring(1)
                    + " il écoute le port : " + Main.getServeurApp().getServer().getLocalPort());
        else
            info.setText("Erreur lors du lancement du serveur !");
    }

    public void sendImage(ActionEvent event, Image image){
        /*
        try {
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", s);
            byte[] res = s.toByteArray();
            s.close();
            System.out.println("Image " + res);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    public static void handleMessage(Socket s, String id, String msg){
        if(msg.equals(Main.STOPCLIENT)){
            data.remove(id);
            listClient.remove(s);
            try{
                s.close();
            }catch (IOException e){
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }else {
            Thread t = new Thread(new Emission(msg, id));
            t.setDaemon(true);
            t.start();
            MainController.messageAfficher.setText("Expéditeur : " + id + "\n" + " Message : " + msg);
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
        BufferedImage bufferedImage = null;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, width, height);
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
        qrView = new ImageView();
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        HBox hBox = new HBox();
        hBox.getChildren().add(qrView);
        hBox.setAlignment(Pos.CENTER);
        rVbox.getChildren().add(hBox);
        /* Creation Stage */
        try {
            Parent aideP = FXMLLoader.load(getClass().getResource(AIDE_FXML));
            Parent aproposP = FXMLLoader.load(getClass().getResource(APROPOS_FXML));
            aideStage = new Stage();
            aideStage.setScene(new Scene(aideP));
            aideStage.getIcons().add(new Image(String.valueOf(getClass().getResource("Images/icon.png"))));
            aproposStage = new Stage();
            aproposStage.setScene(new Scene(aproposP));
            aproposStage.getIcons().add(new Image(String.valueOf(getClass().getResource("Images/icon.png"))));
        }catch (Exception e){
            e.printStackTrace();
        }
        /* Combo Init */
        comboBox1.getItems().addAll(
          Couleur.MUTIPLE,
          Couleur.ROUGE,
          Couleur.BLEU,
          Couleur.JAUNE,
          Couleur.VERT
        );
        comboBox1.getSelectionModel().selectFirst();
        /* Event */
        start_button.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        chute_button.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        course_button.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        texte_button.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
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
        }else if(event.getSource() == course_button){
            if(MVController.verifNumber(numeroCourse.getText()) && MVController.verifNumber(numeroManche.getText())) {
                String msg = comboBox1.getSelectionModel().getSelectedIndex() + "course : " + numeroCourse.getText()
                        + ", manche : " + numeroManche.getText();
                Thread t = new Thread(new Emission(msg, ADMIN_ID));
                t.setDaemon(true);
                t.start();
                info.setText(msg);
                numeroCourse.clear();
                numeroManche.clear();
            }else{
                showAlert(Alert.AlertType.INFORMATION, "Information sur le texte saisir", null, "Le texte saisir n'est pas " +
                        "valide merci de saisir un autre texte.");
            }
        }else if(event.getSource() == texte_button){
            if(MVController.verifString(texte.getText())) {
                String msg = comboBox1.getSelectionModel().getSelectedIndex() + texte.getText();
                Thread t = new Thread(new Emission(msg, ADMIN_ID));
                t.setDaemon(true);
                t.start();
                info.setText(msg);
                texte.clear();
            }else{
                showAlert(Alert.AlertType.INFORMATION, "Information sur le texte saisir", null, "Le texte saisir n'est pas " +
                        "valide merci de saisir un autre texte.");
            }
        }else if(event.getSource() == chute_button){
            if(!getListClient().isEmpty()) {
                start_button.setDisable(false);
                chute_button.setDisable(true);
                info.setText(CHUTE);
                Thread t = new Thread(new Emission(comboBox1.getSelectionModel().getSelectedIndex() + CHUTE, ADMIN_ID));
                t.setDaemon(true);
                t.start();
            }else{
                showAlert(Alert.AlertType.INFORMATION, "Information", null, "Il n'y a aucun " +
                        "client connecté.");
            }
        }else if(event.getSource() == start_button){
            if(!getListClient().isEmpty()) {
                start_button.setDisable(true);
                chute_button.setDisable(false);
                info.setText(START);
                Thread t = new Thread(new Emission(comboBox1.getSelectionModel().getSelectedIndex() + START, ADMIN_ID));
                t.setDaemon(true);
                t.start();
            }else{
                showAlert(Alert.AlertType.INFORMATION, "Information", null, "Il n'y a aucun " +
                        "client connecté.");
            }
        }
    }

    public void showAlert(Alert.AlertType alertType, String title, String header, String content){
        Alert alert = new Alert(alertType);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("Images/icon.png"))));
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    public static boolean removeClient(String clt){
        boolean result = false;
        for (Map.Entry<Socket, String> s : getListClient().entrySet()) {
            if(s.getValue().equals(clt)){
                listClient.remove(s.getKey(), s.getValue());
                data.remove(clt);
                return true;
            }
        }
        return result;
    }

    public static void addClient(Socket s, String id){
        MainController.data.add(id);
        MainController.getListClient().put(s, id);
    }
    public static boolean clientExist(String clt){
        for (Map.Entry<Socket, String> s : getListClient().entrySet()) {
            if(s.getValue().equals(clt)){
                return true;
            }
        }
        return false;
    }

    public static Map<Socket, String> getListClient() {
        return listClient;
    }
}
