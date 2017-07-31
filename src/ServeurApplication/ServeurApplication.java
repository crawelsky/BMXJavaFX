package ServeurApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import ServeurApplication.Connexion;

/**
 * Created by didi on 22/06/17.
 */

public class ServeurApplication {

    private static ServerSocket server = null;
    private static boolean state = false;
    private static Thread threadConnexion;


    public ServeurApplication() {
        try {
            server = new ServerSocket(5030);
            System.out.println("Le serveur a demarré sur le port :  " + server.getLocalPort());
            state = true;
            threadConnexion = new Thread(new Connexion(server));
            threadConnexion.setDaemon(true);
            threadConnexion.start();
        } catch (IOException e) {
            System.err.println("Le port " + server.getLocalPort() + " est déjà utilisé !");
        }
    }

    /* Getters and Setters */

    public static ServerSocket getServer() {
        return server;
    }

    public static boolean isState() {
        return state;
    }

    public static Thread getThreadConnexion() {
        return threadConnexion;
    }
}
