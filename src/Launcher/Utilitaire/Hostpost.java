package Launcher.Utilitaire;

import Launcher.Main;
import ServeurApplication.ServeurApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by JeanDelest on 08/07/2017.
 */
public class Hostpost {

    public static String outputString;

    public static String start(String ssid, String pass){
        executeCommand("netsh wlan start hostednetwork");
        String activation = "The hosted network couldn't be started.";
        //int index = activation.indexOf(outputString);
        String result = "";
        if (!outputString.contains(activation)){
            executeCommand("netsh wlan stop hostednetwork");
            if (ssid != null && pass.length() >= 8){
                String startingCommand = "netsh wlan set hostednetwork mode=allow ssid=" + ssid + " key=" + pass;
                System.out.println("ixi");
                executeCommand(startingCommand);
                executeCommand("netsh wlan start hostednetwork");
                Main.wifiState = true;
            } else {
                result = "Taille minimal du mot de passe est de 8";
            }
        } else {
            result = "Activer le peripherique WiFi";
        }
        return result;
    }

    public static void stop(){
        executeCommand("netsh wlan stop hostednetwork");
        Main.wifiState = false;
    }

    public static String executeCommand(String command) {
        StringBuffer output = new StringBuffer();
        Process processCommand;
        try {
            processCommand = Runtime.getRuntime().exec(command);
            processCommand.waitFor();
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(processCommand.getInputStream()));
            while ((line = reader.readLine()) != null){
                output.append(line + "\n");
            }
            outputString = output.toString();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return outputString;
    }
}
