package Launcher.Utilitaire;

import Launcher.Main;

import java.util.Properties;

public class Property {

    public static String getProperty(String id){
        try {
            Properties prop = new Properties();
            prop.load(Main.class.getClassLoader().getResourceAsStream("prop.properties"));
            return prop.getProperty(id);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
