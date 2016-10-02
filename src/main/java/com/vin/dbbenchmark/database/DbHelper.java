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

import com.vin.dbbenchmark.configuration.ConfigurationHelper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a helper abstract class that implements the main methods to exec statements on both PostgreSQL and SQL Server.
 * The helper can open/close connection to a database, create a test table, exec insert batch and select statements by pk.
 * The method {@link #connect() connect}, {@link #createTable() createTable} and {@link #closeConnection() closeConnection} 
 * has to be implemented to complete the helper for a specific database.
 * 
 * In order to execute the statements, the helper provides 4 methods per statement. One to prepare the statement, one to set the data,
 * one to exec the statement and commit, and one to close the statement. This is done so that the db tester objects can decide whether
 * to benchmark all the 4 operations or just part of them depending on the methods that are called in the execTest() function of the
 * tester. 
 * 
 * @author Vincenzo Micelli
 */
public abstract class DbHelper{
    
    // table name and fields names for the test table

    /**
     * table name for the table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String TABLE_NAME = "test_table";
    
    /**
     * name for the primary key of table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String COLUMN_PK_NAME = "id";
    
    /**
     * name for the varchar column of table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String COLUMN_VARCHAR_NAME = "test_column_varchar";
    
    /**
     * name for the int column of table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String COLUMN_INT_NAME = "test_column_int";
    
    /**
     * name for the decimal column of table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String COLUMN_DECIMAL_NAME = "test_column_decimal";
    
    /**
     * name for the date column of table that has to be created by {@link #createTable() createTable} method
     */
    protected static final String COLUMN_DATE_NAME = "test_column_date";
    
    
    protected Connection connection;
    protected String username;
    protected String password;
    protected String serverName;
    protected String databaseName;
    protected String portNumber;
    
    private PreparedStatement insertDataBatchPreparedStatement;
    private PreparedStatement selectDataPreparedStatement;
    
    public DbHelper()
    {
        //get parameters for connection from configuration file
        ConfigurationHelper helper = new ConfigurationHelper();
        serverName = helper.getDbServerName();
        portNumber = helper.getDbServerPortNumber();
        databaseName = helper.getDatabaseName();
        username = helper.getUsername();
        password = helper.getPassword();
        
    }
    
    /**
     * An entry object for the test table
     */
    public static class DbEntry
    {
        private int primaryKey;
        private String varcharField;
        private int intField;
        private BigDecimal decimalField;
        private Timestamp dateField;

        public int getPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(int primaryKey) {
            this.primaryKey = primaryKey;
        }

        public String getVarcharField() {
            return varcharField;
        }

        public void setVarcharField(String varcharField) {
            this.varcharField = varcharField;
        }

        public int getIntField() {
            return intField;
        }

        public void setIntField(int intField) {
            this.intField = intField;
        }

        public BigDecimal getDecimalField() {
            return decimalField;
        }

        public void setDecimalField(BigDecimal decimalField) {
            this.decimalField = decimalField;
        }

        public Timestamp getDateField() {
            return dateField;
        }

        public void setDateField(Timestamp dateField) {
            this.dateField = dateField;
        }
        
    }
    
    
    /**
     * Connect to the database.
     * 
     * @return 0 if the connection has been established. A negative number if an error has occurred.
     */
    public abstract int connect();

    /**
     * Close the connection to the database
     */
    public abstract void closeConnection();
    
    /**
     * Create the test table (drop the table if exists).
     * The name of the table must be the value of the static field {@code TABLE_NAME}.
     * The table has to have the following columns:
     *  - integer not null PK column named as the value of the static field {@code COLUMN_PK_NAME}.
     *  - varchar not null column named as the value of the static field {@code COLUMN_VARCHAR_NAME}.
     *  - integer not null column named as the value of the static field {@code COLUMN_INT_NAME}.
     *  - decimal not null column named as the value of the static field {@code COLUMN_DECIMAL_NAME}.
     *  - date not null column named as the value of the static field {@code COLUMN_DATE_NAME}.
     * 
     * @return 0 if the connection has been established. A negative number if an error has occurred.
     */
    public abstract int createTable();
    
    /**
     * Prepare a statement to insert records in the table created by {@link #createTable() createTable} method.
     */
    public void prepareInsertStatement() {
        
        if(insertDataBatchPreparedStatement != null)
            closeInsertStatement();
        
        try {
            String insertTableSQL = "INSERT INTO " + TABLE_NAME 
                    + "(" + COLUMN_VARCHAR_NAME + ", " + COLUMN_INT_NAME + ", " + COLUMN_DECIMAL_NAME + ", " + COLUMN_DATE_NAME + ") VALUES"
                    + "(?,?,?,?)";
            
            insertDataBatchPreparedStatement = connection.prepareStatement(insertTableSQL);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param entries The new records to insert.
     */
    public void setInsertDataBatch(List<DbEntry> entries) {

        if(entries == null)
        {
            throw new IllegalArgumentException("entries parameter cannot be null");
        }
        
        try {
            connection.setAutoCommit(false);
            
            //just in case we end up in some dirty state (this should not be needed).
            insertDataBatchPreparedStatement.clearBatch();
            
            for(DbEntry entry : entries)
            {
                insertDataBatchPreparedStatement.setString(1, entry.getVarcharField());
                insertDataBatchPreparedStatement.setInt(2, entry.getIntField());
                insertDataBatchPreparedStatement.setBigDecimal(3, entry.getDecimalField());
                insertDataBatchPreparedStatement.setTimestamp(4, entry.getDateField());
                insertDataBatchPreparedStatement.addBatch();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Exec the insert batch and commit.
     * 
     * @throws SQLException Throws sql exceptions
     */
    public void execInsertDataBatch() throws SQLException  {
        try {
            insertDataBatchPreparedStatement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Close the prepared statement for insert.
     */
    public void closeInsertStatement() {
        try {
            insertDataBatchPreparedStatement.close();
            insertDataBatchPreparedStatement = null;
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getMaxPrimaryKeyValue() {
        
        int maxPrimaryKeyValue = 0;
        
        try {
            String selectSQL = "SELECT MAX(" + COLUMN_PK_NAME + ") FROM " + TABLE_NAME;
            PreparedStatement selectMaxPrimaryKeyPreparedStatement = connection.prepareStatement(selectSQL);
            
            connection.setAutoCommit(true);
            ResultSet rs = selectMaxPrimaryKeyPreparedStatement.executeQuery();
             
            if(rs.next())
            {
                maxPrimaryKeyValue = rs.getInt(1);
            }
            
            rs.close();
            selectMaxPrimaryKeyPreparedStatement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return maxPrimaryKeyValue;
        
    }

    /**
     * Prepare a statement to select records from the table created by {@link #createTable() createTable} method. 
     * 
     */
    public void prepareSelectStatement() {
        if(selectDataPreparedStatement != null)
            closeSelectStatement();
        
        try {
            String selectSQL = "SELECT " + COLUMN_PK_NAME + ", " + COLUMN_VARCHAR_NAME + ", " + COLUMN_INT_NAME + ", " + COLUMN_DECIMAL_NAME + ", " + COLUMN_DATE_NAME 
                             + " FROM " + TABLE_NAME + " WHERE " + COLUMN_PK_NAME + " = ?";
            
            selectDataPreparedStatement = connection.prepareStatement(selectSQL);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param primaryKey The primary key to use for data selection
     */
    public void setSelectDataPK(int primaryKey) {
        try {
            connection.setAutoCommit(true);
            selectDataPreparedStatement.setInt(1, primaryKey);
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Exec the select statement.
     * @return The result of the select query.
     */
    public ResultSet execSelectData() {
        try {
            // execute select SQL stetement
            ResultSet rs = selectDataPreparedStatement.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    /**
     * Close the prepared statement for select.
     */
    public void closeSelectStatement() {
        try {
            selectDataPreparedStatement.close();
            selectDataPreparedStatement = null;
        } catch (SQLException ex) {
            Logger.getLogger(DbHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
