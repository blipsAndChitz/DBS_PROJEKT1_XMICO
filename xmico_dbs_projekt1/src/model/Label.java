package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Label{
    
    private final SimpleIntegerProperty column_label_id;
    private final SimpleStringProperty column_label_label;
    private final SimpleIntegerProperty column_label_founded;
    private final SimpleStringProperty column_label_city;
    private final SimpleStringProperty column_label_website;
    
    public Label(Integer id, String label, Integer year, String city, 
                String web){
        
        this.column_label_id = new SimpleIntegerProperty(id);
        this.column_label_label = new SimpleStringProperty(label);
        this.column_label_founded = new SimpleIntegerProperty(year);
        this.column_label_city = new SimpleStringProperty(city);
        this.column_label_website = new SimpleStringProperty(web);
    }

    public Label(String username) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Integer getColumn_label_id() {
        return column_label_id.get();
    }

    public String getColumn_label_label() {
        return column_label_label.get();
    }

    public Integer getColumn_label_founded() {
        return column_label_founded.get();
    }

    public String getColumn_label_city() {
        return column_label_city.get();
    }

    public String getColumn_label_website() {
        return column_label_website.get();
    }
       
    public void setColumn_label_id(Integer id) {
       column_label_id.set(id);
    }

    public void setColumn_label_label(String label) {
        column_label_label.set(label);
    }

    public void setColumn_label_year(Integer year) {
        column_label_founded.set(year);
    }

    public void setColumn_label_city(String city) {
        column_label_city.set(city);
    }

    public void setColumn_label_website(String web) {
        column_label_website.set(web);
    }
 
}
