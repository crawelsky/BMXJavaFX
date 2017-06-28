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
    private String login = null, pass =  null;
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
                if(MVController.isValid(login, pass)){
                    out.println(Main.CONNECTED);
                    out.flush();
                    authentifier = true;
                } else {
                    out.println(Main.NOT_CONNECTED);
                    out.flush();
                }
            }
            ServeurApplication.getListClient().put(socket, login); // Sauvegarder le socket
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if( login != "R3") MainController.data.add(login); // Ajouter le login a la liste
                }
            });
            Thread t = new Thread(new Reception(socket, in, login));
            t.setDaemon(true);
            t.start();
        } catch (IOException e) {
            System.err.println(login + " ne répond pas !");
        }
    }
}

