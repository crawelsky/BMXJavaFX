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
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {

    public static ServeurApplication sApp;
    public static MainController mainController;
    /* Stages */
    public static Stage logStage;
    public static Stage mainStage;
    /* Constants */
    public static String CONNECTED = "OK";
    public static String NOT_CONNECTED = "NOTOK";
    public static final String SERVERCLOSE = "CLOSE";
    public static final String STOPCLIENT = "STOP";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root =  FXMLLoader.load(getClass().getResource("login.fxml"));
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainParent = mainLoader.load();
        primaryStage.setTitle("BMX Panel");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        logStage = primaryStage;
        Main.mainStage = new Stage();
        Main.mainStage.setScene(new Scene(mainParent));
        Main.mainStage.setResizable(false);
        Main.mainStage.setTitle("BMX Panel");
        mainController = mainLoader.getController();
    }


    public static void main(String[] args) {
        sApp = new ServeurApplication();
        launch(args);
    }

    @Override
    public void stop(){
        if(sApp.isState()) {
            Thread t = new Thread(new Emission(Main.SERVERCLOSE));
            t.setDaemon(true);
            t.start();
            sApp.getThreadConnexion().stop();
            try {
                sApp.getServer().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Platform.exit();
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

    /* Getters and Setters */
    public static ServeurApplication getServeurApp(){
        return sApp;
    }
}
