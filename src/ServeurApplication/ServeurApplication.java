package ServeurApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by didi on 22/06/17.
 */

public class ServeurApplication {

    private static Map<Socket, String> listClient = new HashMap<>();
    private static ServerSocket server = null;
    private static boolean state = false;
    private static Thread threadConnexion;

    public ServeurApplication(){
        try {
            server = new ServerSocket(5030);
            System.out.println("Le serveur a demarré sur le port :  " + server.getLocalPort());
            state = true;
            threadConnexion = new Thread(new ConnexionOk(server));
            threadConnexion.setDaemon(true);
            threadConnexion.start();
        } catch (IOException e) {
            System.err.println("Le port " + server.getLocalPort() + " est déjà utilisé !");
        }
    }

    public static boolean clientExist(String clt){
        boolean result = false;
        for (Map.Entry<Socket, String> s : getListClient().entrySet()) {
            if(s.getValue().equals(clt)){
                result = true; break;
            }
        }
        return result;
    }

    public static boolean removeClient(String clt){
        boolean result = false;
        for (Map.Entry<Socket, String> s : getListClient().entrySet()) {
            if(s.getValue().equals(clt)){
                listClient.remove(s.getKey(), s.getValue());
                return true;
            }
        }
        return result;
    }
    /* Getters and Setters */

    public static Map<Socket, String> getListClient() {
        return listClient;
    }

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
