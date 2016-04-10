package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Shop {
    
    private final SimpleIntegerProperty column_shop_id;
    private final SimpleStringProperty column_shop_city;
    private final SimpleStringProperty column_shop_address;
    private final SimpleIntegerProperty column_shop_postcode;
    
    public Shop(Integer id, String city, String address, Integer postcode){
        
        this.column_shop_id = new SimpleIntegerProperty(id);
        this.column_shop_city = new SimpleStringProperty(city);
        this.column_shop_address = new SimpleStringProperty(address);
        this.column_shop_postcode = new SimpleIntegerProperty(postcode);
    }

    public int getColumn_shop_id() {
        return column_shop_id.get();
    }

    public String getColumn_shop_city() {
        return column_shop_city.get();
    }

    public String getColumn_shop_address() {
        return column_shop_address.get();
    }

    public int getColumn_shop_postcode() {
        return column_shop_postcode.get();
    }
    
    public void setColumn_shop_id(Integer id) {
        column_shop_id.set(id);
    }

    public void setColumn_shop_city(String city) {
       column_shop_city.set(city);
    }

    public void setColumn_shop_address(String address) {
        column_shop_address.set(address);
    }

    public void setColumn_shop_postcode(Integer postcode) {
        column_shop_postcode.set(postcode);
    }
    
    
}
