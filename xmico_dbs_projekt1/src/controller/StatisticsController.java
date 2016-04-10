package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.TimeStats;

/**
 * Controller class for screen "More Statistics"
 * Handles 3 tabs Income per employee, Sold items per each month and Bestselling authors
 * @author Jakub Mičo
 */
public class StatisticsController implements Initializable {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector();  
    
    @FXML
    private Button logOff_btn;
    
    @FXML
    private Button goBack_btn;
    
    @FXML
    void logOffHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.LOGIN);
    }
    
    @FXML
    void goBackHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
    }
    
    // INCOME EMPLOYEE__________________________________________________________
    
    @FXML private Button ms_refresh_btn;   
    @FXML private BarChart<String, Integer> barChart;
    @FXML private CategoryAxis xAxis2;    
    ObservableList<String> employees = FXCollections.observableArrayList();
    
    /**
     * This method set axis value and whole graf on first tab
     */
    public void setIncomeByEmployeeData() {
        try {
            employees.clear();
            connect.setAutoCommit(false);
            String query="SELECT employee.name FROM (SELECT ROUND ( vinyls.price * sv.num_of_sold,"
                     + " 2 ) AS total, sv.employee_id FROM ( SELECT * FROM sold_vinyls ) AS sv "
                     + "JOIN vinyls ON sv.vinyl_id = vinyls.vinyls_id ) AS result JOIN employee ON "
                     + "result.employee_id=employee.employee_id GROUP BY result.employee_id ORDER BY employee.name";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                employees.add(resultSet.getString("name"));
            }
            resultSet.close();
            xAxis2.setCategories(employees);
            
            XYChart.Series<String, Integer> series = new XYChart.Series<>();
            
            query="SELECT SUM(result.total) AS profit, employee.name FROM ( SELECT ROUND ( "
                    + "vinyls.price * sv.num_of_sold, 2 ) AS total, sv.employee_id FROM ( SELECT"
                    + " * FROM sold_vinyls ) AS sv JOIN vinyls ON sv.vinyl_id = vinyls.vinyls_id )"
                    + " AS result JOIN employee ON result.employee_id = employee.employee_id GROUP BY"
                    + " result.employee_id ORDER BY employee.name";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                series.getData().add(new XYChart.Data<>(
                        resultSet.getString("name"),
                        resultSet.getInt("profit")
                ));
            }
            resultSet.close();
            connect.commit();  
            series.setName("Income in (€)/employee");
            barChart.getData().retainAll();
            barChart.getData().add(series);            
        } catch (SQLException ex) {            
            if (connect != null) {
                    try {
                        System.out.println(ex.getMessage());
                        System.out.println("Transaction is roller back...");
                        connect.rollback();

                    } catch (SQLException ex1) {
                        Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                    }
            }
        } finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } 
    }
    
    @FXML
    private void refreshIncomeChart(ActionEvent event) throws IOException {        
        setIncomeByEmployeeData();
    }
    
    // INCOME YEAR______________________________________________________________
    
    @FXML private Button monthly_refresh_btn;    
    @FXML private BarChart<String, Integer> barChartYears;
    @FXML private CategoryAxis xAxis;
    @FXML private ComboBox<String> cb_ms_year;    
    ObservableList<String> monthNames = FXCollections.observableArrayList();
    ObservableList<String> years = FXCollections.observableArrayList();
    
    /**
     * This method fill year combo box on second tab
     */
    private void fillYearComboBox(){              
        try{
            years.clear();
            connect.setAutoCommit(false);
            cb_ms_year.getSelectionModel().clearSelection();
            cb_ms_year.getItems().clear();
            String query="SELECT EXTRACT(YEAR FROM sold_vinyls.date) AS year FROM `sold_vinyls` GROUP BY year ORDER BY year DESC LIMIT 5";           
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                years.add(resultSet.getString("year"));
            }
            resultSet.close();
            connect.commit();            
            cb_ms_year.getItems().addAll(years);
        } catch (SQLException ex) {            
            if (connect != null) {
                    try {
                        System.out.println(ex.getMessage());
                        System.out.println("Transaction is roller back...");
                        connect.rollback();

                    } catch (SQLException ex1) {
                        Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                    }
            }
        } finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }  
    }
    
    @FXML
    public void refreshMonthBarCharHandler() {
        
        try {
            String year = cb_ms_year.getValue();
            if(!year.equals(null) && !year.equals(""))
                monthBarChar(year);
        } catch (Exception e) { 
            System.out.println("Chyba "+e);
        }      
    }
    
    /**
     * This method set graf on second tab, income for each month
     * @param year year that you want show
     */
    public void monthBarChar(String year){                        
        try {
            connect.setAutoCommit(false);
            XYChart.Series<String, Integer> series = new XYChart.Series<>();  
            String query="SELECT rs.month, rs.total FROM (SELECT EXTRACT(year FROM sold_vinyls.date) "
                    + "AS year, MONTHNAME(STR_TO_DATE(EXTRACT(month FROM sold_vinyls.date), '%m')) "
                    + "AS month, SUM(sold_vinyls.num_of_sold) AS total FROM `sold_vinyls` GROUP BY year, "
                    + "month HAVING year LIKE '"+year+"') AS rs ORDER BY rs.month";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                series.getData().add(new XYChart.Data<>(
                        resultSet.getString("month"),
                        resultSet.getInt("total")
                ));
            } 
            resultSet.close();
            connect.commit();
            series.setName(year);
            barChartYears.getData().retainAll();
            barChartYears.getData().add(series);
        } catch (SQLException ex) {            
            if (connect != null) {
                    try {
                        System.out.println(ex.getMessage());
                        System.out.println("Transaction is roller back...");
                        connect.rollback();

                    } catch (SQLException ex1) {
                        Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                    }
            }
        } catch (Exception e) { 
            System.out.println("Chyba "+e);
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, e);    
        } finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
    }
        
    /**
     * This method set x axis on second tab graf (months names)
     */
    public void setAxis(){                        
        String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
        monthNames.addAll(Arrays.asList(months));
        xAxis.setCategories(monthNames);
    }
    
    //OTHER ____________________________________________________________________
    
    //button
    @FXML private Button other_refresh_btn;
    @FXML private Button other_refresh_bestsellers_btn;
    //combobox
    @FXML private ComboBox<String> cb_ms_month;
    //textfields
    @FXML private TextField tf_ms_year;
    //tables
    @FXML private TableView<TimeStats> monthsSoldStatsTable;      
    @FXML private TableColumn<TimeStats, String> column_sold_year;    
    @FXML private TableColumn<TimeStats, String> column_sold_month;
    @FXML private TableColumn<TimeStats, String> column_sold_amount;
    //charts
    @FXML private PieChart pieChartBestsellers;
    
    ObservableList<TimeStats> timeStats = FXCollections.observableArrayList(); 
    ObservableList<PieChart.Data> pieChartBestsellersData = FXCollections.observableArrayList();
    
    private void fillMonthComboBox() throws IOException{ 
        cb_ms_month.getItems().add("All");
        cb_ms_month.getItems().addAll(monthNames);
    }
    
    //hadnlers
    @FXML
    private void refreshMSSHandler(ActionEvent event){        
        try{  
            String tf_filin_year = tf_ms_year.getText();
            String tf_filin_month = cb_ms_month.getValue();
                              
            if(tf_filin_year.equals("")){ 
                tf_filin_year="%";
            }
            if(tf_filin_month.equals("All") || tf_filin_month.equals("") || tf_filin_month.equals("Month")){ 
                tf_filin_month="%";
            }
            refreshMonthsSoldStatsTable(tf_filin_year,tf_filin_month);   
        }   
        catch(Exception e){
            System.out.println("Chyba "+e);
        }   
    }
    
    /**
     * This method fill table by input 
     * @param year year for which we want record
     * @param month same as year
     */    
    private void refreshMonthsSoldStatsTable(String year, String month){
         try{     
            timeStats.clear();            
            connect.setAutoCommit(false);
            String query = "SELECT EXTRACT(YEAR FROM sold_vinyls.date) AS year, "
                    + "MONTHNAME( STR_TO_DATE( EXTRACT(MONTH FROM sold_vinyls.date),"
                    + " '%m' ) ) AS month, SUM(sold_vinyls.num_of_sold) AS total FROM "
                    + "`sold_vinyls` GROUP BY YEAR , MONTH HAVING YEAR LIKE ?"
                    + " AND month LIKE '"+month+"'";
            preparedStatement  =  connect.prepareStatement(query);
            preparedStatement.setString(1, "%" +year);
            resultSet=preparedStatement.executeQuery();
            
            while(resultSet.next()){
                timeStats.add(new TimeStats(
                        resultSet.getString("year"),
                        resultSet.getString("month"),
                        (resultSet.getString("total") + " ks")        
                ));
             }
            resultSet.close();
            connect.commit();
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
            System.out.println("Chyba "+e);
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
        column_sold_year.setCellValueFactory(new PropertyValueFactory<>("column_sold_year"));
        column_sold_month.setCellValueFactory(new PropertyValueFactory<>("column_sold_month"));
        column_sold_amount.setCellValueFactory(new PropertyValueFactory<>("column_sold_amount"));
        monthsSoldStatsTable.setItems(timeStats);      
     }  
       
    /**
     * This method fill bestseller piechart in others tab
     */
    @FXML
    private void bestsellerPieChart(){        
        try{
            connect.setAutoCommit(false);
            pieChartBestsellersData.clear();
                 String query="SELECT SUM(sv.num_of_sold) AS amount, artists.name AS artist FROM"
                         + " ( SELECT * FROM sold_vinyls ) AS sv JOIN vinyls ON sv.vinyl_id = vinyls.vinyls_id "
                         + "JOIN artists ON vinyls.artist_id = artists.artist_id GROUP BY artist LIMIT 10";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                pieChartBestsellersData.add(new PieChart.Data(
                        resultSet.getString("artist"),
                        resultSet.getDouble("amount")
                    ));
             }            
            resultSet.close();
            connect.commit();
            pieChartBestsellers.setData(pieChartBestsellersData); 
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
        finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}   
        
    @FXML
    private void refreshProfitPieChart(ActionEvent event) throws IOException {        
        bestsellerPieChart();
    }    
    
    //__________________________________________________________________________
        
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connector.connectToDbs(); //connect
        connect=connector.getConnect();        
        try {
            //icome by employee
            setIncomeByEmployeeData();
            String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
            monthBarChar(year);
            
            //income by years tab
            fillYearComboBox();
            setAxis();
            
            //other tab
            refreshMonthsSoldStatsTable("%","%");
            fillMonthComboBox();
            bestsellerPieChart(); 
            
        } catch (IOException ex) {
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
