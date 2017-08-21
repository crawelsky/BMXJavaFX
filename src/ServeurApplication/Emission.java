package ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import Launcher.Main;
import Launcher.MainController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;


public class Emission implements Runnable {

    private String message = null;
    private String id = null;

    public Emission(String msg, String ident) {
        message = msg;
        id = ident;
    }

    public void run() {
        if(!MainController.getListClient().isEmpty()) {
            for (Map.Entry<Socket, String> s : MainController.getListClient().entrySet()) {
                try {
                    PrintWriter out = new PrintWriter(s.getKey().getOutputStream());
                    if(!s.getValue().equals(Main.ID_ARDUINO)) {
                        out.println(id);
                        out.flush();
                        out.println(message.substring(1)); // On enlève le code couleur
                        out.flush();
                    }else{
                        out.println(message);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

