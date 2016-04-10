package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Artist;
import model.Genre;
import model.Label;


public class VinylsMoreControl implements Initializable {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private PreparedStatement preparedStatement = null;
    Connector connector = new Connector();  
    
    @FXML private Button logOff_btn;    
    @FXML private Button goBack_btn;
    
    @FXML
    void logOffHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.LOGIN);
    }
    
    @FXML
    void goBackHandler(ActionEvent event) {
        ScreenNavigator.loadScreen(ScreenNavigator.INSIDE);
    }
    
    
    //LABEL_____________________________________________________________________
    
    //buttons
    @FXML private Button label_refresh_btn;
    @FXML private Button label_insert_btn;
    @FXML private Button label_update_btn;
    @FXML private Button label_remove_btn;     
    @FXML private Button label_edit_btn;
    @FXML private Button label_cancel_btn;    
    
    //tables
    @FXML private TableView<Label> labelsTable;       
    @FXML private TableColumn<Label, Integer> column_label_id;    
    @FXML private TableColumn<Label, String> column_label_label;
    @FXML private TableColumn<Label, Integer> column_label_founded;
    @FXML private TableColumn<Label, String> column_label_city;
    @FXML private TableColumn<Label, String> column_label_website;
    
    ObservableList<Label> labels = FXCollections.observableArrayList(); 
    ObservableList<Label> labelToEdit;
    ObservableList<Label> labelToRemove;
    
    //textfields
    @FXML private TextField tf_in_label_label;
    @FXML private TextField tf_in_label_founded;
    @FXML private TextField tf_in_label_location;
    @FXML private TextField tf_in_label_website;
    @FXML private TextField tf_up_label_label;
    @FXML private TextField tf_up_label_founded;
    @FXML private TextField tf_up_label_location;
    @FXML private TextField tf_up_label_website;    
    
    //hadnlers
    @FXML
    private void refreshLabelHandler(ActionEvent event){
        refreshLabelTable();        
    }
    
    private void refreshLabelTable(){ 
        try{            
            labels.clear();
            connect.setAutoCommit(false); 
            String query = "SELECT * FROM label ORDER BY label.label_id ASC";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
            while(resultSet.next()){
                labels.add(new Label(
                        resultSet.getInt("label_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("founded"),
                        resultSet.getString("location"),
                        resultSet.getString("website")
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
        column_label_id.setCellValueFactory(new PropertyValueFactory<>("column_label_id"));
        column_label_label.setCellValueFactory(new PropertyValueFactory<>("column_label_label"));
        column_label_founded.setCellValueFactory(new PropertyValueFactory<>("column_label_founded"));
        column_label_city.setCellValueFactory(new PropertyValueFactory<>("column_label_city"));
        column_label_website.setCellValueFactory(new PropertyValueFactory<>("column_label_website"));
        labelsTable.setItems(labels);
    }
    
    @FXML
    private void insertLabelHandler(ActionEvent event) {
        try {
            String label=tf_in_label_label.getText();
            String city=tf_in_label_location.getText();
            int year=Integer.parseInt(tf_in_label_founded.getText());
            String website = tf_in_label_website.getText();
            
            if(year<9999 && !label.equals("") && !city.equals("") &&  
                    !website.equals("") && !label.equals("")){            
                
                connect.setAutoCommit(false);          
                String query = "INSERT INTO label (name, founded, location, website) VALUES (?, ?, ?, ?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, label);
                preparedStatement.setInt(2, year);
                preparedStatement.setString(3, city);
                preparedStatement.setString(4, website);
                preparedStatement.executeUpdate();
                connect.commit();
                
                refreshLabelTable();
                 
                tf_in_label_label.clear();
                tf_in_label_founded.clear();
                tf_in_label_location.clear();
                tf_in_label_website.clear();
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
    private void removeLabelHandler(ActionEvent event) {        
        try{
            connect.setAutoCommit(false);     
            labelToRemove = labelsTable.getSelectionModel().getSelectedItems();                    			
            String query = "DELETE FROM label WHERE label_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, labelToRemove.get(0).getColumn_label_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshLabelTable();
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
    private void editLabelHandler(ActionEvent event){       
        try{
            labelToEdit = labelsTable.getSelectionModel().getSelectedItems();
            if(labelToEdit.get(0).getColumn_label_id()> -1){
                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM label WHERE label.label_id="+labelToEdit.get(0).getColumn_label_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close(); 
                
                label_cancel_btn.setDisable(false);
                label_update_btn.setDisable(false);
                label_edit_btn.setDisable(true);
                tf_up_label_label.setDisable(false);
                tf_up_label_label.setText(labelToEdit.get(0).getColumn_label_label());
                tf_up_label_founded.setDisable(false);
                tf_up_label_founded.setText(labelToEdit.get(0).getColumn_label_founded().toString());
                tf_up_label_location.setDisable(false);
                tf_up_label_location.setText(labelToEdit.get(0).getColumn_label_city());
                tf_up_label_website.setDisable(false);
                tf_up_label_website.setText(labelToEdit.get(0).getColumn_label_website());
                labelsTable.setDisable(true);     
            }
        }
        catch(Exception e){}
    }
    
    @FXML
    private void cancelLabelHandler(ActionEvent event){         
        try {
            setToDefaultLabel();
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
    private void updateLabelHandler(ActionEvent event){       
        try {
            String label=tf_up_label_label.getText();
            String city=tf_up_label_location.getText();
            int year=Integer.parseInt(tf_up_label_founded.getText());
            String state = tf_up_label_website.getText();
        
            if(year<9999 && !label.equals("") && !city.equals("") &&  
                    !state.equals("artist") && !label.equals("label")){            
                connect.setAutoCommit(false);  
                String query = "UPDATE label SET name=?, founded=?, location=?, website=? WHERE label_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, label);
                preparedStatement.setInt(2, year);
                preparedStatement.setString(3, city);
                preparedStatement.setString(4, state);
                preparedStatement.setInt(5, labelToEdit.get(0).getColumn_label_id());
                preparedStatement.executeUpdate();
                connect.commit();      
                setToDefaultLabel();
                refreshLabelTable();
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
    
    private void setToDefaultLabel(){
        label_cancel_btn.setDisable(true);
        label_update_btn.setDisable(true);
        label_edit_btn.setDisable(false);
        tf_up_label_label.setDisable(true);
        tf_up_label_label.clear();
        tf_up_label_founded.setDisable(true);
        tf_up_label_founded.clear();
        tf_up_label_location.setDisable(true);
        tf_up_label_location.clear();
        tf_up_label_website.setDisable(true);
        tf_up_label_website.clear();
        labelsTable.setDisable(false);       
    }     
    
    // ARTIST___________________________________________________________________
   
    //buttons
    @FXML private Button artist_refresh_btn;
    @FXML private Button artist_insert_btn;
    @FXML private Button artist_update_btn;
    @FXML private Button artist_remove_btn;     
    @FXML private Button artist_edit_btn;
    @FXML private Button artist_cancel_btn;  
    
    //tables
    @FXML private TableView<Artist> artistsTable;       
    @FXML private TableColumn<Artist, Integer> column_artist_id;    
    @FXML private TableColumn<Artist, String> column_artist_name;
    @FXML private TableColumn<Artist, String> column_artist_addinfo;    
    
    ObservableList<Artist> artists = FXCollections.observableArrayList(); 
    ObservableList<Artist> artistlToEdit;
    ObservableList<Artist> artistToRemove;
    ObservableList<PieChart.Data> pieChartArtistData = FXCollections.observableArrayList();
    
    @FXML private PieChart chartArtist;
    
    //textfields
    @FXML private TextField tf_in_artist_name;
    @FXML private TextField tf_in_artist_addinfo;
    @FXML private TextField tf_up_artist_name;
    @FXML private TextField tf_up_artist_addinfo;
    
    //hadnlers
    @FXML
    private void refreshArtistHandler(ActionEvent event){        
        refreshArtistTable();
    }  

    /**
     * This method refresh table of artist and pie chart
     */
    private void refreshArtistTable(){        
       
        try{            
            artists.clear();
            connect.setAutoCommit(false);  
            String query = "SELECT * FROM artists ORDER BY artists.artist_id ASC";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
            while(resultSet.next()){
                artists.add(new Artist(
                        resultSet.getInt("artist_id"),
                        resultSet.getString("name"),
                        resultSet.getString("add_info")
                ));
             }
            resultSet.close();
            connect.commit();
            artistChart();
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

        column_artist_id.setCellValueFactory(new PropertyValueFactory<>("column_artist_id"));
        column_artist_name.setCellValueFactory(new PropertyValueFactory<>("column_artist_name"));
        column_artist_addinfo.setCellValueFactory(new PropertyValueFactory<>("column_artist_addinfo"));
        artistsTable.setItems(artists);
    }
        
    @FXML
    private void insertArtistHandler(ActionEvent event) {
        try {
            String name=tf_in_artist_name.getText();
            String info=tf_in_artist_addinfo.getText();
       
            if(!name.equals("")){            
                connect.setAutoCommit(false);         
                String query = "INSERT INTO artists (name, add_info) VALUES (?, ?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, info);
                preparedStatement.executeUpdate();
                connect.commit();

                tf_in_artist_name.setText("");
                tf_in_artist_addinfo.setText("");
                refreshArtistTable();
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
    private void removeArtistHandler(ActionEvent event) {        
        try{
            connect.setAutoCommit(false);
            artistToRemove = artistsTable.getSelectionModel().getSelectedItems();                    			
            String query = "DELETE FROM artists WHERE artist_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, artistToRemove.get(0).getColumn_artist_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshArtistTable();
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
    private void updateArtistHandler(ActionEvent event) {
        try {
            String name=tf_up_artist_name.getText();
            String info=tf_up_artist_addinfo.getText();
       
            if(!name.equals("")){            
                connect.setAutoCommit(false);            
                String query = "UPDATE artists SET name=?, add_info=? WHERE artist_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, info);
                preparedStatement.setInt(3, artistlToEdit.get(0).getColumn_artist_id());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connect.commit();
                tf_up_artist_name.setText("");
                tf_up_artist_addinfo.setText("");
                setToDefaultArtist();
                refreshArtistTable();
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
    private void editArtistHandler(ActionEvent event){       
        try{
            artistlToEdit = artistsTable.getSelectionModel().getSelectedItems();
            if(artistlToEdit.get(0).getColumn_artist_id()> -1){
                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM artists WHERE artists.artist_id="+artistlToEdit.get(0).getColumn_artist_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close(); 
                
                artist_cancel_btn.setDisable(false);
                artist_update_btn.setDisable(false);
                artist_edit_btn.setDisable(true);
                tf_up_artist_name.setDisable(false);
                tf_up_artist_name.setText(artistlToEdit.get(0).getColumn_artist_name());
                tf_up_artist_addinfo.setDisable(false);
                tf_up_artist_addinfo.setText(artistlToEdit.get(0).getColumn_artist_addinfo());
                artistsTable.setDisable(true);     
            }
        }
        catch(Exception e){}
    }
    
    @FXML
    private void cancelArtistHandler(ActionEvent event){
        try {
            setToDefaultArtist();
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
    
    private void setToDefaultArtist(){
        artist_cancel_btn.setDisable(true);
        artist_update_btn.setDisable(true);
        artist_edit_btn.setDisable(false);
        tf_up_artist_name.setDisable(true);
        tf_up_artist_name.clear();
        tf_up_artist_addinfo.setDisable(true);
        tf_up_artist_addinfo.clear();
        artistsTable.setDisable(false);  
    }
    
    /**
     * This method set pie graf in artist tab
     */
    private void artistChart(){
        try{            
            pieChartArtistData.clear();
            connect.setAutoCommit(false);
            String query="SELECT artists.name, COUNT(vinyls.vinyls_id) AS num FROM vinyls"
                    + " LEFT JOIN artists ON vinyls.artist_id=artists.artist_id GROUP BY"
                    + " name ORDER BY num DESC LIMIT 10";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                pieChartArtistData.add(new PieChart.Data(
                        resultSet.getString("name"),
                        resultSet.getInt("num")
                    ));
             }
            resultSet.close();
            connect.commit();
            chartArtist.setData(pieChartArtistData);                       
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
    
    
    // GENRE____________________________________________________________________
    
    //buttons
    @FXML private Button genre_refresh_btn;
    @FXML private Button genre_insert_btn;
    @FXML private Button genre_update_btn;
    @FXML private Button genre_remove_btn;     
    @FXML private Button genre_edit_btn;
    @FXML private Button genre_cancel_btn;  
        
    //tables
    @FXML private TableView<Genre> genresTable;       
    @FXML private TableColumn<Genre, Integer> column_genre_id;    
    @FXML private TableColumn<Genre, String> column_genre_name;

    ObservableList<Genre> genres = FXCollections.observableArrayList(); 
    ObservableList<Genre> genreToEdit;
    ObservableList<Genre> genreToRemove;
    ObservableList<PieChart.Data> pieChartGenreData = FXCollections.observableArrayList();
    
    @FXML private PieChart chartGenre;    
    
    @FXML private TextField tf_in_genre_name;
    @FXML private TextField tf_up_genre_name;
    
     //hadnlers
    
    @FXML
    private void refreshGenreHandler(ActionEvent event) {
        refreshGenreTable();
    }
    /**
     * This method refresh table of genre and pie chart
     */
    private void refreshGenreTable() {      
        try{            
            genres.clear();
            connect.setAutoCommit(false);  
            String query = "SELECT * FROM genre ORDER BY genre.genre_id ASC";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);
  
            while(resultSet.next()){
                genres.add(new Genre(
                        resultSet.getInt("genre_id"),
                        resultSet.getString("name")
                    ));
             }
            resultSet.close();
            connect.commit();
            genreChart();
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

        column_genre_id.setCellValueFactory(new PropertyValueFactory<>("column_genre_id"));
        column_genre_name.setCellValueFactory(new PropertyValueFactory<>("column_genre_name"));
        genresTable.setItems(genres);
    }
    
    @FXML
    private void insertGenreHandler(ActionEvent event) {
        try {
            String name=tf_in_genre_name.getText();        
            if(!name.equals("")){
                connect.setAutoCommit(false);       
                String query = "INSERT INTO genre (name) VALUES (?)";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.executeUpdate();
                connect.commit();
                tf_in_genre_name.setText("");
                refreshGenreTable();
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
    private void removeGenreHandler(ActionEvent event) {        
        try{ 
            connect.setAutoCommit(false); 
            genreToRemove = genresTable.getSelectionModel().getSelectedItems();                    			
            String query = "DELETE FROM genre WHERE genre_id=?";
            preparedStatement  =  connect.prepareStatement(query);			
            preparedStatement.setInt(1, genreToRemove.get(0).getColumn_genre_id());
            preparedStatement.executeUpdate();
            connect.commit();
            refreshGenreTable();
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
    private void editGenreHandler(ActionEvent event){       
        try{
            genreToEdit = genresTable.getSelectionModel().getSelectedItems();
            if(genreToEdit.get(0).getColumn_genre_id()> -1){
                
                connect.setAutoCommit(false);
                String query = "SELECT * FROM genre WHERE genre.genre_id="+genreToEdit.get(0).getColumn_genre_id()+" FOR UPDATE";
                statement = connect.createStatement();
                resultSet = statement.executeQuery(query);
                resultSet.close(); 
                
                genre_cancel_btn.setDisable(false);
                genre_update_btn.setDisable(false);
                genre_edit_btn.setDisable(true);
                tf_up_genre_name.setDisable(false);
                tf_up_genre_name.setText(genreToEdit.get(0).getColumn_genre_name());
                genresTable.setDisable(true);     
            }
        }
        catch(Exception e){}
    }
    
    @FXML
    private void cancelGenreHandler(ActionEvent event){       
        try {
           setToDefaultGenre();
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
    private void updateGenreHandler(ActionEvent event){       
        try {
            String name=tf_up_genre_name.getText();        
            if(!name.equals("")){
                connect.setAutoCommit(false);  
                String query = "UPDATE genre SET name=? WHERE genre_id=?";
                preparedStatement = connect.prepareStatement(query);
                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, genreToEdit.get(0).getColumn_genre_id());
                preparedStatement.executeUpdate();
                connect.commit();                       
                setToDefaultGenre();
                refreshGenreTable();
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
        
    private void setToDefaultGenre(){
        genre_cancel_btn.setDisable(true);
        genre_update_btn.setDisable(true);
        genre_edit_btn.setDisable(false);
        tf_up_genre_name.setDisable(true);
        tf_up_genre_name.clear();
        genresTable.setDisable(false);        
    }

    
    private void genreChart(){
        try{
            connect.setAutoCommit(false);            
            pieChartGenreData.clear();
            String query="SELECT genre.name, COUNT(vinyls.vinyls_id) AS num FROM vinyls\n" +
                    "LEFT JOIN genre\n" +
                    "ON vinyls.genre_id=genre.genre_id\n" +
                    "GROUP BY name ORDER BY num DESC LIMIT 12";
            statement = connect.createStatement();
            resultSet = statement.executeQuery(query);  
            while(resultSet.next()){
                pieChartGenreData.add(new PieChart.Data(
                        resultSet.getString("name"),
                        resultSet.getInt("num")
                    ));
             }
            resultSet.close();
            connect.commit();
            chartGenre.setData(pieChartGenreData);                       
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
    
    //__________________________________________________________________________
    
    private void badInputError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Ooops, there was an error!");
        alert.setContentText("Bad input!!");
        alert.showAndWait(); 
    }   
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connector.connectToDbs();
        connect=connector.getConnect();
       
        //labels
        refreshLabelTable();
        
        //genre tab
        genreChart();
        refreshGenreTable();
        
        //artist tab
        refreshArtistTable();
        artistChart();
    }
    
}
