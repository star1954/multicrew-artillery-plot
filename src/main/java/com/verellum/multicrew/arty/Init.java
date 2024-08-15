package com.verellum.multicrew.arty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Init extends Application {

    public static Path tempDir;
    public static BufferedImage template;

    public static void initApp(){
        //extractResources();
        
        try {
            Main.mapTemplate = ImageIO.read(Main.class.getResource("maps/chernobyl.png"));
            //We don't know why this works, but it stops a lagspike on detect minimap
            ScreenCapture.bufferedImageToMat(Main.mapTemplate); 
        } catch (IOException e) {
            System.out.println("Unable to read template image");
            e.printStackTrace();
        }
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
    private static Parent loadFXML(String fxml, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Parent par = fxmlLoader.load();
        ((Controller) fxmlLoader.getController()).setStage(stage);

        // Specific logic for the testing fxml
        if (fxml.equals("test.fxml"))
            ((MainController) fxmlLoader.getController()).setScreenCapture(Main.sc);
        return par;
    }

    /**
     * @param stage JavaFX stage to host the scene
     * @throws IOException ya
     */
    @Override
    public void start(Stage stage) throws IOException {
        Main.scene = new Scene(loadFXML("test.fxml", stage), 640, 480);
        stage.setScene(Main.scene);
        stage.setTitle("🐾 Artillery Calculator Test Panel 🐾");
        stage.setResizable(false); // NO RESIZING !!! FUCK YOU
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setMaxWidth(Screen.getPrimary().getBounds().getMaxX());
        stage.setMaxHeight(Screen.getPrimary().getBounds().getMaxY());
        // boilerplate to close the app as soon as the main stage closes
        // because if we have popups theres a chance theres a background process we wont
        // catch
        stage.setOnHidden(event -> Platform.exit());
        stage.show();
    }

}
