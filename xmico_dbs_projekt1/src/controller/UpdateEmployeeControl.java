package controller;

import static controller.LoginController.LOGGED_EMP_ID;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import redis.clients.jedis.Jedis;

/**
 *Method for update worker
 * @author Jakub
 */
public class UpdateEmployeeControl implements Initializable {
    
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
    private int city_FK;
    private int post_FK;
    
    private int update_id;
    
    //buttons
    @FXML
    private Button logOff_btn;    
    @FXML
    private Button update_btn;
    @FXML
    private Button cancel_btn;
    
    ObservableList<String> shops_post = FXCollections.observableArrayList();
    ObservableList<String> shops_adresses = FXCollections.observableArrayList();
    
    //combobox
    @FXML
    private ComboBox<String> cb_employee_post;
    @FXML
    private ComboBox<String> cb_employee_adress;    
    
    //textfields
    @FXML
    private TextField tf_update_employee_name;
    @FXML
    private TextField tf_update_employee_surname;
    @FXML
    private TextField tf_update_employee_login;
    @FXML
    private TextField tf_update_employee_password;
    
    @FXML
    private DatePicker dp_update_employee_date;
    
    @FXML
    private Label lb_update_employee_info;
    
    
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
   
    /**
     * This method insert new employee 
     * @param event 
     */
    @FXML
    void updateEmployeeHandler(ActionEvent event) {        
                
       try{
            name = tf_update_employee_name.getText();
            surname = tf_update_employee_surname.getText();
            //login = tf_update_employee_login.getText();
            //password = hashPassword(tf_update_employee_password.getText());          
            date = dp_update_employee_date.getValue(); 
            post = cb_employee_post.getValue();
            city = cb_employee_adress.getValue();
            connect.setAutoCommit(false);
            
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
            
            if(!name.equals("") && !surname.equals("") && !post.equals("") && !city.equals("")){
           
                query = "UPDATE employee SET name=?, surname=?, position_id=?, shop_id=?, board=? WHERE employee_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setInt(3, post_FK);
                preparedStatement.setInt(4, city_FK);
                preparedStatement.setString(5, date.toString());
                preparedStatement.setInt(6, InsideController.UPDATED.getColumn_employee_id());
                preparedStatement.executeUpdate();

                connect.commit();
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
           lb_update_employee_info.setText("Fill all field!");
           lb_update_employee_info.setStyle("-fx-text-fill: red");
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
            
            tf_update_employee_name.setText(InsideController.UPDATED.getColumn_employee_name());
            tf_update_employee_surname.setText(InsideController.UPDATED.getColumn_employee_surname());                 
            cb_employee_post.setValue(InsideController.UPDATED.getColumn_employee_post());
            cb_employee_adress.setValue(InsideController.UPDATED.getColumn_employee_city());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(InsideController.UPDATED.getColumn_employee_board(), formatter);
            dp_update_employee_date.setValue(date);
            
            tf_update_employee_login.setText("*********");
            tf_update_employee_login.setDisable(true);
            tf_update_employee_password.setText("*********");
            tf_update_employee_password.setDisable(true);
            
            
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
