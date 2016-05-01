package model;

import javafx.beans.property.SimpleStringProperty;

public class Access {
    
    private final SimpleStringProperty column_access_since;
    private final SimpleStringProperty column_access_to;
    private final SimpleStringProperty column_access_time;
    
    public Access(String since, String to, String time){
        
        this.column_access_since = new SimpleStringProperty(since);
        this.column_access_to = new SimpleStringProperty(to);
        this.column_access_time = new SimpleStringProperty(time);
    }

    public String getColumn_access_since() {
        return column_access_since.get();
    }

    public String getColumn_access_to() {
        return column_access_to.get();
    }

    public String getColumn_access_time() {
        return column_access_time.get();
    }

    public void setColumn_access_since(String since) {
        column_access_since.set(since);
    }

    public void setColumn_access_to(String to) {
       column_access_to.set(to);
    }

    public void setColumn_access_time(String time) {
        column_access_time.set(time);
    }
   
}
