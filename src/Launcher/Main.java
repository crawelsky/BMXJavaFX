package Launcher;

import Launcher.Utilitaire.Hostpost;
import Launcher.Utilitaire.Property;
import ServeurApplication.Emission;
import ServeurApplication.ServeurApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
* Created by Jean Delest on 24/06/2017.
* @author DJADJA D. Jean Delest - jeanlemirates@gmail.com
* @since 1.0
*
* Point d'entré du logiciel
* Class principal toutes les autres classes sont lancés à partir d'ici
*
*/
public class Main extends Application {

    final public static String SPLASHSCREEN_FXML = "FxmlFile/splashscreen.fxml";
    final public static String LOGIN_FXML = "FxmlFile/login.fxml";
    final public static String MAIN_SCREEN_FXML = "FxmlFile/main.fxml";
    final public static String LOGIN_CSS = "CssFile/Login.css";
    /**
     * Application Serveur
     * Cette classe contient tout sur le serveur( échanges, analyse ...)
     */
    public static ServeurApplication sApp;
    /* Stages */
    public static Stage logStage;
    public static Stage mainStage;
    public static Stage splashStage;
    /* Constants */
    public static String CONNECTED = Property.getProperty("CONNECTED");
    public static String NOT_CONNECTED = Property.getProperty("NOT_CONNECTED");
    public static String ALREADY_EXIST = Property.getProperty("ALREADY_EXIST");
    public static final String SERVERCLOSE = Property.getProperty("SERVERCLOSE");
    public static final String STOPCLIENT = Property.getProperty("STOPCLIENT");
    public static String LOG = generateValue();
    public static String PASS = generateValue();
    public static String LOG_ARDUINO = Property.getProperty("LOG_ARDUINO");   // A ne surtout pas modifié
    public static String PASS_ARDUINO = Property.getProperty("PASS_ARDUINO"); // A ne surtout pas modifié
    public static String ID_ARDUINO = Property.getProperty("ID_ARDUINO");  // A ne surtout pas modifié
    public static boolean wifiState = false;

    /**
     * Fonction de demarrage JAVAFX
     * @param primaryStage première fenêtre de JAVAFX
     * @throws Exception lorsque le chargement de la feuille fxml échoue par exemple
     */
    @Override public void start(Stage primaryStage) throws Exception{
        Parent mainP = FXMLLoader.load(getClass().getResource(MAIN_SCREEN_FXML));
        Parent loginP =  FXMLLoader.load(getClass().getResource(LOGIN_FXML));
        primaryStage.setTitle("BMX Panel - Paramètres WIFI");
        Scene loginScene  = new Scene(loginP);
        loginScene.getStylesheets().add(LoginController.class.getResource(LOGIN_CSS).toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("Images/icon.png"))));
        logStage = primaryStage;
        mainStage = new Stage();
        mainStage.setScene(new Scene(mainP));
        mainStage.setTitle("BMX Panel");
        mainStage.getIcons().add(new Image(String.valueOf(getClass().getResource("Images/icon.png"))));
        /* Lancement du splashscreen */
        runSplashScreen();
    }


    public static void main(String[] args) {
        sApp = new ServeurApplication();
        launch(args);
    }

    /**
     * Fonction appelé lorsque l'application doit fermer
     */
    @Override
    public void stop(){
        Hostpost.stop();
        if(getServeurApp().isState()) {
            Thread t = new Thread(new Emission("0" + Main.SERVERCLOSE, MainController.ADMIN_ID));
            t.setDaemon(true);
            t.start();
            getServeurApp().getThreadConnexion().stop();
            try {
                getServeurApp().getServer().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Platform.exit();
    }

    /**
     * Fonction utilisé pour lancer le splashscreen
     */
    private void runSplashScreen(){
        try {
            FXMLLoader loaderSplach = new FXMLLoader(getClass().getResource(SPLASHSCREEN_FXML));
            Parent splashLoad = loaderSplach.load();
            splashStage = new Stage();
            splashStage.setScene(new Scene(splashLoad));
            splashStage.initStyle(StageStyle.TRANSPARENT);
            splashStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void changeStage(){
        if(logStage.isShowing()){
            logStage.hide();
            mainStage.show();
        }else{
            logStage.show();
            mainStage.hide();
        }
    }

    public void getNetIp(){
        try {
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            System.out.println(br.readLine());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /* Getters and Setters */
    public static ServeurApplication getServeurApp(){
        return sApp;
    }

    /**
     * Cette fonction sert à obtenir l'adresse ip du serveur
     * @return l'adresse ip local du periphérique wifi, j'avoue elle est écrite de façon un archaique
     * (mais vu le temps...)
     */
    public static InetAddress getWifiAdresse(){
        try {
            InterfaceAddress ia = null;
            int i = 0;
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                if(ni.getName().contains("wlan")) {
                    List<InterfaceAddress> list = ni.getInterfaceAddresses();
                    Iterator<InterfaceAddress> it = list.iterator();
                    ia = it.next();
                    break;
                }
            }
            return ia.getAddress();
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Code pour permettant de generer une serie de caractère aléatoire
     * Code provenant de http://codes-sources.commentcamarche.net/source/55168-generateur-de-mot-de-passe
     * @return une chaine de caractère de taille 10
     */
    public static String generateValue(){
        String result = "";
        final Random rand = new Random();
        final String Xsi ="abcdefghijklmnopqrstuvwxyz";
        final String Gamm ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String Iot = "1234567890";
        final String Zeta="&~#|`_)('/?,;:.";
        {
            while (result.length() < 10) {
                int rPick = rand.nextInt(4);
                if (rPick ==0) {
                    int erzat = rand.nextInt(25);
                    result += Xsi.charAt(erzat);
                } else if (rPick == 1) {
                    int erzat = rand.nextInt(9);
                    result += Iot.charAt(erzat);
                } else if (rPick==2) {
                    int erzat=rand.nextInt(25);
                    result += Gamm.charAt(erzat);
                }else if (rPick==3) {
                    int erzat=rand.nextInt(15);
                    result += Zeta.charAt(erzat);
                }
            }
        }
        return result;
    }
}
