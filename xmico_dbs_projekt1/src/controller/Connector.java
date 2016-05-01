/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import redis.clients.jedis.Jedis;

/**
 * Provides connection to the DB
 * @author Jakub
 */
public class Connector {
        
    private Connection connect = null;   
    private  Jedis jedis = null;
    
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

    public void connectToRedisDbs(){      
      
        try{
            jedis = new Jedis("localhost");
            System.out.println("Connection to server sucessfully");
            jedis.select(1);
            System.out.println("Server is running: "+jedis.ping());  
        }
        catch(Exception ex){
            System.out.println("Jedis is down");
            System.exit(1);
        }
    }
    
    public Connection getConnect() {
        return connect;
    }

    public Jedis getRedisConnect() {
        return jedis;
    } 
    
}
