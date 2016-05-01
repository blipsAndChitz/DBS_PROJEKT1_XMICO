package controller;

import static controller.LoginController.LOGGED_EMP_ID;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.image.Image;
import redis.clients.jedis.Jedis;

public class Main extends Application {

    private static Connector connector = new Connector();
    
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
    private static Jedis jedis = null;
    private String logList;   
    
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
        
        connector.connectToRedisDbs();
        jedis=connector.getRedisConnect();
        
        launch(args);
    }
    
    @Override
    public void stop() throws FileNotFoundException, UnsupportedEncodingException{
        
        try{
            System.out.println("Stage is closing");
            Date date = new Date();              
                    //System.out.println(dateFormat.format(date));        

            if(jedis==null){
                connector.connectToRedisDbs();
                jedis=connector.getRedisConnect();
            }        

            if(LOGGED_EMP_ID>0){        

                logList=jedis.hget("user:"+LOGGED_EMP_ID, "log");

                if(jedis.llen(logList)%2!=0){          
                        jedis.lpush(logList, dateFormat.format(date));       
                }
            }
        }
        catch (Exception ex){
            System.out.println("Creating Log files");
            PrintWriter writer = new PrintWriter("sessionerorr.txt", "UTF-8");
            Date date = new Date();
            writer.println("access:"+LOGGED_EMP_ID);
            writer.println(dateFormat.format(date));
            writer.close();
        }
        
        jedis.close();
        
}
    
}
