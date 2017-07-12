package ServeurApplication;

/**
 * Created by didi on 22/06/17.
 */

import Launcher.Main;
import Launcher.MainController;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;


public class Reception implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private String message = null, id = null;
    private boolean stopClient = false;

    public Reception(Socket socket, BufferedReader in, String id){
        this.in = in;
        this.id = id;
        this.socket = socket;
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
                //e.printStackTrace();
            }
        }
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}

