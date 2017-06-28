package ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;


public class Emission implements Runnable {

    private static String message = null;

    public Emission(String msg) {
        message = msg;
    }

    public void run() {
        if(!ServeurApplication.getListClient().isEmpty()) {
            for (Map.Entry<Socket, String> s : ServeurApplication.getListClient().entrySet()) {
                try {
                    PrintWriter out = new PrintWriter(s.getKey().getOutputStream());
                    out.println(message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

