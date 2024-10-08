package com.verellum.multicrew.arty;

import java.io.IOException;
import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Init extends Application {

    public static boolean debug = false;
    public static final long TICKRATE = 1000/30;

    public static void initApp(){
        try {
            Main.mapTemplate = ImageIO.read(Main.class.getResource("maps/newTemplate.png"));

            //load pings
            for (int index = 0; index <= 2; index++) {
                Main.pingTemplate[index] = ImageIO.read(Main.class.getResource("icons/circle" + index + ".png"));
            }
            

            //We don't know why this works, but it stops a lagspike on detect minimap
            ScreenCapture.bufferedImageToMat(Main.mapTemplate); 
        } catch (IOException e) {
            System.out.println("Unable to read template image");
            e.printStackTrace();
        }
        PingDetect.initialize();
        launch();
    }

    /**
     * <p> loads fxml and returns root node of the fxml
     * <p> also links reference to screencapture to the controller if
     * we are attempting to load test.fxml
     * @param fxml  The classpath to the fxml file
     * @param stage Stage which fxml controller will be linked to
     * @return Parent (JavaFX root node)
     * @throws IOException ya
     */
    public static Parent loadFXML(String fxml, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Parent par = fxmlLoader.load();
        ((Controller) fxmlLoader.getController()).setStage(stage);

        // Specific logic for the testing fxml
        if (fxml.contains("test")) {
            Main.setMainController(((MainController) fxmlLoader.getController()));
            Main.getMainController().setScreenCapture(Main.sc);
        }
        return par;
    }

    /**
     * @param stage JavaFX stage to host the scene
     * @throws IOException ya
     */
    @Override
    public void start(Stage stage) throws IOException {
        Main.scene = new Scene(loadFXML("test.fxml", stage), 720, 580);
        stage.setScene(Main.scene);
        stage.setTitle("🐾 Artillery Calculator Test Panel 🐾");
        stage.setResizable(false); // NO RESIZING !!! FUCK YOU
        stage.setMinWidth(720);
        stage.setMinHeight(580);
        stage.setMaxWidth(Screen.getPrimary().getBounds().getMaxX());
        stage.setMaxHeight(Screen.getPrimary().getBounds().getMaxY());
        // boilerplate to close the app as soon as the main stage closes
        // because if we have popups theres a chance theres a background process we wont
        // catch
        stage.setOnHidden(event -> Platform.exit());
        Main.getMainController().setInit(this);
        stage.show();
    }

    @Override
    public void stop() {
        TickFactory.blowUpTheFactory();
    }

}
