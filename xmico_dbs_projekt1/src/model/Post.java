package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Post {
    
    private final SimpleIntegerProperty column_post_id;
    private final SimpleStringProperty column_post_name;
    
    public Post(Integer id, String name){        
        this.column_post_id = new SimpleIntegerProperty(id);
        this.column_post_name = new SimpleStringProperty(name);
    }

    public int getColumn_post_id() {
        return column_post_id.get();
    }

    public String getColumn_post_name() {
        return column_post_name.get();
    }

    public void setColumn_post_id(Integer id) {
        column_post_id.set(id);
    }

    public void setColumn_post_city(String name) {
       column_post_name.set(name);
    }    
    
}
