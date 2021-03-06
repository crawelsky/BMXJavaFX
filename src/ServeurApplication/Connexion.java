package ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Connexion implements Runnable{

    private ServerSocket socketserver = null;
    private Socket socket = null;

    public Connexion(ServerSocket s){
        socketserver = s;
    }

    public void run() {
        try {
            while(true){
                socket = socketserver.accept();
                Thread t = new Thread(new Authentification(socket));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur");
        }
        System.out.println("Server stop");
    }
}

