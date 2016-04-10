package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("StellarWind Music Shop");
        stage.getIcons().add(new Image("/etc/icon.png"));
        stage.setScene(createScene(loadMainPane()));
        stage.show();
    }

    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane)loader.load(getClass().getResourceAsStream(ScreenNavigator.MAIN));

        MainController mainController = loader.getController();
        ScreenNavigator.setMainController(mainController);
        ScreenNavigator.loadScreen(ScreenNavigator.LOGIN);
        return mainPane;
    }

    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);

        /*scene.getStylesheets().setAll(
            getClass().getResource("vista.css").toExternalForm()
        );*/

        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
