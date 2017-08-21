package ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import Launcher.Main;
import Launcher.MainController;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Authentification implements Runnable {

    private Socket socket;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String login = null, pass =  null, id = null;
    private boolean authentifier = false;

    public Authentification(Socket s){
        socket = s;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            while(!authentifier){
                // Attends le login
                login = in.readLine();
                // Attends le mot de passe
                pass = in.readLine();
                // Attends l'identifiant
                id = in.readLine();
                if(MVController.isValid(login, pass)){
                    if(Main.ID_ARDUINO.equals(id)) {
                        if (!MainController.clientExist(id)) {
                            out.println("0" + Main.CONNECTED);  authentifier = true;
                        } else {
                            out.println("0" + Main.ALREADY_EXIST);
                        }
                    }else{
                        if (!MainController.clientExist(id)) {
                            out.println(Main.CONNECTED);
                            authentifier = true;
                        } else {
                            out.println(Main.ALREADY_EXIST);
                        }
                    }
                } else {
                    if(Main.ID_ARDUINO.equals(id)) {
                        out.println("0" + Main.NOT_CONNECTED);
                    }else {
                        out.println(Main.NOT_CONNECTED);
                    }
                }
                out.flush();
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if(MainController.clientExist(id))  {
                        MainController.removeClient(id);
                    }
                    MainController.addClient(socket, id);
                }
            });
            Thread t = new Thread(new Reception(socket, id));
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            System.err.println(login + " ne répond pas !");
        }
    }
}