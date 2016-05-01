package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class LoginController implements Initializable {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector();
    
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
    private  Jedis jedis = null;
    private String logList;
    
    private String username;
    private BigInteger password;
    
    public static int LOGGED_SHOP_ID;
    public static String LOGGED_SHOP_NAME;
    public static int LOGGED_EMP_ID;
    
    @FXML private Button logIn_btn;
    @FXML private TextField username_tf;
    @FXML private PasswordField password_pf;
    @FXML private Label info_lb;
    
    /**
     * Handler of login button
     * Gets hash form of password from DB and compares with user input  
     */    
    @FXML
    void logInHandler(ActionEvent event) {
        
       try{
           String passFromDbs = "";
           
           username = username_tf.getText();
           password = hashPassword(password_pf.getText());
           connect.setAutoCommit(false);
           String query = "SELECT employee.password, employee.employee_id, employee.shop_id, "
                   + "shops.city FROM employee JOIN shops ON employee.shop_id=shops.shop_id "
                   + "WHERE employee.login=?";
           preparedStatement  =  connect.prepareStatement(query);		
           preparedStatement.setString(1,username);
           resultSet=preparedStatement.executeQuery();
            
           while(resultSet.next()){
               passFromDbs=resultSet.getString("password");
               LOGGED_SHOP_ID=resultSet.getInt("shop_id");
               LOGGED_EMP_ID=resultSet.getInt("employee_id");
               LOGGED_SHOP_NAME=resultSet.getString("city");
           }
           resultSet.close();
           connect.commit();
           
          if(passFromDbs.equals(password.toString()) && !passFromDbs.equals("")){
               
                //redis 
                Date date = new Date();              
                //System.out.println(dateFormat.format(date));
              
                
                
                logList=jedis.hget("user:"+LOGGED_EMP_ID, "log");
                
                
                if(jedis.llen(logList)%2==0){
                    //jedis=connector.getRedisConnect();
                    //Transaction t = jedis.multi();
                    jedis.lpush(logList, dateFormat.format(date));
                    //t.exec();
                    //jedis=connector.getRedisConnect();
                    ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
                }                 
                else{
                    info_lb.setText("You are already logged in!!");
                    info_lb.setStyle("-fx-text-fill: red");   
                }
           }
           else{
                info_lb.setText("Wrong password or login!!");
                info_lb.setStyle("-fx-text-fill: red");   
           }
           
       }
       catch(SQLException ex){
             if (connect != null) {
                try {
                    System.out.println(ex.getMessage());
                    System.out.println("Transaction is roller back...");
                    connect.rollback();

                } catch (SQLException ex1) {
                    Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }       
       catch(Exception e){
           info_lb.setText("Wrong password or login!!");
           info_lb.setStyle("-fx-text-fill: red");
       }
       finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        System.out.println("Program started...");         
     
        //handle erorr
        
           
        
        try {
                    
            connector.connectToDbs();
            connect=connector.getConnect();
            
            //redis
            connector.connectToRedisDbs();
            jedis=connector.getRedisConnect();
           
            
            String erorrLog=readFile("sessionerorr.txt");
            System.out.println(erorrLog);
           
            
            if(erorrLog.length()>0){
            
            String erorrUser = erorrLog.substring(0,8);
            System.out.println(erorrUser);            
            
            String erorrDate = erorrLog.substring(9,25);
            System.out.println(erorrDate); 
            
            if(jedis.llen(erorrUser)%2==1)
                jedis.lpush(erorrUser, erorrDate);
            
            clearTheFile();   
            }
                          
            
        } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Houston, we have a problem!");
                alert.setContentText("Can't connect to the database. Check your internet connection, or do something.");
                
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String exceptionText = sw.toString();

                Label label = new Label("The exception stacktrace was:");
                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);
                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);

                alert.getDialogPane().setExpandableContent(expContent);                
                alert.showAndWait();
                System.exit(1);
        }

    }
  
    /**
     * Simple hash function that convert input into hashform of password that will
     * be compared with hash in DB
     * @param pass user input
     * @return hash form of password
     */
    public BigInteger hashPassword(String pass){
        
        StringBuilder sb = new StringBuilder();
        for (char c : pass.toCharArray())
            sb.append((int)c);
        BigInteger password = new BigInteger(sb.toString());
       
        return password;
    }
    
    public void clearTheFile() throws IOException {
        FileWriter fwOb = new FileWriter("sessionerorr.txt", false); 
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
 
    private String readFile(String fileName) throws IOException {
    
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
}
    
    
    
       
}