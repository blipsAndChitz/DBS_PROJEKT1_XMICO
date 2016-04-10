package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Vinyls{
    
    private final SimpleIntegerProperty column_vinyl_id;
    private final SimpleStringProperty column_vinyl_name;
    private final SimpleStringProperty column_vinyl_artist;
    private final SimpleStringProperty column_vinyl_genre;
    private final SimpleIntegerProperty column_vinyl_year;
    private final SimpleStringProperty column_vinyl_label;
    private final SimpleStringProperty column_vinyl_length;
    private final SimpleDoubleProperty column_vinyl_price;
    
    public Vinyls(Integer id, String name, String artist, String genre, 
            Integer year, String label, String length, Double price){
        
        this.column_vinyl_id = new SimpleIntegerProperty(id);
        this.column_vinyl_name = new SimpleStringProperty(name);
        this.column_vinyl_artist = new SimpleStringProperty(artist);
        this.column_vinyl_genre = new SimpleStringProperty(genre);
        this.column_vinyl_year = new SimpleIntegerProperty(year);
        this.column_vinyl_label = new SimpleStringProperty(label);
        this.column_vinyl_length = new SimpleStringProperty(length);
        this.column_vinyl_price = new SimpleDoubleProperty(price);
    }

    public Integer getColumn_vinyl_id() {
        return column_vinyl_id.get();
    }

    public String getColumn_vinyl_name() {
        return column_vinyl_name.get();
    }

    public String getColumn_vinyl_artist() {
        return column_vinyl_artist.get();
    }

    public String getColumn_vinyl_genre() {
        return column_vinyl_genre.get();
    }

    public Integer getColumn_vinyl_year() {
        return column_vinyl_year.get();
    }

    public String getColumn_vinyl_label() {
        return column_vinyl_label.get();
    }

    public String getColumn_vinyl_length() {
        return column_vinyl_length.get();
    }

    public Double getColumn_vinyl_price() {
        return column_vinyl_price.get();
    }
    
    public void setColumn_Vinyl_id(Integer id) {
       column_vinyl_id.set(id);
    }

    public void setColumn_Vinyl_name(String name) {
        column_vinyl_name.set(name);
    }

    public void setColumn_Vinyl_artist(String artist) {
        column_vinyl_artist.set(artist);
    }

    public void setColumn_Vinyl_genre(String genre) {
        column_vinyl_genre.set(genre);
    }

    public void setColumn_Vinyl_year(Integer year) {
        column_vinyl_year.set(year);
    }

    public void setColumn_Vinyl_amount(String label) {
        column_vinyl_label.set(label);
    }

    public void setColumn_Vinyl_shop(String length) {
        column_vinyl_length.set(length);
    }

    public void setColumn_Vinyl_price(Double price) {
        column_vinyl_price.set(price);
    }    
}
