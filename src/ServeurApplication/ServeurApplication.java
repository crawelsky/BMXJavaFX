package ServeurApplication;

import java.io.IOException;
import java.net.*;
import java.util.*;

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
            //firstBroadcast();
            threadConnexion = new Thread(new ConnexionOk(server));
            threadConnexion.setDaemon(true);
            threadConnexion.start();
        } catch (IOException e) {
            System.err.println("Le port " + server.getLocalPort() + " est déjà utilisé !");
        }
    }

    public boolean firstBroadcast() {
        try {
            InterfaceAddress ia = null;
            String data = "serveurApplication";
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);
            int i = 0;
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                List<InterfaceAddress> list = ni.getInterfaceAddresses();
                Iterator<InterfaceAddress> it = list.iterator();
                while (it.hasNext()) {
                    ia = it.next();
                    if(ia.getBroadcast() != null && i < 2){ i++; break; }
                }

                if(i == 2) break;
            }
            System.out.println(" Broadcast = " + ia.getBroadcast());
            InetAddress broadcastAddress = ia.getBroadcast();
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, broadcastAddress, 5030);
            socket.send(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* Getters and Setters */

    public static Map<Socket, String> getListClient() {
        return listClient;
    }

    public static void setListClient(Map<Socket, String> listClient) {
        ServeurApplication.listClient = listClient;
    }

    public static ServerSocket getServer() {
        return server;
    }

    public static void setServer(ServerSocket server) {
        ServeurApplication.server = server;
    }

    public static boolean isState() {
        return state;
    }

    public static void setState(boolean state) {
        ServeurApplication.state = state;
    }

    public static Thread getThreadConnexion() {
        return threadConnexion;
    }

    public static void setThreadConnexion(Thread threadConnexion) {
        ServeurApplication.threadConnexion = threadConnexion;
    }
}
