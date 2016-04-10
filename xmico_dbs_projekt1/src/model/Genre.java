package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Genre {
    
    private final SimpleIntegerProperty column_genre_id;
    private final SimpleStringProperty column_genre_name;
    
    public Genre(Integer id, String name){        
        this.column_genre_id = new SimpleIntegerProperty(id);
        this.column_genre_name = new SimpleStringProperty(name);
    }

    public int getColumn_genre_id() {
        return column_genre_id.get();
    }

    public String getColumn_genre_name() {
        return column_genre_name.get();
    }

    public void setColumn_genre_id(Integer id) {
        column_genre_id.set(id);
    }

    public void setColumn_genre_city(String name) {
       column_genre_name.set(name);
    }    
    
}