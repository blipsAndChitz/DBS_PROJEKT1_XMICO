package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee {
    
    private final SimpleIntegerProperty column_employee_id;
    private final SimpleStringProperty column_employee_name;
    private final SimpleStringProperty column_employee_surname;
    private final SimpleStringProperty column_employee_post;
    private final SimpleStringProperty column_employee_board;
    private final SimpleStringProperty column_employee_city;

    
    public Employee(Integer id, String name, String surname, String post, 
           String board, String city){
        
        this.column_employee_id = new SimpleIntegerProperty(id);
        this.column_employee_name = new SimpleStringProperty(name);
        this.column_employee_surname = new SimpleStringProperty(surname);
        this.column_employee_post = new SimpleStringProperty(post);
        this.column_employee_board = new SimpleStringProperty(board);
        this.column_employee_city = new SimpleStringProperty(city);  
    }

    public Integer getColumn_employee_id() {
        return column_employee_id.get();
    }

    public String getColumn_employee_name() {
        return column_employee_name.get();
    }

    public String getColumn_employee_surname() {
        return column_employee_surname.get();
    }

    public String getColumn_employee_post() {
        return column_employee_post.get();
    }

    public String getColumn_employee_board() {
        return column_employee_board.get();
    }

    public String getColumn_employee_city() {
        return column_employee_city.get();
    }

    public void setColumn_employee_id(Integer id) {
       column_employee_id.set(id);
    }

    public void setColumn_employee_name(String name) {
        column_employee_name.set(name);
    }

    public void setColumn_employee_surname(String surname) {
        column_employee_surname.set(surname);
    }

    public void setColumn_employee_post(String post) {
        column_employee_post.set(post);
    }

    public void setColumn_employee_board(String date) {
        column_employee_board.set(date);
    }

    public void setColumn_employee_city(String city) {
        column_employee_city.set(city);
    }

}
