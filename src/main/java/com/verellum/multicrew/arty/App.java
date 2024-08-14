package com.verellum.multicrew.arty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.verellum.multicrew.arty.Controller;

/**
 * Hello world!
 *
 */
public class App extends Application {

    private static ScreenCapture sc;
    private static Scene scene;
    public static String[] paths = { null, null };

    /**
     * @param args
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public static void app(String[] args) {
        // OpenCV.loadLocally();
        // Mat img1 =
        // Imgcodecs.imread("src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png");

        String path1 = "src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png";
        String path2 = "src/main/resources/com/verellum/multicrew/arty/maps/test4.png";
        String path3 = "src/main/resources/com/verellum/multicrew/arty/icons/circle_test2.png";
        String path4 = "src/main/resources/com/verellum/multicrew/arty/maps/default_homography.xml";
        // template goes second
        paths[0] = path2;
        paths[1] = path1;

        sc = new ScreenCapture();
        launch();
        // TemplateMatch.newStyle(paths);
        // TemplateMatch.oldStyle(paths);

    }

    /**
     * @param stage TODO add description
     * @throws Exception
     *                   TODO add description
     *                   Starts the javafx or whatever idk
     */
    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("test.fxml", stage), 640, 480);
        stage.setScene(scene);
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

    /**
     * @param fxml  The classpath to the fxml file
     * @param stage TODO add description
     * @return Parent TODO add description
     * @throws IOException
     *                     Loads the fxml configuration from file
     *                     TODO explain the other logic
     */
    private static Parent loadFXML(String fxml, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Parent par = fxmlLoader.load();
        ((Controller) fxmlLoader.getController()).setStage(stage);

        // Specific logic for the testing fxml
        if (fxml.equals("test.fxml"))
            ((MainController) fxmlLoader.getController()).setScreenCapture(sc);
        return par;
    }

}
