/*
 * Copyright 2016 Vincenzo Micelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vin.dbbenchmark.database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This helper class extends {@link DbHelper} to implement methods to open and close a connection to PostgreSql and create a test table.
 * 
 * @author Vincenzo Micelli
 */
public class PostgreSqlDbHelper extends DbHelper {

    @Override
    public int connect() {
        
        if(connection != null)
            return 0;
        
        try {
            // Create a variable for the connection string.
            String connectionUrl = "jdbc:postgresql://" + serverName + ":" + portNumber + "/" + databaseName;
            
            // Establish the connection.
            Class.forName("org.postgresql.Driver");  
            connection = DriverManager.getConnection(connectionUrl,username,password);
            
            return 0;
 
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(PostgreSqlDbHelper.class.getName()).log(Level.SEVERE, null, ex);
            
            return - 1;
        }

    }

    @Override
    public int createTable() {
        int result = 0;
        
        String dropTableSQL = "  DROP TABLE IF EXISTS " + TABLE_NAME;
                
        try (PreparedStatement dropTablePreparedStatement = connection.prepareStatement(dropTableSQL)) {

            connection.setAutoCommit(true);
            // execute drop SQL stetement
            dropTablePreparedStatement.executeUpdate();
            
        } catch (SQLException  ex) {
            Logger.getLogger(PostgreSqlDbHelper.class.getName()).log(Level.SEVERE, null, ex);
            result = -1;
        }
        
        if(result < 0)
            return result;
        
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_PK_NAME + " SERIAL PRIMARY KEY, "
                    + COLUMN_VARCHAR_NAME + " VARCHAR(20) NOT NULL, "
                    + COLUMN_INT_NAME + " INTEGER NOT NULL, "
                    + COLUMN_DECIMAL_NAME + " DECIMAL(9,2) NOT NULL, "
                    + COLUMN_DATE_NAME + " DATE NOT NULL "
                    + ")";
        
        try (PreparedStatement createTablePreparedStatement = connection.prepareStatement(createTableSQL)) {

            // execute create SQL stetement
            createTablePreparedStatement.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(PostgreSqlDbHelper.class.getName()).log(Level.SEVERE, null, ex);
            result = -1;
        }
        
        return result;
    }

    @Override
    public void closeConnection() {
        try {
            
            if(connection != null)
            {
                connection.close();
                connection = null;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PostgreSqlDbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
