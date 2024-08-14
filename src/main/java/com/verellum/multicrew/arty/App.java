package com.verellum.multicrew.arty;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.bytedeco.opencv.opencv_core.Mat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public class App extends Application {

    private static ScreenCapture sc;
    private static Scene scene;
    public static String[] paths = { null, null };
    public static String path1, path2, path3, path4;
    public static Mat template = imread(path1,0);

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

        path1 = "src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png";
        path2 = "src/main/resources/com/verellum/multicrew/arty/maps/test4.png";
        path3 = "src/main/resources/com/verellum/multicrew/arty/icons/circle_test2.png";
        path4 = "src/main/resources/com/verellum/multicrew/arty/maps/default_homography.xml";
        //template goes second
        paths[1] = path1;
        paths[0] = path2;
        sc = new ScreenCapture();
        launch();
        // TemplateMatch.newStyle(paths);
        // TemplateMatch.oldStyle(paths);

    }

    /**
     * @param stage JavaFX stage to host the scene
     * @throws IOException ya
     */
    @Override
    public void start(Stage stage) throws IOException {
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
     * <p> loads fxml and returns root node of the fxml
     * <p> also links reference to screencapture to the controller if
     * we are attempting to load test.fxml
     * @param fxml  The classpath to the fxml file
     * @param stage Stage which fxml controller will be linked to
     * @return Parent (JavaFX root node)
     * @throws IOException ya
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
