package com.verellum.multicrew.arty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

import com.verellum.multicrew.arty.Controller;

/**
 * Hello world!
 *
 */
public class App extends Application
{

    private static ScreenCapture sc;
    private static Scene scene;

    public static void app(String[] args) throws URISyntaxException, FileNotFoundException, InterruptedException
    {
        OpenCV.loadLocally();
        Mat img1 = Imgcodecs.imread("src/main/resources/com/verellum/multicrew/arty/maps/chernobyl.png");

        String path1 = "src/main/resources/com/verellum/multicrew/arty/icons/circle.png";
        String path2 = "src/main/resources/com/verellum/multicrew/arty/maps/test6.png";
        String path3 = "src/main/resources/com/verellum/multicrew/arty/icons/circle_test2.png";
        String path4 = "src/main/resources/com/verellum/multicrew/arty/maps/default_homography.xml";
        String[] paths = {path2, path1, path3};
        new TemplateMatch().run(paths);
        /*paths[0] = path1;
        paths[1] = path2;
        paths[2] = path4;
        new AKAZEMatch().run(paths);*/
        sc = new ScreenCapture();
        launch();
    }

	@Override
	public void start(Stage stage) throws Exception {
		scene = new Scene(loadFXML("test", stage), 640, 480);
        stage.setScene(scene);
        stage.setTitle("🐾 Artillery Calculator Test Panel 🐾");
        stage.setResizable(false); //NO RESIZING !!! FUCK YOU
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setMaxWidth(Screen.getPrimary().getBounds().getMaxX());
        stage.setMaxHeight(Screen.getPrimary().getBounds().getMaxY());
        //boilerplate to close the app as soon as the main stage closes
        //because if we have popups theres a chance theres a background process we wont catch
        stage.setOnHidden(event -> Platform.exit());
        stage.show();
	}

    private static Parent loadFXML(String fxml, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent par = fxmlLoader.load();
        ((Controller)fxmlLoader.getController()).setStage(stage);
        if (fxml.equals("test"))
            ((MainController)fxmlLoader.getController()).setScreenCapture(sc);
        return par;
    }

    
    
}
