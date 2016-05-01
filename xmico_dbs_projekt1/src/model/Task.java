/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Jakub
 */
public class Task {
    
    private final SimpleStringProperty column_task_city;
    private final SimpleStringProperty column_task_name;
    private final SimpleStringProperty column_task_date;
    private String task;
    private String hashName;

    public Task(String column_task_name, String column_task_city, String column_task_date, String task, String hashName) {
        this.column_task_city = new SimpleStringProperty(column_task_city);
        this.column_task_name = new SimpleStringProperty(column_task_name);
        this.column_task_date = new SimpleStringProperty(column_task_date);
        this.task = task;
        this.hashName = hashName;
    }

    public String getColumn_task_city() {
        return column_task_city.get();
    }

    public String getColumn_task_name() {
        return column_task_name.get();
    }

    public String getColumn_task_date() {
        return column_task_date.get();
    }

    public String getTask() {
        return task;
    }

    public String getHashName() {
        return hashName;
    }    
    
    public void setColumn_task_city(String city) {
        column_task_city.set(city);
    }

    public void getColumn_task_name(String name) {
        column_task_name.set(name);
    }

    public void getColumn_task_date(String date) {
        column_task_date.set(date);
    }

    public void getTask(String task1) {
        task=task1;
    }

    public void setHashName(String hashName) {
        this.hashName = hashName;
    }
    
    
    
    
}
