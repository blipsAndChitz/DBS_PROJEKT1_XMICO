package model;

import javafx.beans.property.SimpleStringProperty;

public class TimeStats{
    
    private final SimpleStringProperty column_sold_year;
    private final SimpleStringProperty column_sold_month;
    private final SimpleStringProperty column_sold_amount;

    public TimeStats(String year, String month, String total){
        
        this.column_sold_year = new SimpleStringProperty(year);
        this.column_sold_month = new SimpleStringProperty(month);
        this.column_sold_amount = new SimpleStringProperty(total);
    }

    public String getColumn_sold_year() {
        return column_sold_year.get();
    }

    public String getColumn_sold_month() {
        return column_sold_month.get();
    }

    public String getColumn_sold_amount() {
        return column_sold_amount.get();
    }
    
    public void setColumn_sold_year(String id) {
       column_sold_year.set(id);
    }

    public void setColumn_sold_month(String name) {
        column_sold_month.set(name);
    }

    public void setColumn_sold_amount(String artist) {
        column_sold_amount.set(artist);
    }

}
