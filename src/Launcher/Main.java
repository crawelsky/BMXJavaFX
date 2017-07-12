package Launcher;

import ServeurApplication.Emission;
import ServeurApplication.ServeurApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Main extends Application {

    final public static String SPLASHSCREEN_FXML = "splashscreen.fxml";
    final public static String LOGIN_FXML = "login.fxml";
    final public static String MAIN_SCREEN_FXML = "main.fxml";
    /* Utilis */
    public static MainController mainController;
    public static ServeurApplication sApp;
    /* Stages */
    public static Stage logStage;
    public static Stage mainStage;
    public static Stage splashStage;
    /* Constants */
    public static String CONNECTED = "OK";
    public static String NOT_CONNECTED = "NOTOK";
    public static final String SERVERCLOSE = "CLOSE";
    public static final String STOPCLIENT = "STOP";

    @Override public void start(Stage primaryStage) throws Exception{
        Parent root =  FXMLLoader.load(getClass().getResource(LOGIN_FXML));
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(MAIN_SCREEN_FXML));
        Parent mainParent = mainLoader.load();
        primaryStage.setTitle("BMX Panel");
        Scene loginScene  = new Scene(root);
        loginScene.getStylesheets().add(LoginController.class.getResource("Login.css").toExternalForm());
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        logStage = primaryStage;
        mainStage = new Stage();
        mainStage.setScene(new Scene(mainParent));
        mainStage.setTitle("BMX Panel");
        mainController = mainLoader.getController();
        /* Lancement du splashscreen */
        runSplashScreen();
    }


    public static void main(String[] args) {
        sApp = new ServeurApplication();
        launch(args);
    }

    @Override
    public void stop(){
        Hostpost.stop();
        if(getServeurApp().isState()) {
            Thread t = new Thread(new Emission(Main.SERVERCLOSE));
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
}
