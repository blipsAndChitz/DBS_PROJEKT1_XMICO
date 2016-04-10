package controller;


import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InsideController implements Initializable {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector();  
    
    @FXML
    private Button logOff_btn;
        
    @FXML
    void logOffHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.LOGIN);
    }  
       
    //EMPLOYEES TAB_____________________________________________________________
    
    //variables
    public static Employee UPDATED;    
    //buttons
    @FXML private Button employee_refresh_btn;
    @FXML private Button employee_clear_btn;
    @FXML private Button employee_insert_btn;
    @FXML private Button employee_delete_btn;
    @FXML private Button employee_update_btn;    
    //table
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> column_employee_id;    
    @FXML private TableColumn<Employee, String> column_employee_name;
    @FXML private TableColumn<Employee, String> column_employee_surname;
    @FXML private TableColumn<Employee, String> column_employee_post;
    @FXML private TableColumn<Employee, String> column_employee_board;
    @FXML private TableColumn<Employee, String> column_employee_city;        
    
    ObservableList<Employee> employees = FXCollections.observableArrayList(); 
   
    //textfields
    @FXML private TextField tf_employee_name;
    @FXML private TextField tf_employee_surname;
    @FXML private TextField tf_employee_board;
    @FXML private TextField tf_employee_city;
    @FXML private TextField tf_employee_post;
    
    //handlers and other methods
    
    /**
     * This method clears all fields
     */
     @FXML
     private void clearEmployeeFields(ActionEvent event) throws IOException{
         tf_employee_name.clear();
         tf_employee_surname.clear();
         tf_employee_board.clear();
         tf_employee_city.clear();
         tf_employee_post.clear();
     }
     
     /**
     * Button REFRESH handler 
     * This method loaads information from dbs to table in employee tab and refresh 
     */
    @FXML
    private void refreshEmployeeHandler(ActionEvent event) throws IOException {        
       
        String tf_filin_name = "%";
        String tf_filin_surname = "%";
        String tf_filin_post = "%";
        String tf_filin_city = "%";
        String tf_filin_board = "%";
    
        try{
        if(!(tf_employee_name.getText().equals(""))){ 
            tf_filin_name=tf_employee_name.getText();
        }
        else tf_filin_name="%";
        if(!(tf_employee_surname.getText().equals(""))){ 
            tf_filin_surname=tf_employee_surname.getText();
        }
        else tf_filin_surname="%";
        if(!(tf_employee_board.getText().equals(""))){ 
            tf_filin_board=tf_employee_board.getText();
        }
        else tf_filin_board="%";
        if(!(tf_employee_city.getText().equals(""))){ 
            tf_filin_city=tf_employee_city.getText();
        }
        else tf_filin_city="%";
        if(!(tf_employee_post.getText().equals(""))){ 
            tf_filin_post=tf_employee_post.getText();
        }
        else tf_filin_post="%";
        
        refreshEmployeeTable(tf_filin_name,tf_filin_surname,tf_filin_city,tf_filin_post,tf_filin_board);
        }
        catch(Exception ex){
            badInputError();
        }
    } 
    
    private void refreshEmployeeTable(String name, String surname, String city, String post, String board){        
        try{            
            employees.clear();
            connect.setAutoCommit(false);

            String query = "SELECT employee.employee_id, employee.name, employee.surname, position.post, "
                    + "employee.board, shops.city FROM employee JOIN POSITION ON employee.position_id = POSITION.position_id "
                    + "JOIN shops ON employee.shop_id = shops.shop_id WHERE shops.city LIKE ?"
                    + " AND employee.name LIKE ? AND employee.surname LIKE ?"
                    + " AND position.post LIKE ? AND employee.board LIKE ? "
                    + "ORDER BY employee.employee_id";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setString(1, "%" +city);
            preparedStatement.setString(2, "%" +name);
            preparedStatement.setString(3, "%" +surname);
            preparedStatement.setString(4, "%" +post);
            preparedStatement.setString(5, "%" +board);
            resultSet=preparedStatement.executeQuery();
            
            
            while(resultSet.next()){
                employees.add(new Employee(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("post"),
                        resultSet.getString("board"),
                        resultSet.getString("city")
                ));
             }
            resultSet.close();
            connect.commit();
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
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //System.out.println("Showing data..");
        column_employee_id.setCellValueFactory(new PropertyValueFactory<>("column_employee_id"));
        column_employee_name.setCellValueFactory(new PropertyValueFactory<>("column_employee_name"));
        column_employee_surname.setCellValueFactory(new PropertyValueFactory<>("column_employee_surname"));
        column_employee_post.setCellValueFactory(new PropertyValueFactory<>("column_employee_post"));
        column_employee_board.setCellValueFactory(new PropertyValueFactory<>("column_employee_board"));
        column_employee_city.setCellValueFactory(new PropertyValueFactory<>("column_employee_city"));
        employeeTable.setItems(employees);
    } 
    
    /**
     * This method opens insertion screen
     */
    @FXML
    void insertEmployeeHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.INSERT_EMPLOYEE);
    } 
    
    @FXML
    void deleteEmployeeHandler(ActionEvent event) throws SQLException {
        try{ 
            ObservableList<Employee> employeeToRemove = employeeTable.getSelectionModel().getSelectedItems();
            if(!(employeeToRemove.get(0).getColumn_employee_id()==null)){ 
                connect.setAutoCommit(false);
                String query = "DELETE FROM employee WHERE employee_id=?";
                preparedStatement  =  connect.prepareStatement(query);			
                preparedStatement.setInt(1, employeeToRemove.get(0).getColumn_employee_id());
                preparedStatement.executeUpdate();
                connect.commit();
                refreshEmployeeTable("%","%","%","%","%");
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
    private TextField updateNameTxtField;
   
    @FXML
    void showEmployeeProfileHandler(ActionEvent event) throws SQLException {
        try{ 
            ObservableList<Employee> profile = employeeTable.getSelectionModel().getSelectedItems();
            if(!(profile.get(0).getColumn_employee_id()==null)){ 
                connect.setAutoCommit(false);
                String query = "SELECT employee.name, employee.surname, "
                        + "POSITION.post, employee.board, shops.city, employee.age, "
                        + "employee.add_info FROM employee JOIN POSITION ON employee.position_id = "
                        + "POSITION.position_id JOIN shops ON employee.shop_id = shops.shop_id WHERE employee_id=?";
                preparedStatement  =  connect.prepareStatement(query);			
                preparedStatement.setInt(1, profile.get(0).getColumn_employee_id());
                resultSet=preparedStatement.executeQuery();
                
                Text em_name = new Text("");
                Text em_surname= new Text("");
                Text em_post= new Text("");
                Text em_joined= new Text("");
                Text em_city= new Text("");
                Text em_age= new Text("");
                Text em_add_info= new Text("");
                
                while(resultSet.next()){
                    em_name = new Text(resultSet.getString("name"));
                    em_surname = new Text(resultSet.getString("surname"));
                    em_post = new Text(resultSet.getString("post"));
                    em_joined = new Text(resultSet.getString("board"));
                    em_city = new Text(resultSet.getString("city"));
                    em_age = new Text(resultSet.getString("age"));
                    em_add_info = new Text(resultSet.getString("add_info"));
                }
                
                connect.commit();
                
                Text name = new Text("Name:");
                Text surname = new Text("Surname:");
                Text post = new Text("Post:");
                Text joined = new Text("Joined:");
                Text city = new Text("City:");
                Text age = new Text("Age:");
                Text add_info = new Text("Add. info.");

                
                Dialog<ButtonType> dialog = new Dialog<>();
                
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("/etc/icon.png").toString()));
                
                dialog.setTitle("Show employee profile");
                dialog.setHeaderText("Some other information about");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                              
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(50, 50, 10, 10));
                grid.add(name, 0, 0);
                grid.add(surname, 0, 1);
                grid.add(post, 0, 2);
                grid.add(joined, 0, 3);
                grid.add(city, 0, 4);
                grid.add(age, 0, 5);
                grid.add(add_info, 0, 6);           

                grid.add(em_name, 1, 0);
                grid.add(em_surname, 1, 1);
                grid.add(em_post, 1, 2);
                grid.add(em_joined, 1, 3);
                grid.add(em_city, 1, 4);
                grid.add(em_age, 1, 5);
                grid.add(em_add_info, 1, 6);             
                dialog.setResizable(true);
                dialog.getDialogPane().setPrefSize(480, 320);
                dialog.getDialogPane().setContent(grid);
                dialog.show();
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
    void updateEmployeeHandler(ActionEvent event) {
         try{
            ObservableList<Employee> employeeToRemove = employeeTable.getSelectionModel().getSelectedItems();
             if(!(employeeToRemove.get(0).getColumn_employee_id()==null)){
                UPDATED=employeeToRemove.get(0); 
                ScreenNavigator.loadScreen(ScreenNavigator.UPDATE_EMPLOYEE);
             }
        }
        catch(Exception e){
        }
    } 
  
    //OTHER TAB SHOPS___________________________________________________________   
    
    //buttons
    @FXML private Button other_refresh_shop_btn;
    @FXML private Button other_insert_shop_btn;
    @FXML private Button other_update_shop_btn;
    @FXML private Button other_remove_shop_btn;    
    @FXML private Button other_edit_shop_btn;
    @FXML private Button other_cancel_shop_btn;    
    //tables
    @FXML private TableView<Shop> shopsTable;
    @FXML private TableColumn<Shop, Integer> column_shop_id;    
    @FXML private TableColumn<Shop, String> column_shop_city;
    @FXML private TableColumn<Shop, String> column_shop_address;
    @FXML private TableColumn<Shop, Integer> column_shop_postcode;
    
    ObservableList<Shop> shops = FXCollections.observableArrayList(); 
    ObservableList<Shop> shopToEdit;
    
    //textfields
    @FXML private TextField tf_in_shop_city;
    @FXML private TextField tf_in_shop_address;
    @FXML private TextField tf_in_shop_postcode;
    @FXML private TextField tf_up_employee_city;
    @FXML private TextField tf_up_shop_address;
    @FXML private TextField tf_up_employee_post;
    
    
    //handlers    
    @FXML
    private void refreshShopHandler(ActionEvent event) throws IOException {        
       refreshShopTable();
    }
    
    @FXML
    private void refreshShopTable(){        
       
        try{            
            shops.clear();
            connect.setAutoCommit(false); 
            String query = "SELECT * FROM shops";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
        while(resultSet.next()){
            shops.add(new Shop(
                    resultSet.getInt("shop_id"),
                    resultSet.getString("city"),
                    resultSet.getString("address"),
                    resultSet.getInt("postcode")
            ));
         }
        resultSet.close();
        connect.commit();
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

        column_shop_id.setCellValueFactory(new PropertyValueFactory<>("column_shop_id"));
        column_shop_city.setCellValueFactory(new PropertyValueFactory<>("column_shop_city"));
        column_shop_address.setCellValueFactory(new PropertyValueFactory<>("column_shop_address"));
        column_shop_postcode.setCellValueFactory(new PropertyValueFactory<>("column_shop_postcode"));
        shopsTable.setItems(shops);
    } 
   
    @FXML
    private void insertShopHandler(ActionEvent event) {
        
        String city=tf_in_shop_city.getText();
        String address=tf_in_shop_address.getText();
        String postcode=tf_in_shop_postcode.getText();
        
        if(!city.equals("") && !address.equals("") && !postcode.equals("")){
            String query = "INSERT INTO shops (city, address, postcode) VALUES (?, ?, ?)";
            try {
                connect.setAutoCommit(false); 
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, city);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, postcode);
                preparedStatement.executeUpdate();
                connect.commit();

                tf_in_shop_city.clear();
                tf_in_shop_address.clear();
                tf_in_shop_postcode.clear();
                refreshShopTable();
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
                if(preparedStatement != null) try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
        }        
    } 
    
    @FXML
    private void editShopHandler(ActionEvent event) {
        
       try{
            shopToEdit = shopsTable.getSelectionModel().getSelectedItems();       
            if(shopToEdit.get(0).getColumn_shop_id()> -1){}
                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM shops WHERE shops.shop_id="+shopToEdit.get(0).getColumn_shop_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close();     
            
                other_update_shop_btn.setDisable(false);
                other_cancel_shop_btn.setDisable(false);
                tf_up_employee_city.setDisable(false);
                tf_up_employee_city.setText(shopToEdit.get(0).getColumn_shop_city());
                tf_up_shop_address.setDisable(false);
                tf_up_shop_address.setText(shopToEdit.get(0).getColumn_shop_address());
                tf_up_employee_post.setDisable(false);        
                tf_up_employee_post.setText(String.valueOf(shopToEdit.get(0).getColumn_shop_postcode()));
                other_edit_shop_btn.setDisable(true);
                shopsTable.setDisable(true);            
       }
       catch(Exception e){}
        
    }  
    
    @FXML
    private void cancelShopHandler(ActionEvent event) {        
        try {
            setToDefault();
            connect.commit();
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
    }  
    
    @FXML
    private void updateShopHandler(ActionEvent event) {
        try {
            String city=tf_up_employee_city.getText();
            String address=tf_up_shop_address.getText();
            String postcode=tf_up_employee_post.getText();           
                       
            connect.setAutoCommit(false);
            String query = "UPDATE shops SET city=?, address=?, postcode=? WHERE shop_id=?";
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, postcode);
            preparedStatement.setInt(4, shopToEdit.get(0).getColumn_shop_id());
            preparedStatement.executeUpdate();
            connect.commit(); 
            setToDefault();
            refreshShopTable();
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
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
    private void removeShopHandler(ActionEvent event) {
        try{ 
            ObservableList<Shop> shopToRemove = shopsTable.getSelectionModel().getSelectedItems();                    			
            connect.setAutoCommit(false); 
            String query = "DELETE FROM shops WHERE shop_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, shopToRemove.get(0).getColumn_shop_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshShopTable();
        } 
        catch (SQLException ex) {            
            if (connect != null) {
                    try {
                        System.out.println(ex.getMessage());
                        System.out.println("Transaction is roller back...");
                        connect.rollback();                        
                        removeError();
                    } catch (SQLException ex1) {
                        Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                    }
            }
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } 
    }
    
    private void setToDefault(){
        other_update_shop_btn.setDisable(true);
        other_cancel_shop_btn.setDisable(true);
        other_edit_shop_btn.setDisable(false);
        tf_up_employee_city.setDisable(true);
        tf_up_employee_city.clear();
        tf_up_shop_address.setDisable(true);
        tf_up_shop_address.clear();
        tf_up_employee_post.setDisable(true);
        tf_up_employee_post.clear();
        shopsTable.setDisable(false);
    }
    
    
    //OTHER TAB POSITIONS_______________________________________________________
    
    //buttons
    @FXML private Button other_refresh_post_btn;
    @FXML private Button other_insert_post_btn;
    @FXML private Button other_update_post_btn;
    @FXML private Button other_remove_post_btn;     
    @FXML private Button other_edit_post_btn;
    @FXML private Button other_cancel_post_btn;    
    //tables
    @FXML private TableView<Post> postsTable;       
    @FXML private TableColumn<Post, Integer> column_post_id;    
    @FXML private TableColumn<Post, String> column_post_name;
    
    ObservableList<Post> posts = FXCollections.observableArrayList(); 
    ObservableList<Post> postToEdit;
       
    //textfields
    @FXML private TextField tf_in_post;
    @FXML private TextField tf_up_post;
    
    //hadnlers
    @FXML
    private void refreshPostHandler(ActionEvent event){        
        refreshPostTable();
    } 
    
    private void refreshPostTable(){       
        try{            
            posts.clear();
            connect.setAutoCommit(false); 
            String query = "SELECT * FROM position";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
        while(resultSet.next()){
            posts.add(new Post(
                    resultSet.getInt("position_id"),
                    resultSet.getString("post")
            ));
         }
        resultSet.close();
        connect.commit();
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
        } finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } 

        column_post_id.setCellValueFactory(new PropertyValueFactory<>("column_post_id"));
        column_post_name.setCellValueFactory(new PropertyValueFactory<>("column_post_name"));
        postsTable.setItems(posts);
    } 
    
    @FXML
    private void insertPostHandler(ActionEvent event) {
        
        String post=tf_in_post.getText();
        
        if(!post.equals("")){
            String query = "INSERT INTO position (post) VALUES (?)";
            try {
                connect.setAutoCommit(false); 
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, post);
                preparedStatement.executeUpdate();
                connect.commit();                
                refreshPostTable();
                tf_in_post.clear();
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
            catch (Exception e) {
                Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
            }
            finally {
                if(preparedStatement != null) try {
                    preparedStatement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    } 
    
    @FXML
    private void cancelPostHandler(ActionEvent event) {
        try {
            setToDefaultPost();
            connect.commit();
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
    } 
    
    private void setToDefaultPost(){
        other_update_post_btn.setDisable(true);
        other_cancel_post_btn.setDisable(true);
        other_edit_post_btn.setDisable(false);
        tf_up_post.setDisable(true);
        tf_up_post.clear();
        postsTable.setDisable(false);
    }
    
    @FXML
    private void editPostHandler(ActionEvent event){
       
        try{
            postToEdit = postsTable.getSelectionModel().getSelectedItems();
            if(postToEdit.get(0).getColumn_post_id()> -1){
                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM position WHERE position.position_id="+postToEdit.get(0).getColumn_post_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close(); 
                
                other_update_post_btn.setDisable(false);
                other_cancel_post_btn.setDisable(false);
                other_edit_post_btn.setDisable(true);
                tf_up_post.setDisable(false);
                tf_up_post.setText(postToEdit.get(0).getColumn_post_name());
                postsTable.setDisable(true);
            }
        }
        catch(Exception e){}
    }
    
    @FXML
    private void updatePostHandler(ActionEvent event) {
        try {
            String post=tf_up_post.getText();
            connect.setAutoCommit(false);            
            String query = "UPDATE position SET post=? WHERE position_id=?";
            preparedStatement = connect.prepareStatement(query);
            preparedStatement.setString(1, post);
            preparedStatement.setInt(2, postToEdit.get(0).getColumn_post_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshPostTable();
            setToDefaultPost();
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
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
    private void removePostHandler(ActionEvent event) {
        try{
            connect.setAutoCommit(false);
            ObservableList<Post> postToRemove = postsTable.getSelectionModel().getSelectedItems();                    			
            String query = "DELETE FROM position WHERE position_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, postToRemove.get(0).getColumn_post_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshPostTable();
        }
        catch (SQLException ex) {
            if (connect != null) {
                try {
                    System.out.println(ex.getMessage());
                    System.out.println("Transaction is roller back...");
                    connect.rollback();
                    removeError();
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
    
    
    //VINYLS TAB POSITIONS______________________________________________________
        
    //buttons
    @FXML private Button vinyls_refresh_btn;
    @FXML private Button vinyls_insert_btn;
    @FXML private Button vinyls_update_btn;
    @FXML private Button vinyls_remove_btn;     
    @FXML private Button vinyls_edit_btn;
    @FXML private Button vinyls_cancel_btn;
    @FXML private Button vinyls_more_btn;
    @FXML private Button vinyls_new_btn;
    @FXML private Button vinyls_cancelInsert_btn;    
     //tables
    @FXML private TableView<Vinyls> vinylsTable;       
    @FXML private TableColumn<Vinyls, Integer> column_vinyl_id;    
    @FXML private TableColumn<Vinyls, String> column_vinyl_name;
    @FXML private TableColumn<Vinyls, String> column_vinyl_artist;
    @FXML private TableColumn<Vinyls, String> column_vinyl_genre;
    @FXML private TableColumn<Vinyls, Integer> column_vinyl_year;
    @FXML private TableColumn<Vinyls, String> column_vinyl_label;
    @FXML private TableColumn<Vinyls, String> column_vinyl_length;
    @FXML private TableColumn<Vinyls, Double> column_vinyl_price;
    
    ObservableList<Vinyls> vinyls = FXCollections.observableArrayList(); 
    ObservableList<Vinyls> vinylToEdit;
    ObservableList<Vinyls> vinylToRemove;
    
    //comboboxes
    @FXML private ComboBox<String> cb_vinyls_artist;
    @FXML private ComboBox<String> cb_vinyls_label;    
    @FXML private ComboBox<String> cb_vinyls_genre;
    @FXML private ComboBox<String> cb_vinyls_up_artist;
    @FXML private ComboBox<String> cb_vinyls_up_label;    
    @FXML private ComboBox<String> cb_vinyls_up_genre; 
    
     ObservableList<String> vinyls_artist = FXCollections.observableArrayList();
     ObservableList<String> vinyls_label = FXCollections.observableArrayList();
     ObservableList<String> vinyls_genre = FXCollections.observableArrayList();
    
    private void fillArtistComboBox(ComboBox cb, ComboBox cb2) throws IOException{              
        try{
            vinyls_artist.clear();
            connect.setAutoCommit(false);
            cb.getSelectionModel().clearSelection();
            cb.getItems().clear();
            cb2.getSelectionModel().clearSelection();
            cb2.getItems().clear();
            String query = "SELECT artists.name FROM artists ORDER BY name ASC";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                vinyls_label.add(resultSet.getString("name"));
            }
            resultSet.close();  
            connect.commit();
            cb.getItems().addAll(vinyls_label);
            cb2.getItems().addAll(vinyls_label);
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
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
    }
    
    private void fillLabelComboBox(ComboBox cb, ComboBox cb2) throws IOException{              
        try{
            vinyls_label.clear();
            connect.setAutoCommit(false);
            cb.getSelectionModel().clearSelection();
            cb.getItems().clear();
            cb2.getSelectionModel().clearSelection();
            cb2.getItems().clear();
            String query = "SELECT label.name FROM label ORDER BY name ASC";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                vinyls_artist.add(resultSet.getString("name"));
            }
            resultSet.close();
            connect.commit();            
            cb.getItems().addAll(vinyls_artist);
            cb2.getItems().addAll(vinyls_artist);
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
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }     
    }
    
    private void fillGenreComboBox(ComboBox cb, ComboBox cb2) throws IOException{              
        try{
            vinyls_genre.clear();
            connect.setAutoCommit(false);
            cb.getSelectionModel().clearSelection();
            cb.getItems().clear();
            cb2.getSelectionModel().clearSelection();
            cb2.getItems().clear();
            String query = "SELECT genre.name FROM genre ORDER BY name ASC";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                vinyls_genre.add(resultSet.getString("name"));
            }
            resultSet.close();
            connect.commit();
            cb.getItems().addAll(vinyls_genre);
            cb2.getItems().addAll(vinyls_genre);
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
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    //textfields
    @FXML private TextField tf_in_vinyl_album;
    @FXML private TextField tf_in_vinyl_length;
    @FXML private TextField tf_in_vinyl_year;
    @FXML private TextField tf_in_vinyl_price;
    @FXML private TextField tf_up_vinyl_album;
    @FXML private TextField tf_up_vinyl_length;
    @FXML private TextField tf_up_vinyl_year;
    @FXML private TextField tf_up_vinyl_price;
    
    //hadnlers
   
    @FXML
    private void refreshVinylHandler(ActionEvent event) throws IOException {
        refreshVinylTable();
    }  
    
    private void refreshVinylTable() throws IOException{        
       
        try{            
            vinyls.clear();
            connect.setAutoCommit(false);     
            String query = "SELECT vinyls.vinyls_id, artists.name AS artists, vinyls.name AS album,"
                    + " vinyls.year, vinyls.length, vinyls.price, genre.name AS genre, label.name AS label "
                    + "FROM vinyls JOIN artists ON vinyls.artist_id = artists.artist_id "
                    + "JOIN genre ON vinyls.genre_id = genre.genre_id JOIN label ON "
                    + "vinyls.label_id = label.label_id ORDER BY vinyls_id ASC";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
        while(resultSet.next()){
            vinyls.add(new Vinyls(
                    resultSet.getInt("vinyls_id"),
                    resultSet.getString("album"),
                    resultSet.getString("artists"),
                    resultSet.getString("genre"),
                    resultSet.getInt("year"),
                    resultSet.getString("label"),
                    resultSet.getString("length"),
                    resultSet.getDouble("price")
            ));
         }
        resultSet.close();
        connect.commit();
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        column_vinyl_id.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_id"));
        column_vinyl_name.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_name"));
        column_vinyl_artist.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_artist"));
        column_vinyl_genre.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_genre"));
        column_vinyl_year.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_year"));
        column_vinyl_label.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_label"));
        column_vinyl_length.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_length"));
        column_vinyl_price.setCellValueFactory(new PropertyValueFactory<>("column_vinyl_price"));    
        vinylsTable.setItems(vinyls);
        
        fillArtistComboBox(cb_vinyls_artist,cb_vinyls_up_artist);
        fillLabelComboBox(cb_vinyls_label,cb_vinyls_up_label);
        fillGenreComboBox(cb_vinyls_genre,cb_vinyls_up_genre);
    }
    
    @FXML
    private void newVinylHandler(ActionEvent event){        
        try {
            connect.setAutoCommit(false);  
            String query = "SELECT artists.name FROM artists FOR UPDATE";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);               

            connect.setAutoCommit(false);  
            query = "SELECT genre.name FROM genre FOR UPDATE";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);              

            connect.setAutoCommit(false);  
            query = "SELECT label.name FROM label FOR UPDATE";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            
            tf_in_vinyl_album.setDisable(false);
            tf_in_vinyl_length.setDisable(false);
            tf_in_vinyl_year.setDisable(false);
            tf_in_vinyl_price.setDisable(false);
            cb_vinyls_artist.setDisable(false);
            cb_vinyls_label.setDisable(false);
            cb_vinyls_genre.setDisable(false);
            vinyls_insert_btn.setDisable(false);
            vinyls_cancelInsert_btn.setDisable(false);
            vinyls_new_btn.setDisable(true);
        } catch (SQLException ex) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void cancelInsertVinylHandler(ActionEvent event){
        try {
            setInsertToDefault();
            connect.commit();
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
    }
    
    private void setInsertToDefault(){
        tf_in_vinyl_album.setDisable(true);
        tf_in_vinyl_length.setDisable(true);
        tf_in_vinyl_year.setDisable(true);
        tf_in_vinyl_price.setDisable(true);
        cb_vinyls_artist.setDisable(true);
        cb_vinyls_label.setDisable(true);
        cb_vinyls_genre.setDisable(true);
        vinyls_insert_btn.setDisable(true);
        vinyls_cancelInsert_btn.setDisable(true);
        vinyls_new_btn.setDisable(false);
    }
    
    @FXML
    private void insertVinylHandler(ActionEvent event) {
        try {
            String album=tf_in_vinyl_album.getText();
            String length=tf_in_vinyl_length.getText();
            int year=Integer.parseInt(tf_in_vinyl_year.getText());
            Double price = Double.parseDouble(tf_in_vinyl_price.getText().replace(",","."));
            String artist = cb_vinyls_artist.getValue();
            String label = cb_vinyls_label.getValue();
            String genre = cb_vinyls_genre.getValue();            

            int artist_FK=0;
            int label_FK=0;
            int genre_FK=0;        
        
            if(year<9999 && !album.equals("") && !length.equals("") && price>0
                && !(artist.equals("artist") || artist.equals("umelec")) && 
                !(label.equals("label") || label.equals("vydavateľstvo")) &&
                !(genre.equals("genre") || genre.equals("žáner"))){
               
                connect.setAutoCommit(false);
               
                String query = "SELECT artists.artist_id FROM artists WHERE artists.name=?";
                preparedStatement  =  connect.prepareStatement(query);			
                preparedStatement.setString(1,artist);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   artist_FK=resultSet.getInt("artist_id");
                }           

                query = "SELECT label.label_id FROM label WHERE label.name=?";
                preparedStatement  =  connect.prepareStatement(query);	
                preparedStatement.setString(1,label);
                resultSet=preparedStatement.executeQuery();;
                while(resultSet.next()){
                   label_FK=resultSet.getInt("label_id");
                }

                query = "SELECT genre.genre_id FROM genre WHERE genre.name=?";
                preparedStatement  =  connect.prepareStatement(query);	
                preparedStatement.setString(1,genre);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   genre_FK=resultSet.getInt("genre_id");                
                }

                System.out.println(artist_FK+" "+label_FK+" "+genre_FK);   
                query = "INSERT INTO vinyls (artist_id, genre_id, name, year, label_id, length, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1, artist_FK);
                preparedStatement.setInt(2, genre_FK);
                preparedStatement.setString(3, album);
                preparedStatement.setInt(4, year);
                preparedStatement.setInt(5, label_FK);
                preparedStatement.setString(6, length);
                preparedStatement.setDouble(7, price);
                preparedStatement.executeUpdate();
                connect.commit();
                
                setInsertToDefault();

                tf_in_vinyl_album.clear();
                tf_in_vinyl_length.clear();
                tf_in_vinyl_year.clear();
                tf_in_vinyl_price.clear();
                cb_vinyls_artist.setValue("");
                cb_vinyls_label.setValue("");
                cb_vinyls_genre.setValue("");
                refreshVinylTable();
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
              System.out.println("Chyba "+ex);
              badInputError();
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
    private void removeVinylHandler(ActionEvent event) {        
        try{ 
            connect.setAutoCommit(false); 
            vinylToRemove = vinylsTable.getSelectionModel().getSelectedItems();                    			
            String query = "DELETE FROM vinyls WHERE vinyls_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, vinylToRemove.get(0).getColumn_vinyl_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshVinylTable();
        } 
        catch (SQLException ex) {
            if (connect != null) {
                try {
                    System.out.println(ex.getMessage());
                    System.out.println("Transaction is roller back...");
                    connect.rollback();
                    removeError();
                } catch (SQLException ex1) {
                    Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
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
    private void editVinylHandler(ActionEvent event){
       
        try{
            vinylToEdit = vinylsTable.getSelectionModel().getSelectedItems();
            
            if(vinylToEdit.get(0).getColumn_vinyl_id()> -1){            
                                
                connect.setAutoCommit(false);  
                String query = "SELECT artists.name FROM artists FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);               
                                
                connect.setAutoCommit(false);  
                query = "SELECT genre.name FROM genre FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);              
                                
                connect.setAutoCommit(false);  
                query = "SELECT label.name FROM label FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);               
                
                vinyls_cancel_btn.setDisable(false);
                vinyls_update_btn.setDisable(false);
                vinyls_edit_btn.setDisable(true);
                tf_up_vinyl_album.setDisable(false);
                tf_up_vinyl_album.setText(vinylToEdit.get(0).getColumn_vinyl_name());
                tf_up_vinyl_length.setDisable(false);
                tf_up_vinyl_length.setText(vinylToEdit.get(0).getColumn_vinyl_length());
                tf_up_vinyl_year.setDisable(false);
                tf_up_vinyl_year.setText(vinylToEdit.get(0).getColumn_vinyl_year().toString());
                tf_up_vinyl_price.setDisable(false);
                tf_up_vinyl_price.setText(vinylToEdit.get(0).getColumn_vinyl_price().toString());
                cb_vinyls_up_artist.setDisable(false);
                cb_vinyls_up_artist.setValue(vinylToEdit.get(0).getColumn_vinyl_artist());
                cb_vinyls_up_label.setDisable(false);
                cb_vinyls_up_label.setValue(vinylToEdit.get(0).getColumn_vinyl_label());
                cb_vinyls_up_genre.setDisable(false);
                cb_vinyls_up_genre.setValue(vinylToEdit.get(0).getColumn_vinyl_genre());
                vinylsTable.setDisable(true);
                
            }
        }
        catch(Exception e){}
    }
    
    @FXML
    private void cancelVinylHandler(ActionEvent event){
        try {
            setToDefaultVinyl();
            connect.commit();
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
    }
    
    @FXML
    private void moreVinylHandler(ActionEvent event){
        ScreenNavigator.loadScreen(ScreenNavigator.VINYLS_MORE);
    }
        
    @FXML
    private void updateVinylHandler(ActionEvent event) throws IOException{
       
        try {
            String album=tf_up_vinyl_album.getText();
            String length=tf_up_vinyl_length.getText();
            int year=Integer.parseInt(tf_up_vinyl_year.getText());
            Double price = Double.parseDouble(tf_up_vinyl_price.getText().replace(",","."));
            String artist = cb_vinyls_up_artist.getValue();
            String label = cb_vinyls_up_label.getValue();
            String genre = cb_vinyls_up_genre.getValue();            

            int artist_FK=0;
            int label_FK=0;
            int genre_FK=0;        
        
            if(year<9999 && !album.equals("") && !length.equals("") && price>0
                && !(artist.equals("artist") || artist.equals("umelec")) && 
                !(label.equals("label") || label.equals("vydavateľstvo")) &&
                !(genre.equals("genre") || genre.equals("žáner"))){
            
                connect.setAutoCommit(false);                  
                
                String query = "SELECT artists.artist_id FROM artists WHERE artists.name=?";
                preparedStatement  =  connect.prepareStatement(query);			
                preparedStatement.setString(1,artist);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   artist_FK=resultSet.getInt("artist_id");
                }           

                query = "SELECT label.label_id FROM label WHERE label.name=?";
                preparedStatement  =  connect.prepareStatement(query);	
                preparedStatement.setString(1,label);
                resultSet=preparedStatement.executeQuery();;
                while(resultSet.next()){
                   label_FK=resultSet.getInt("label_id");
                }

                query = "SELECT genre.genre_id FROM genre WHERE genre.name=?";
                preparedStatement  =  connect.prepareStatement(query);	
                preparedStatement.setString(1,genre);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   genre_FK=resultSet.getInt("genre_id");                
                }
                        
                query = "UPDATE vinyls SET artist_id=?, genre_id=?, name=?, year=?, label_id=?, length=?, price=? WHERE vinyls_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1, artist_FK);
                preparedStatement.setInt(2, genre_FK);
                preparedStatement.setString(3, album);
                preparedStatement.setInt(4, year);
                preparedStatement.setInt(5, label_FK);
                preparedStatement.setString(6, length);
                preparedStatement.setDouble(7, price);
                preparedStatement.setInt(8, vinylToEdit.get(0).getColumn_vinyl_id());
                preparedStatement.executeUpdate();
                connect.commit();
                refreshVinylTable();       
                setToDefaultPost();
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
              System.out.println("Chyba "+ex);
              badInputError();
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }   
        } 
      
        setToDefaultVinyl();
    }
    
    private void setToDefaultVinyl(){       
        vinyls_cancel_btn.setDisable(true);
        vinyls_update_btn.setDisable(true);
        vinyls_edit_btn.setDisable(false);
        tf_up_vinyl_album.setDisable(true);
        tf_up_vinyl_album.clear();
        tf_up_vinyl_length.setDisable(true);
        tf_up_vinyl_length.clear();
        tf_up_vinyl_year.setDisable(true);
        tf_up_vinyl_year.clear();
        tf_up_vinyl_price.setDisable(true);
        tf_up_vinyl_price.clear();
        cb_vinyls_up_artist.setDisable(true);
        cb_vinyls_up_artist.setValue("Artist");
        cb_vinyls_up_label.setDisable(true);
        cb_vinyls_up_label.setValue("Label");
        cb_vinyls_up_genre.setDisable(true);
        cb_vinyls_up_genre.setValue("Genre");
        vinylsTable.setDisable(false);
    }
    
    //CATALOGUE ________________________________________________________________
    
    //buttons
    @FXML private Button catalogue_refresh_btn;
    @FXML private Button catalogue_clear_btn;    
    @FXML private Button catalogue_sell_btn;    
    @FXML private Button catalogue_confirm_btn;
    @FXML private Button catalogue_cancel_sell_btn;    
    @FXML private Button catalogue_update_btn;  
    @FXML private Button catalogue_cancel_update_btn;
    @FXML private Button catalogue_edit_btn;    
    @FXML private Button catalogue_new_btn;  
    @FXML private Button catalogue_cancel_insert_btn;
    @FXML private Button catalogue_insert_btn;    
    //tables
    @FXML private TableView<Vinyls_shops> catalogueTable;       
    @FXML private TableColumn<Vinyls_shops, Integer> column_catalogue_id;    
    @FXML private TableColumn<Vinyls_shops, String> column_catalogue_name;
    @FXML private TableColumn<Vinyls_shops, String> column_catalogue_artist;
    @FXML private TableColumn<Vinyls_shops, String> column_catalogue_genre;
    @FXML private TableColumn<Vinyls_shops, Integer> column_catalogue_year;
    @FXML private TableColumn<Vinyls_shops, String> column_catalogue_amount;
    @FXML private TableColumn<Vinyls_shops, String> column_catalogue_shop;
    @FXML private TableColumn<Vinyls_shops, Double> column_catalogue_price;
    
    ObservableList<Vinyls_shops> catalogue = FXCollections.observableArrayList(); 
    ObservableList<Vinyls_shops> catalogueItemToEdit;
    ObservableList<Vinyls_shops> catalogueItemToSell;
    ObservableList<String> catalogue_shops = FXCollections.observableArrayList();
    ObservableList<String> catalogue_vinyls = FXCollections.observableArrayList(); 
    
    @FXML private TextField tf_fl_catalogue_name;
    @FXML private TextField tf_fl_catalogue_artist;
    @FXML private TextField tf_fl_catalogue_genre;
    @FXML private TextField tf_fl_catalogue_year;
    @FXML private TextField tf_fl_catalogue_amount;
    @FXML private TextField tf_fl_catalogue_shop;
    @FXML private TextField tf_fl_catalogue_price;
    @FXML private TextField tf_sell_catalogue_info;
    @FXML private TextField tf_sell_catalogue_amount;
    @FXML private TextField tf_up_catalogue_amount;
    @FXML private TextField tf_in_catalogue_amount;
    
    @FXML private ComboBox<String> cb_catalogue_shops;
    @FXML private ComboBox<String> cb_catalogue_vinyls; 
    
    //hadnlers
    @FXML
    private void refreshCatalogueHandler(ActionEvent event){            
        try{
            String tf_filin_name = "%";
            String tf_filin_artist = "%";
            String tf_filin_genre = "%";
            String tf_filin_year = "%";
            int tf_filin_amount = 10000;
            String tf_filin_shop = "%";
            Double tf_filin_price = 10000.0;           
            
            if(!(tf_fl_catalogue_name.getText().equals(""))){ 
                tf_filin_name=tf_fl_catalogue_name.getText();
            }
            else tf_filin_name="%";
            if(!(tf_fl_catalogue_artist.getText().equals(""))){ 
                tf_filin_artist=tf_fl_catalogue_artist.getText();
            }
            else tf_filin_artist="%";
            if(!(tf_fl_catalogue_genre.getText().equals(""))){ 
                tf_filin_genre=tf_fl_catalogue_genre.getText();
            }
            else tf_filin_genre="%";
            if(!(tf_fl_catalogue_year.getText().equals(""))){ 
                tf_filin_year=tf_fl_catalogue_year.getText();
            }
            else tf_filin_year="%";
            if(!(tf_fl_catalogue_amount.getText().equals(""))){ 
                tf_filin_amount=Integer.parseInt(tf_fl_catalogue_amount.getText());
            }
            else tf_filin_amount=10000;
            if(!(tf_fl_catalogue_shop.getText().equals(""))){ 
                tf_filin_shop=tf_fl_catalogue_shop.getText();
            }
            else tf_filin_shop="%";
            if(!(tf_fl_catalogue_price.getText().equals(""))) {
                tf_filin_price=Double.parseDouble(tf_fl_catalogue_price.getText());
            } else tf_filin_price=10000.0;
        
            refreshCatalogueTable(tf_filin_name, tf_filin_artist, tf_filin_genre, tf_filin_shop, 
                tf_filin_year, tf_filin_amount, tf_filin_price);   
        }catch (Exception e) {
            badInputError();
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
        }          
    }    
    
    private void refreshCatalogueTable(String name, String artist, String genre, String shop, String year, Integer amount, Double price){
         try{           
            catalogue.clear();
            connect.setAutoCommit(false);
            String query = "SELECT sv.shops_vinyls_id AS id, artists.name AS artist, vinyls.name,"
                    + " genre.name AS genre, vinyls.year, vinyls.vinyls_id, sv.num_of_vinyls AS amount, shops.city,"
                    + " vinyls.price FROM ( SELECT * FROM shops_vinyls ) AS sv JOIN vinyls ON sv.vinyl_id"
                    + " = vinyls.vinyls_id JOIN shops ON sv.shop_id = shops.shop_id JOIN artists ON "
                    + "vinyls.artist_id = artists.artist_id JOIN genre ON vinyls.genre_id = genre.genre_id "
                    + "WHERE artists.name LIKE ? AND vinyls.name LIKE ?"
                    + " AND genre.name LIKE ? AND vinyls.year LIKE ? AND"
                    + " sv.num_of_vinyls<=? AND shops.city LIKE ? AND "
                    + "vinyls.price<? ORDER BY sv.shops_vinyls_id";
            preparedStatement  =  connect.prepareStatement(query);	
            preparedStatement.setString(1, "%" +artist);
            preparedStatement.setString(2, "%" +name);
            preparedStatement.setString(3, "%" +genre);
            preparedStatement.setString(4, "%" +year);
            preparedStatement.setInt(5,amount);
            preparedStatement.setString(6, "%" +shop);
            preparedStatement.setDouble(7, price);
            resultSet=preparedStatement.executeQuery();
  
        while(resultSet.next()){
            catalogue.add(new Vinyls_shops(
                    resultSet.getInt("id"),
                    resultSet.getString("artist"),
                    resultSet.getString("name"),
                    resultSet.getString("genre"),
                    resultSet.getInt("year"),
                    resultSet.getInt("amount"),
                    resultSet.getString("city"),
                    resultSet.getDouble("price"),
                    resultSet.getInt("vinyls_id")
            ));
         }
        resultSet.close();
        connect.commit();
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

        column_catalogue_id.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_id"));
        column_catalogue_name.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_name"));
        column_catalogue_artist.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_artist"));
        column_catalogue_genre.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_genre"));
        column_catalogue_year.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_year"));
        column_catalogue_amount.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_amount"));
        column_catalogue_shop.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_shop"));
        column_catalogue_price.setCellValueFactory(new PropertyValueFactory<>("column_catalogue_price"));    
        catalogueTable.setItems(catalogue); 
    }
    
    @FXML
    private void clearCatalogueHandler(ActionEvent event){
        clearFilter();
    }
    
    @FXML
    private void sellCatalogueHandler(ActionEvent event){
        try{
            catalogueItemToSell = catalogueTable.getSelectionModel().getSelectedItems();
            //System.out.println(catalogueItemToSell.get(0).getColumn_catalogue_amount());
            if(catalogueItemToSell.get(0).getColumn_catalogue_id()> -1 && 
                catalogueItemToSell.get(0).getColumn_catalogue_shop().equals(LoginController.LOGGED_SHOP_NAME)){                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM shops_vinyls WHERE shops_vinyls.shops_vinyls_id="+catalogueItemToSell.get(0).getColumn_catalogue_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close(); 
                
                tf_sell_catalogue_info.setDisable(false);
                tf_sell_catalogue_info.setText(catalogueItemToSell.get(0).getColumn_catalogue_name()+ 
                                    " - " + catalogueItemToSell.get(0).getColumn_catalogue_artist());
                tf_sell_catalogue_amount.setDisable(false);
                catalogue_sell_btn.setDisable(true);
                catalogue_confirm_btn.setDisable(false);
                catalogue_cancel_sell_btn.setDisable(false);
                catalogueTable.setDisable(true);
            }
            else{               
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Are you sure?");
                alert.setHeaderText("You try to sell vinyl that is not in your home shop.");
                alert.setContentText("Are you ok with this?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){       
                    connect.setAutoCommit(false);
                    String query = "SELECT * FROM shops_vinyls WHERE shops_vinyls.shops_vinyls_id="+catalogueItemToSell.get(0).getColumn_catalogue_id()+" FOR UPDATE";
                    statement = connect.createStatement();
                    resultSet = statement.executeQuery(query);
                    resultSet.close(); 

                    tf_sell_catalogue_info.setDisable(false);
                    tf_sell_catalogue_info.setText(catalogueItemToSell.get(0).getColumn_catalogue_name()+ 
                                        " - " + catalogueItemToSell.get(0).getColumn_catalogue_artist());
                    tf_sell_catalogue_amount.setDisable(false);
                    catalogue_sell_btn.setDisable(true);
                    catalogue_confirm_btn.setDisable(false);
                    catalogue_cancel_sell_btn.setDisable(false);
                    catalogueTable.setDisable(true);
                } else {
                    setToDefaultCatalogue();
                    connect.commit();
                }
                
            }
        
            
            
        }
        catch(Exception e){}
    }
    
    @FXML
    private void cancelSellCatalogueHandler(ActionEvent event){
        try {
            setToDefaultCatalogue();
            connect.commit();
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
    }
    
    @FXML
    private void confirmSellCatalogueHandler(ActionEvent event){
        int pieces = Integer.parseInt(tf_sell_catalogue_amount.getText());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        //System.out.println("V confirme "+catalogueItemToSell.get(0).getColumn_catalogue_amount());
        try { 
            if(pieces<=catalogueItemToSell.get(0).getColumn_catalogue_amount()){               
                connect.setAutoCommit(false);  
                String query = "INSERT INTO sold_vinyls (vinyl_id, employee_id, shop_id, num_of_sold, date) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1, catalogueItemToSell.get(0).getColumn_catalogue_vinyl_id());
                preparedStatement.setInt(2, LoginController.LOGGED_EMP_ID);
                preparedStatement.setInt(3, LoginController.LOGGED_SHOP_ID);
                preparedStatement.setInt(4, pieces);
                preparedStatement.setString(5, dateFormat.format(cal.getTime()));
                preparedStatement.executeUpdate();
                preparedStatement.close();
                
                query = "UPDATE shops_vinyls SET num_of_vinyls=? WHERE shops_vinyls_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1, (catalogueItemToSell.get(0).getColumn_catalogue_amount()-pieces));
                preparedStatement.setInt(2, catalogueItemToSell.get(0).getColumn_catalogue_id());
                preparedStatement.executeUpdate();
                connect.commit();              
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Ooops, there was an error!");
            alert.setContentText("You dont have enough copies!!");
            alert.showAndWait();    
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        setToDefaultCatalogue();
    }
    
    @FXML
    private void updateCatalogueHandler(ActionEvent event){
        try {
            int pieces=Integer.parseInt(tf_up_catalogue_amount.getText());       
        
            if(pieces>-1){
                connect.setAutoCommit(false);  
                String query = "UPDATE shops_vinyls SET num_of_vinyls=? WHERE shops_vinyls_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setInt(1, pieces);
                preparedStatement.setInt(2, catalogueItemToEdit.get(0).getColumn_catalogue_id());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connect.commit();
                setToDefaultCatalogueUpdate();
                refreshCatalogueTable("%","%","%","%","%",10000,10000.0);
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
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
    private void editCatalogueHandler(ActionEvent event){
        try{
            catalogueItemToEdit = catalogueTable.getSelectionModel().getSelectedItems();
            if(catalogueItemToEdit.get(0).getColumn_catalogue_id()> -1){
               
                connect.setAutoCommit(false);
                String query = "SELECT * FROM shops_vinyls WHERE shops_vinyls.shops_vinyls_id="+catalogueItemToEdit.get(0).getColumn_catalogue_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close();  
               
                catalogue_cancel_update_btn.setDisable(false);
                catalogue_update_btn.setDisable(false);
                catalogue_edit_btn.setDisable(true);
                catalogue_sell_btn.setDisable(true);
                tf_up_catalogue_amount.setDisable(false);
                tf_up_catalogue_amount.setText(catalogueItemToEdit.get(0).getColumn_catalogue_amount().toString());
                catalogueTable.setDisable(true);     
            }
        }
        catch(Exception e){}
    
    }  
    
    @FXML
    private void cancelUpdateCatalogueHandler(ActionEvent event){       
        try {
            setToDefaultCatalogueUpdate();
            connect.commit();
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
    }
    
    @FXML
    private void newCatalogueHandler(ActionEvent event){
        try {
            fillShopsComboBox();
            fillVinylsComboBox();
            
            connect.setAutoCommit(false);
                String query = "SELECT * FROM vinyls FOR UPDATE";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.close();  
                
            connect.setAutoCommit(false);
            query ="SELECT * FROM shops FOR UPDATE";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            resultSet.close();     
            
            cb_catalogue_shops.setDisable(false);
            cb_catalogue_vinyls.setDisable(false);
            catalogue_new_btn.setDisable(true);
            catalogue_cancel_insert_btn.setDisable(false);
            catalogue_insert_btn.setDisable(false);
            tf_in_catalogue_amount.setDisable(false);
        } catch (Exception ex) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    
    @FXML
    private void cancelInsertCatalogueHandler(ActionEvent event){
        try {
            setToDefaultCatalogueInsert();
            connect.commit();
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
    }  
    
    @FXML
    private void insertCatalogueHandler(ActionEvent event){
        
         try {
            String amount=tf_in_catalogue_amount.getText();
            String shop = cb_catalogue_shops.getValue();
            String vinyl = cb_catalogue_vinyls.getValue();            

            int shop_FK=0;
            int vinyl_FK=0;
            int value=-1;
       
            if(!amount.equals("") && !shop.equals("") && !vinyl.equals("")){
                connect.setAutoCommit(false); 
                
                String query = "SELECT shops.shop_id FROM shops WHERE shops.city=?";
                preparedStatement  =  connect.prepareStatement(query);
                preparedStatement.setString(1,shop);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   shop_FK=resultSet.getInt("shop_id");
                }           

                query = "SELECT vinyls.vinyls_id FROM vinyls WHERE vinyls.name LIKE ?";
                preparedStatement  =  connect.prepareStatement(query);
                preparedStatement.setString(1,"%"+vinyl);
                resultSet=preparedStatement.executeQuery();
                while(resultSet.next()){
                   vinyl_FK=resultSet.getInt("vinyls_id");
                }
                
                query = "SELECT shops_vinyls.num_of_vinyls FROM shops_vinyls WHERE shops_vinyls.shop_id='"+shop_FK+"' AND shops_vinyls.vinyl_id='"+vinyl_FK+"' ";
                //System.out.println(query);
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                   value=resultSet.getInt("num_of_vinyls");
                }
                
                System.out.println(value);
                
                if(value==-1){
                    query = "INSERT INTO shops_vinyls (shop_id, vinyl_id, num_of_vinyls) VALUES (?, ?, ?)";
                    preparedStatement = connect.prepareStatement(query);
                    preparedStatement.setInt(1, shop_FK);
                    preparedStatement.setInt(2, vinyl_FK);
                    preparedStatement.setString(3, amount);
                    preparedStatement.executeUpdate();
                    
                }
                else { 
                    value=value+Integer.parseInt(amount);
                    query = "UPDATE shops_vinyls SET num_of_vinyls=? WHERE shops_vinyls.shop_id=? AND shops_vinyls.vinyl_id=? ";
                    preparedStatement = connect.prepareStatement(query);
                    preparedStatement.setInt(1, value);
                    preparedStatement.setInt(2, shop_FK);
                    preparedStatement.setInt(3, vinyl_FK);
                    preparedStatement.executeUpdate();
                }
                connect.commit();             
                refreshCatalogueTable("%","%","%","%","%",10000,10000.0);    
            }
            setToDefaultCatalogueInsert();
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
            badInputError();
        }
        finally {
            if(preparedStatement != null) try {
                preparedStatement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    } 
    
    private void fillShopsComboBox() throws IOException{              
        try{
            catalogue_shops.clear();
            connect.setAutoCommit(false);
            cb_catalogue_shops.getSelectionModel().clearSelection();
            cb_catalogue_shops.getItems().clear();
            String query = "SELECT shops.city FROM shops ORDER BY city ASC";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                catalogue_shops.add(resultSet.getString("city"));
            }
            resultSet.close();
            connect.commit();
            cb_catalogue_shops.getItems().addAll(catalogue_shops);
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    private void fillVinylsComboBox() throws IOException{              
        try{
            catalogue_vinyls.clear();
            connect.setAutoCommit(false);
            cb_catalogue_vinyls.getSelectionModel().clearSelection();
            cb_catalogue_vinyls.getItems().clear();
            String query = "SELECT vinyls.name FROM vinyls ORDER BY name ASC";            
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                catalogue_vinyls.add(resultSet.getString("name"));
            }
            resultSet.close();
            connect.commit();            
            cb_catalogue_vinyls.getItems().addAll(catalogue_vinyls);
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    private void clearFilter(){
        tf_fl_catalogue_name.clear();
        tf_fl_catalogue_artist.clear();
        tf_fl_catalogue_genre.clear();
        tf_fl_catalogue_year.clear();
        tf_fl_catalogue_amount.clear();
        tf_fl_catalogue_shop.clear();
        tf_fl_catalogue_price.clear();
    }
    
    private void setToDefaultCatalogue(){
        tf_sell_catalogue_info.setDisable(true);
        tf_sell_catalogue_info.clear();
        tf_sell_catalogue_amount.setDisable(true);
        tf_sell_catalogue_amount.clear();
        catalogue_sell_btn.setDisable(false);
        catalogue_confirm_btn.setDisable(true);
        catalogue_cancel_sell_btn.setDisable(true);
        catalogueTable.setDisable(false);
    }
    
    private void setToDefaultCatalogueUpdate(){
        catalogue_cancel_update_btn.setDisable(true);
        catalogue_update_btn.setDisable(true);
        catalogue_edit_btn.setDisable(false);
        catalogue_sell_btn.setDisable(false);
        tf_up_catalogue_amount.setDisable(true);
        tf_up_catalogue_amount.clear();
        catalogueTable.setDisable(false); 
    }
    
    private void setToDefaultCatalogueInsert(){
        cb_catalogue_shops.setDisable(true);
        cb_catalogue_shops.setValue(null);
        cb_catalogue_vinyls.setDisable(true);
        cb_catalogue_vinyls.setValue(null);
        catalogue_new_btn.setDisable(false);
        catalogue_cancel_insert_btn.setDisable(true);
        catalogue_insert_btn.setDisable(true);
        tf_in_catalogue_amount.setDisable(true);
        tf_in_catalogue_amount.clear();
     }
    
    //HISTORY  _________________________________________________________________
    
    //buttons
    @FXML private Button history_refresh_btn;    
    //tables
    @FXML private TableView<Sold_vinyls> soldHistoryTable;       
    @FXML private TableColumn<Sold_vinyls, Integer> column_saleshistory_id;    
    @FXML private TableColumn<Sold_vinyls, Integer> column_saleshistory_sold;
    @FXML private TableColumn<Sold_vinyls, Double> column_saleshistory_price;
    @FXML private TableColumn<Sold_vinyls, Double> column_saleshistory_total;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_date;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_album;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_artist;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_city;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_name;
    @FXML private TableColumn<Sold_vinyls, String> column_saleshistory_surname;
    //textfield
    @FXML private TextField tf_fl_history_price;
    @FXML private TextField tf_fl_history_artist;
    @FXML private TextField tf_fl_history_album;
    @FXML private TextField tf_fl_history_city;
    @FXML private TextField tf_fl_history_amount;
    
    ObservableList<Sold_vinyls> sold = FXCollections.observableArrayList(); 
    
    //hadnlers
    @FXML
    private void refreshSoldHistoryHandler(ActionEvent event) throws IOException {        
       
        Integer tf_filin_price = 10000;
        String tf_filin_artist = "%";
        String tf_filin_album = "%";
        Integer tf_filin_amount = 10000;            
        String tf_filin_city = "%";

        try{
            if(!(tf_fl_history_price.getText().equals(""))){ 
                tf_filin_price=parseInt(tf_fl_history_price.getText());
            }
            else tf_filin_price=10000;
            if(!(tf_fl_history_artist.getText().equals(""))){ 
                tf_filin_artist=tf_fl_history_artist.getText();
            }
            else tf_filin_artist="%";
            if(!(tf_fl_history_album.getText().equals(""))){ 
                tf_filin_album=tf_fl_history_album.getText();
            }
            else tf_filin_album="%";
            if(!(tf_fl_history_amount.getText().equals(""))){ 
                tf_filin_amount=parseInt(tf_fl_history_amount.getText());
            }
            else tf_filin_amount=10000;
            if(!(tf_fl_history_city.getText().equals(""))){ 
                tf_filin_city=tf_fl_history_city.getText();
            }
            else tf_filin_city="%";    

            refreshSoldHistoryTable(tf_filin_city,tf_filin_artist,tf_filin_album,tf_filin_amount,tf_filin_price);
        }
        catch(Exception ex){
            badInputError();
        }
    }
     
    private void refreshSoldHistoryTable(String city, String artist, String album, Integer amount, Integer price){
        try{            
            sold.clear();
            connect.setAutoCommit(false); 
            String query = "SELECT sv.sold_vinyl_id, sv.num_of_sold, vinyls.price AS price, ROUND( vinyls.price "
                    + "* sv.num_of_sold, 2 ) AS total, sv.date, vinyls.name AS album, artists.name "
                    + "AS artist, shops.city, employee.name, employee.surname FROM ( SELECT * FROM sold_vinyls )"
                    + " AS sv JOIN vinyls ON sv.vinyl_id = vinyls.vinyls_id JOIN shops ON sv.shop_id = shops.shop_id "
                    + "JOIN employee ON sv.employee_id = employee.employee_id JOIN artists ON vinyls.artist_id = "
                    + "artists.artist_id WHERE sv.num_of_sold<=? AND vinyls.price * sv.num_of_sold<=?"
                    + " AND vinyls.name LIKE ? AND artists.name LIKE ? AND city LIKE ? ";
            preparedStatement  =  connect.prepareStatement(query);
            preparedStatement.setInt(1,amount);
            preparedStatement.setInt(2,price);
            preparedStatement.setString(3, "%" +album);
            preparedStatement.setString(4, "%" +artist);
            preparedStatement.setString(5, "%" +city);
            resultSet=preparedStatement.executeQuery();
            
            while(resultSet.next()){
                sold.add(new Sold_vinyls(
                        resultSet.getInt("sold_vinyl_id"),
                        resultSet.getInt("num_of_sold"),
                        resultSet.getDouble("price"),
                        resultSet.getDouble("total"),
                        resultSet.getString("date"),
                        resultSet.getString("album"),
                        resultSet.getString("artist"),
                        resultSet.getString("city"),
                        resultSet.getString("name"),
                        resultSet.getString("surname")
                ));
             }
            resultSet.close();
            connect.commit();
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
        }
        finally {
            if(statement != null) try {
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        column_saleshistory_id.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_id"));
        column_saleshistory_sold.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_sold"));
        column_saleshistory_price.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_price"));
        column_saleshistory_total.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_total"));
        column_saleshistory_date.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_date"));
        column_saleshistory_album.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_album"));
        column_saleshistory_artist.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_artist"));
        column_saleshistory_city.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_city"));
        column_saleshistory_name.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_name"));
        column_saleshistory_surname.setCellValueFactory(new PropertyValueFactory<>("column_saleshistory_surname"));    
        soldHistoryTable.setItems(sold);
    
    } 
    
    // STATISTICS ______________________________________________________________
    
    @FXML private PieChart chartProfit;
    @FXML private Button statistic_refresh_btn;
    @FXML private Button statistic_more_btn;    
    @FXML private TableView<Profit> profitTable;        
    @FXML private TableColumn<Profit, Integer> column_profit_city;    
    @FXML private TableColumn<Profit, String> column_profit_name;
    
    ObservableList<PieChart.Data> pieChartProfitData = FXCollections.observableArrayList();
    ObservableList<Profit> profit = FXCollections.observableArrayList();
    
    @FXML
    private void profitPieChart(){
        
        try{            
            pieChartProfitData.clear();
            profit.clear();
            connect.setAutoCommit(false);   
            String query="SELECT result.city, ROUND(SUM(result.sumofcol)) AS profit FROM ( SELECT "
                    + "sold_vinyls.num_of_sold * vinyls.price AS sumofcol, shops.city FROM sold_vinyls "
                    + "JOIN vinyls ON sold_vinyls.vinyl_id = vinyls.vinyls_id JOIN shops ON "
                    + "sold_vinyls.shop_id = shops.shop_id ) AS result GROUP BY result.city";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
           
            // fill pie chart
            while(resultSet.next()){
                pieChartProfitData.add(new PieChart.Data(
                        resultSet.getString("city"),
                        resultSet.getDouble("profit")
                    ));
             }
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
            // fill table next to pie chart
            while(resultSet.next()){
                profit.add(new Profit(
                        resultSet.getDouble("profit"),
                        resultSet.getString("city")
                    ));
             }
            
            resultSet.close();
             connect.commit();
            chartProfit.setData(pieChartProfitData); 
            column_profit_name.setCellValueFactory(new PropertyValueFactory<>("column_profit_name"));
            column_profit_city.setCellValueFactory(new PropertyValueFactory<>("column_profit_city"));    
            profitTable.setItems(profit);
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
        catch (Exception e) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, e);
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
    private void refreshProfitPieChart(ActionEvent event) throws IOException {        
        profitPieChart();
    }

    @FXML
    private void moreStatisticsHandler(ActionEvent event){
        ScreenNavigator.loadScreen(ScreenNavigator.STATISTICS_MORE);
    }    
    
    /**
     * Alert if you do something wrong
     */
    private void removeError(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText("Look, you cant delete item that is currently used!");
        alert.showAndWait(); 
    }   
    
    private void badInputError(){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText("Bad input!!");
        alert.showAndWait(); 
    }      
    
    /**
     * Intitialize funkction starts after the program is execute connects to dbs a fill all tables
     * @param location
     * @param resources 
     */
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Logged in..");
            connector.connectToDbs();
            connect=connector.getConnect();
       
            //catalogue
            refreshCatalogueTable("%","%","%","%","%",10000,10000.0);
            //history
            refreshSoldHistoryTable("%","%","%",10000,10000);
            //statistics
            profitPieChart();
            //vinyls
            fillArtistComboBox(cb_vinyls_artist,cb_vinyls_up_artist);
            fillLabelComboBox(cb_vinyls_label,cb_vinyls_up_label);
            fillGenreComboBox(cb_vinyls_genre,cb_vinyls_up_genre); 
            refreshVinylTable();
            //employees
            refreshEmployeeTable("%","%","%","%","%");            
            //others
            refreshShopTable();
            refreshPostTable();
            
        } catch (IOException ex) {
            Logger.getLogger(InsideController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}