package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Vinyls_shops {
    
    private final SimpleIntegerProperty column_catalogue_id;
    private final SimpleStringProperty column_catalogue_name;
    private final SimpleStringProperty column_catalogue_artist;
    private final SimpleStringProperty column_catalogue_genre;
    private final SimpleIntegerProperty column_catalogue_year;
    private final SimpleIntegerProperty column_catalogue_amount;
    private final SimpleStringProperty column_catalogue_shop;
    private final SimpleDoubleProperty column_catalogue_price;
    private final SimpleIntegerProperty column_catalogue_vinyl_id;
    
    public Vinyls_shops(Integer id, String artist, String name, String genre, 
            Integer year, Integer amount, String shop, Double price, Integer vinyls_id){
        
        this.column_catalogue_id = new SimpleIntegerProperty(id);
        this.column_catalogue_name = new SimpleStringProperty(name);
        this.column_catalogue_artist = new SimpleStringProperty(artist);
        this.column_catalogue_genre = new SimpleStringProperty(genre);
        this.column_catalogue_year = new SimpleIntegerProperty(year);
        this.column_catalogue_amount = new SimpleIntegerProperty(amount);
        this.column_catalogue_shop = new SimpleStringProperty(shop);
        this.column_catalogue_price = new SimpleDoubleProperty(price);
        this.column_catalogue_vinyl_id = new SimpleIntegerProperty(vinyls_id);
    }

    public Integer getColumn_catalogue_id() {
        return column_catalogue_id.get();
    }

    public String getColumn_catalogue_name() {
        return column_catalogue_name.get();
    }

    public String getColumn_catalogue_artist() {
        return column_catalogue_artist.get();
    }

    public String getColumn_catalogue_genre() {
        return column_catalogue_genre.get();
    }

    public Integer getColumn_catalogue_year() {
        return column_catalogue_year.get();
    }

    public Integer getColumn_catalogue_amount() {
        return column_catalogue_amount.get();
    }

    public String getColumn_catalogue_shop() {
        return column_catalogue_shop.get();
    }

    public Double getColumn_catalogue_price() {
        return column_catalogue_price.get();
    }
    
    public Integer getColumn_catalogue_vinyl_id() {
        return column_catalogue_vinyl_id.get();
    }
    
    public void setColumn_catalogue_id(Integer id) {
       column_catalogue_id.set(id);
    }

    public void setColumn_catalogue_name(String name) {
        column_catalogue_name.set(name);
    }

    public void setColumn_catalogue_artist(String artist) {
        column_catalogue_artist.set(artist);
    }

    public void setColumn_catalogue_genre(String genre) {
        column_catalogue_genre.set(genre);
    }

    public void setColumn_catalogue_year(Integer year) {
        column_catalogue_year.set(year);
    }

    public void setColumn_catalogue_amount(Integer amount) {
        column_catalogue_amount.set(amount);
    }

    public void setColumn_catalogue_shop(String shop) {
        column_catalogue_shop.set(shop);
    }

    public void setColumn_catalogue_price(Double price) {
        column_catalogue_price.set(price);
    }

    public void setColumn_catalogue_vinyl_id(Integer id) {
        column_catalogue_amount.set(id);
    }    
}
