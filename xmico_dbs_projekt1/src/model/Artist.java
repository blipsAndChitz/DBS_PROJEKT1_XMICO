package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Artist {
    
    private final SimpleIntegerProperty column_artist_id;
    private final SimpleStringProperty column_artist_name;
    private final SimpleStringProperty column_artist_addinfo;
    
    public Artist(Integer id, String name, String info){
        
        this.column_artist_id = new SimpleIntegerProperty(id);
        this.column_artist_name = new SimpleStringProperty(name);
        this.column_artist_addinfo = new SimpleStringProperty(info);
    }

    public int getColumn_artist_id() {
        return column_artist_id.get();
    }

    public String getColumn_artist_name() {
        return column_artist_name.get();
    }

    public String getColumn_artist_addinfo() {
        return column_artist_addinfo.get();
    }

    public void setColumn_artist_id(Integer id) {
        column_artist_id.set(id);
    }

    public void setColumn_artist_name(String name) {
       column_artist_name.set(name);
    }

    public void setColumn_artist_addinfo(String info) {
        column_artist_addinfo.set(info);
    }
   
}
