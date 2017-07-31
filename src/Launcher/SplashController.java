package Launcher;

import Launcher.Utilitaire.Property;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by JeanDelest on 07/07/2017.
 * @author DJADJA D. Jean Delest - jeanlemirates@gmail.com
 * @since 1.0
 *
 * Cette classe est appélée dans le main elle s'affiche en prémière position
 *
 */
public class SplashController implements Initializable {

    @FXML private VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String myWeb = Property.getProperty("WEBSITEAPP");
        HBox hBox = new HBox();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;
        Label home =  new Label("Bienvenue sur BMX Panel");
        home.setTextFill(Paint.valueOf("#689f38"));
        Label web =  new Label("Vous pouvez télécharger l'appli android en flashant le code ou en allant directement sur : \n" + myWeb);
        web.setTextFill(Paint.valueOf("#689f38"));
        web.setTextAlignment(TextAlignment.CENTER);
        web.setMinHeight(35);
        Button goBtn = new Button("Go");
        goBtn.setTextFill(Paint.valueOf("#fff"));
        goBtn.setStyle("-fx-background-color:  #689f38;");
        goBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.splashStage.close();
                Main.logStage.show();
            }
        });
        BufferedImage bufferedImage = null;
        try {
            BitMatrix byteMatrix = qrCodeWriter.encode(myWeb, BarcodeFormat.QR_CODE.QR_CODE, width, height);
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.createGraphics();
            Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        ImageView qrView = new ImageView();
        qrView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        hBox.getChildren().add(goBtn);
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        vBox.getChildren().addAll(home, qrView, web, hBox);
    }
}
