package controller;

import static controller.LoginController.LOGGED_EMP_ID;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Access;
import model.Employee;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import redis.clients.jedis.Jedis;

/**
 *
 * @author Jakub
 */
public class AccessesControl implements Initializable {
    
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector(); 
   
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm");
    private  Jedis jedis = null;
    private String logList;
    
    //buttons
    @FXML private Button logOff_btn;    
    @FXML private Button cancel_btn;
    
    @FXML private Label label_name;    
    @FXML private Label label_total;
    
    ObservableList<Access> access = FXCollections.observableArrayList();
    ObservableList<String> workersName = FXCollections.observableArrayList();
    ObservableList<Integer> workersID = FXCollections.observableArrayList();
    ObservableList<PieChart.Data> pieChartAccessData = FXCollections.observableArrayList();
    
    @FXML private PieChart chartAccess;
    
    //combobox
    @FXML private ComboBox<String> cb_employee;
    
    //table
    @FXML private TableView<Access> accessTable;
    @FXML private TableColumn<Access, String> column_access_since;
    @FXML private TableColumn<Access, String> column_access_to;
    @FXML private TableColumn<Access, String> column_access_time;
      
    
     
         
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
    void goBackHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
    }       
    
    
    /**
     * This method fills post combobox
     * @throws IOException 
     */
    private void fillWorkerComboBox() throws IOException, SQLException{              
       
        if(jedis==null){
            connector.connectToRedisDbs();
            jedis=connector.getRedisConnect();
        }       
        
        int id=0;
        
        connect.setAutoCommit(false);
        String query = "SELECT employee_id FROM `employee` ORDER BY `employee_id` DESC LIMIT 1";            
        statement = connect.createStatement();
        resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            id=resultSet.getInt("employee_id");
        }
        resultSet.close();        
        
        
        for(int i=1;i<=id;i++){                 
            if(jedis.hexists("user:"+i, "name")){
                workersName.add(jedis.hget("user:"+i,"name")+" "+jedis.hget("user:"+i,"surname"));
                workersID.add(i);                        
            }
        }
           
        cb_employee.getItems().addAll(workersName);
        
    }
    
    @FXML
    private void refreshAccessHandler(ActionEvent event) throws IOException {        
       
       access.clear();
       
       if(jedis==null){
            connector.connectToRedisDbs();
            jedis=connector.getRedisConnect();
        }
       
       String name = "";       
       name=cb_employee.getValue();
       label_name.setText(name);
       
       int userID = workersID.get(workersName.indexOf(name));  
       int timeTotal=0;
       for(int i=1; i<=jedis.llen("access:"+userID)-2;i+=2){
           access.add(new Access(timeParser(jedis.lindex("access:"+userID, i)),
                   timeParser(jedis.lindex("access:"+userID, i+1)),
                   timeCounter(timeParser(jedis.lindex("access:"+userID, i)),timeParser(jedis.lindex("access:"+userID, i+1))) ));
                   timeTotal += timeCounterInt(timeParser(jedis.lindex("access:"+userID, i)),timeParser(jedis.lindex("access:"+userID, i+1)));
       }       
       label_total.setText(timeTotal+" min");
       
       
       column_access_since.setCellValueFactory(new PropertyValueFactory<>("column_access_since"));
       column_access_to.setCellValueFactory(new PropertyValueFactory<>("column_access_to"));
       column_access_time.setCellValueFactory(new PropertyValueFactory<>("column_access_time"));
 
       accessTable.setItems(access);      
    } 
    
    @FXML
    private void pieChartAccess(){        
       
        if(jedis==null){
            connector.connectToRedisDbs();
            jedis=connector.getRedisConnect();
        }
        
        pieChartAccessData.clear();
          
       for(int j=0;j<workersName.size();j++){
            int userID = workersID.get(j);  
            int timeTotal=0;
            for(int i=1; i<=jedis.llen("access:"+userID)-2;i+=2){
                 timeTotal += timeCounterInt(timeParser(jedis.lindex("access:"+userID, i)),timeParser(jedis.lindex("access:"+userID, i+1)));       
            }
            
            if(timeTotal!=0){
                pieChartAccessData.add(new PieChart.Data(
                    workersName.get(j),
                    timeTotal
                ));  
            }
       }       
        chartAccess.setData(pieChartAccessData);      
    } 
    
    @FXML
    private void pieChartAccessRefresh(ActionEvent event){ 
       pieChartAccess();
    } 
    
    
    private String timeParser(String rawDate){
        //System.out.println(rawDate);
        StringBuilder date = new StringBuilder(rawDate);
        date.setCharAt(10, ' '); 
        //System.out.println(date);
        return date.toString();
    }
    
    private String timeCounter(String Date1, String Date2 ){

        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime dt1 = formatter.parseDateTime(Date1.substring(2)+":00");
        DateTime dt2 = formatter.parseDateTime(Date2.substring(2)+":00");
        Period p = new Period(dt1, dt2);
        
        long hours = p.getHours();
        long minutes = p.getMinutes();

        return (-60)*hours+minutes*(-1)+" min";
    }
    
    private int timeCounterInt(String Date1, String Date2 ){

        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime dt1 = formatter.parseDateTime(Date1.substring(2)+":00");
        DateTime dt2 = formatter.parseDateTime(Date2.substring(2)+":00");
        Period p = new Period(dt1, dt2);
        
        int hours = p.getHours();
        int minutes = p.getMinutes();

        return (-60)*hours+minutes*(-1);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        connector.connectToDbs();
        connect=connector.getConnect();
        
        connector.connectToRedisDbs();
        jedis=connector.getRedisConnect();
        
        try {
            fillWorkerComboBox();
            pieChartAccess();
        } catch (IOException ex) {
            System.out.println("Error: "+ex);
        } catch (SQLException ex) {
            Logger.getLogger(AccessesControl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
