/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides connection to the DB
 * @author Jakub
 */
public class Connector {
        
    private Connection connect = null;   
    
     /**
     * This method connects to DB
     */    
    public void connectToDbs(){
        String url = "jdbc:mysql://localhost:3306/stellarwind?useUnicode=true&characterEncoding=UTF-8";
        String username = "root";
        String password = "";

        //System.out.println("Connecting database...");

        try{
            connect = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public Connection getConnect() {
        return connect;
    }    
}
