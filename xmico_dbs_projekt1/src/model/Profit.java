package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Profit{
    
    private final SimpleDoubleProperty column_profit_name;
    private final SimpleStringProperty column_profit_city;
    
    public Profit(Double id, String city){
        
        this.column_profit_name = new SimpleDoubleProperty(id);
        this.column_profit_city = new SimpleStringProperty(city);
    }

    public Double getColumn_profit_name() {
        return column_profit_name.get();
    }

    public String getColumn_profit_city() {
        return column_profit_city.get();
    }
   
    public void setColumn_profit_name(Integer id) {
       column_profit_name.set(id);
    }

    public void setColumn_profit_city(Integer sold) {
        column_profit_name.set(sold);
    }    
}
