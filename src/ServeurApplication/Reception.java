package ServeurApplication;

import Launcher.Main;
import Launcher.MainController;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *Created by Jean Delest on 24/06/2017.
 *@author DJADJA D. Jean Delest - jeanlemirates@gmail.com
 *@since 1.0
 *
 * Cette classe est appélé autant de fois qu'il y a de client qui se connecte à l'application
 */

public class Reception implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private String message = null, id = null;
    private boolean stopClient = false;

    public Reception(Socket s, String ident){
        this.socket = s;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        id = ident;
    }

    public void run() {
        while(!stopClient) {
            try {
                message = in.readLine();
                if(message.equals(Main.STOPCLIENT)) stopClient = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MainController.handleMessage(socket, id, message);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

