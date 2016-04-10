package controller;

import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class ScreenNavigator {

    public static final String MAIN    = "/view/main.fxml";
    public static final String LOGIN = "/view/login.fxml";
    public static final String INSIDE = "/view/inside.fxml";
    public static final String INSERT_EMPLOYEE = "/view/insert_employee.fxml";
    public static final String UPDATE_EMPLOYEE = "/view/update_employee.fxml";
    public static final String VINYLS_MORE = "/view/vinyls_more.fxml";
    public static final String STATISTICS_MORE = "/view/statistics_more.fxml";
        
    private static MainController mainController;

    public static void setMainController(MainController mainController) {
        ScreenNavigator.mainController = mainController;
    }

    public static void loadScreen(String fxml) {
        try {
            mainController.setScreen(FXMLLoader.load(ScreenNavigator.class.getResource(fxml)));
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}