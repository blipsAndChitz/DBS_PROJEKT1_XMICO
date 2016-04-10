package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sold_vinyls{
    
    private final SimpleIntegerProperty column_saleshistory_id;
    private final SimpleIntegerProperty column_saleshistory_sold;
    private final SimpleDoubleProperty column_saleshistory_price;
    private final SimpleDoubleProperty column_saleshistory_total;
    private final SimpleStringProperty column_saleshistory_date;
    private final SimpleStringProperty column_saleshistory_album;
    private final SimpleStringProperty column_saleshistory_artist;
    private final SimpleStringProperty column_saleshistory_city;
    private final SimpleStringProperty column_saleshistory_name;
    private final SimpleStringProperty column_saleshistory_surname;
    
    public Sold_vinyls(Integer id, Integer sold, Double price, Double total, String date, String album, 
            String artist, String city, String name, String surname){
        
        this.column_saleshistory_id = new SimpleIntegerProperty(id);
        this.column_saleshistory_sold = new SimpleIntegerProperty(sold);
        this.column_saleshistory_price = new SimpleDoubleProperty(price);
        this.column_saleshistory_total = new SimpleDoubleProperty(total);
        this.column_saleshistory_date = new SimpleStringProperty(date);
        this.column_saleshistory_album = new SimpleStringProperty(album);
        this.column_saleshistory_artist = new SimpleStringProperty(artist);
        this.column_saleshistory_city = new SimpleStringProperty(city);
        this.column_saleshistory_name = new SimpleStringProperty(name);
        this.column_saleshistory_surname = new SimpleStringProperty(surname);
    }

    public Integer getColumn_saleshistory_id() {
        return column_saleshistory_id.get();
    }

    public Integer getColumn_saleshistory_sold() {
        return column_saleshistory_sold.get();
    }

    public Double getColumn_saleshistory_price() {
        return column_saleshistory_price.get();
    }
        
    public Double getColumn_saleshistory_total() {
        return column_saleshistory_total.get();
    }    
        
    public String getColumn_saleshistory_date() {
        return column_saleshistory_date.get();
    }

    public String getColumn_saleshistory_album() {
        return column_saleshistory_album.get();
    }

    public String getColumn_saleshistory_artist() {
        return column_saleshistory_artist.get();
    }

    public String getColumn_saleshistory_city() {
        return column_saleshistory_city.get();
    }

    public String getColumn_saleshistory_name() {
        return column_saleshistory_name.get();
    }

    public String getColumn_saleshistory_surname() {
        return column_saleshistory_surname.get();
    }
    
    public void setColumn_saleshistory_id(Integer id) {
       column_saleshistory_id.set(id);
    }

    public void setColumn_saleshistory_sold(Integer sold) {
        column_saleshistory_sold.set(sold);
    }

    public void setColumn_saleshistory_price(Double price) {
        column_saleshistory_price.set(price);
    }
    
    public void setColumn_saleshistory_total(Double total) {
        column_saleshistory_total.set(total);
    }
    
    public void setColumn_saleshistory_date(String date) {
        column_saleshistory_date.set(date);
    }

    public void setColumn_saleshistory_album(String album) {
        column_saleshistory_album.set(album);
    }

    public void setColumn_saleshistory_artist(String artist) {
        column_saleshistory_artist.set(artist);
    }

    public void setColumn_saleshistory_city(String city) {
        column_saleshistory_city.set(city);
    }

    public void setColumn_saleshistory_name(String name) {
        column_saleshistory_name.set(name);
    }

    public void setColumn_saleshistory_surname(String surname) {
        column_saleshistory_surname.set(surname);
    }    
}
