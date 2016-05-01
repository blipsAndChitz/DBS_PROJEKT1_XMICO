package controller;

import static controller.LoginController.LOGGED_EMP_ID;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Jakub
 */
public class InsertEmployeeControl implements Initializable {
    
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector(); 
   
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
    private  Jedis jedis = null;
    private String logList;
    
    private String name;
    private String surname;
    private String login;
    private BigInteger password;
    private String city;
    private String post;
    private LocalDate date;
    private String age="0";
    private String addinfo;
    private int city_FK;
    private int post_FK;
    
    //buttons
    @FXML private Button logOff_btn;    
    @FXML private Button insert_new_btn;
    @FXML private Button cancel_btn;
    
    ObservableList<String> shops_post = FXCollections.observableArrayList();
    ObservableList<String> shops_adresses = FXCollections.observableArrayList();
    
    //combobox
    @FXML private ComboBox<String> cb_employee_post;
    @FXML private ComboBox<String> cb_employee_adress;    
    
    //textfields
    @FXML private TextField tf_insert_employee_name;
    @FXML private TextField tf_insert_employee_surname;
    @FXML private TextField tf_insert_employee_login;
    @FXML private TextField tf_insert_employee_password;
    @FXML private TextField tf_insert_employee_age;
    @FXML private TextField tf_insert_employee_addinfo;
    
    @FXML private DatePicker dp_insert_employee_date;
    
    @FXML private Label lb_insert_employee_info;
    
    
    /**
     * This method fills post combobox
     * @throws IOException 
     */
    private void fillPostComboBox() throws IOException{              
        try{
            connect.setAutoCommit(false);
            String query = "SELECT position.post FROM position ORDER BY post ASC FOR UPDATE";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                shops_post.add(resultSet.getString("post"));
            }
            resultSet.close();            
            cb_employee_post.getItems().addAll(shops_post);
        }
        catch(Exception e){
            System.out.println("Error: "+e);
        }    
    }
    
    /**
     * This method fills adress combobox
     * @throws IOException 
     */
    private void fillCityComboBox() throws IOException{              
        try{
            connect.setAutoCommit(false);
            String query = "SELECT shops.city FROM shops ORDER BY city ASC FOR UPDATE";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                shops_adresses.add(resultSet.getString("city"));
            }
            resultSet.close();            
            cb_employee_adress.getItems().addAll(shops_adresses);
        }
        catch(Exception e){
            System.out.println("Error: "+e);
        }    
    }
    
    @FXML
    void logOffHandler(ActionEvent event) {
        
        if(jedis==null){
            connector.connectToRedisDbs();
            jedis=connector.getRedisConnect();
        }
        
        java.util.Date date = new java.util.Date();              
        //System.out.println(dateFormat.format(date));
              
        logList=jedis.hget("user:"+LOGGED_EMP_ID, "log");
        jedis.lpush(logList, dateFormat.format(date));
        
        ScreenNavigator.loadScreen(ScreenNavigator.LOGIN);
    }
    
    @FXML
    void goBack(ActionEvent event) {
        try {
            connect.commit();
            ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
        } 
        catch (SQLException ex) {
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
        catch (Exception ex) {
              System.out.println("Chyba "+ex);
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    void getDate(ActionEvent event) {
          date = dp_insert_employee_date.getValue(); 
     }
   
    /**
     * This method insert new employee 
     * @param event 
     */
    @FXML
    void insertNewEmployeeHandler(ActionEvent event) {        
                
       try{
            boolean val=true;
            connect.setAutoCommit(false);
            name = tf_insert_employee_name.getText();
            surname = tf_insert_employee_surname.getText();
            login = tf_insert_employee_login.getText();
            password = hashPassword(tf_insert_employee_password.getText());          
            post = cb_employee_post.getValue();
            city = cb_employee_adress.getValue();
            date=dp_insert_employee_date.getValue(); 
            age=tf_insert_employee_age.getText();
            addinfo=tf_insert_employee_addinfo.getText();
            
            if(age.equals("")){
                age="0";
            }
          
            String query = "SELECT shops.shop_id FROM shops WHERE shops.city='"+city+"'";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
               city_FK=resultSet.getInt("shop_id");
            }           
           
            query = "SELECT position.position_id FROM position WHERE position.post='"+post+"'";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
               post_FK=resultSet.getInt("position_id");
            }
            
            query = "SELECT employee.login FROM employee";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
               String mylogin = resultSet.getString("login");
               if(login.equals(mylogin)){
                    lb_insert_employee_info.setText("Login already exist! ");
                    lb_insert_employee_info.setStyle("-fx-text-fill: red");
                    val=false;
                    break;
               }
               else val=true;
            }
                        
            if(!name.equals("") && !surname.equals("") && !login.equals("") && !password.equals("") &&
                    !post.equals("") && !city.equals("") && val){
           
                query = "INSERT INTO employee (name, surname, login, password, position_id, shop_id, board, age, add_info ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, login);
                preparedStatement.setString(4, password.toString());
                preparedStatement.setInt(5, post_FK);
                preparedStatement.setInt(6, city_FK);
                preparedStatement.setString(7, date.toString());
                preparedStatement.setString(8, age);
                preparedStatement.setString(9, addinfo);
                preparedStatement.executeUpdate();
                
                int id=0;
                
                query = "SELECT * FROM `employee` WHERE 1 ORDER BY employee_id DESC LIMIT 1";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                   id=resultSet.getInt("employee_id");
                }                
                
                connect.commit();
                
                //redis                
                Map<String, String> hash = new HashMap<String, String>();
                hash.put("name",name);
                hash.put("surname",surname);
                hash.put("shop",city);
                hash.put("log", "access:"+id);               
                
                if(id>0){                
                    jedis.hmset("user:"+id, hash);
                }
                
                ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
            }                     
       }
       catch (SQLException ex) {
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
        catch (Exception ex) {
            System.out.println("Error: "+ ex);
           lb_insert_employee_info.setText("Fill all field!");
           lb_insert_employee_info.setStyle("-fx-text-fill: red");
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }       
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        connector.connectToDbs();
        connect=connector.getConnect();
        
        connector.connectToRedisDbs();
        jedis=connector.getRedisConnect();
        
        try {
            fillPostComboBox();
            fillCityComboBox();
        } catch (IOException ex) {
            System.out.println("Error: "+ex);
        }
    }
    
    /**
     * Hasher of password
     * @param pass input
     * @return hash form of input
     */
    public BigInteger hashPassword(String pass){
        
        StringBuilder sb = new StringBuilder();
        for (char c : pass.toCharArray())
        sb.append((int)c);
        BigInteger password = new BigInteger(sb.toString());
       
        return password;
    }
    
    
}
