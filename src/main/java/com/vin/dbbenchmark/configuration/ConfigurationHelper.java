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
package com.vin.dbbenchmark.configuration;

import com.vin.dbbenchmark.database.DBMSName;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vincenzo Micelli
 */
public class ConfigurationHelper {
    
    private static final int DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS = 100;
    private static final int DEFAULT_NUM_OF_INSERT_PER_TRANSACTION = 10;
    private static final int DEFAULT_NUM_OF_SELECT_EXECUTIONS = 100;
    private static final int DEFAULT_NUM_OF_WARMUP_EXECUTIONS = 5;
    
    private static final String CONFIGURATION_FILE = "configuration.properties";
    
    private Properties properties = new Properties();
    
    public ConfigurationHelper()
    {
        String resourceName = CONFIGURATION_FILE;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        properties = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            properties.load(resourceStream);
        } catch (IOException ex) {
            
            String error = "Failed to load configuration properties.";
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.SEVERE, error, ex);
        }

    }
    
    /**
     *
     * @return The num of batch insert execution from the configuration file. 
     * If the property has not been set or has been set to 0, return default value.
     */
    public int getNumberOfBatchInsertExecutions()
    {
        if(properties == null)
            return DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS;
            
        int numOfExecutions;
        
        String numOfExecutionsProp = properties.getProperty(ConfigurationProperties.NUM_OF_BATCH_INSERT_EXECUTIONS);

        try {
            numOfExecutions = Integer.parseInt(numOfExecutionsProp);
        } catch (NumberFormatException numberFormatException) {
            numOfExecutions = 0;
        }
        
        if(numOfExecutions <= 0)
        {
            String warning = "Invalid input for property " + ConfigurationProperties.NUM_OF_BATCH_INSERT_EXECUTIONS + ".\n" +
                             "Input value is not a positive number: " + numOfExecutionsProp + ".\n" +
                             "Using default value " + DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS + "\n\n";
            
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.WARNING, warning);
            
            numOfExecutions = DEFAULT_NUM_OF_BATCH_INSERT_EXECUTIONS;
        }
        
        return numOfExecutions;
    }
    
    /**
     *
     * @return The num of inserts per transaction from the configuration file. 
     * If the property has not been set or has been set to 0, return default value.
     */
    public int getNumberOfInsertsPerTransaction()
    {
        if(properties == null)
            return DEFAULT_NUM_OF_INSERT_PER_TRANSACTION;
        
        int numOfExecutions;
            
        String numOfExecutionsProp = properties.getProperty(ConfigurationProperties.NUM_OF_INSERTS_PER_TRANSACTION);

        try {
            numOfExecutions = Integer.parseInt(numOfExecutionsProp);
        } catch (NumberFormatException numberFormatException) {
            numOfExecutions = 0;
        }
        
        if(numOfExecutions <= 0)
        {
            String warning = "Invalid input for property " + ConfigurationProperties.NUM_OF_INSERTS_PER_TRANSACTION + ".\n" +
                             "Input value is not a positive number: " + numOfExecutionsProp + ".\n" +
                             "Using default value " + DEFAULT_NUM_OF_INSERT_PER_TRANSACTION + "\n\n";
            
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.WARNING, warning);
            
            numOfExecutions = DEFAULT_NUM_OF_INSERT_PER_TRANSACTION;
        }
        
        return numOfExecutions;
    }
    
    public int getNumberOfSelectExecutions()
    {
        if(properties == null)
            return DEFAULT_NUM_OF_SELECT_EXECUTIONS;
            
        int numOfExecutions;
        
        String numOfExecutionsProp = properties.getProperty(ConfigurationProperties.NUM_OF_SELECT_EXECUTIONS);

        try {
            numOfExecutions = Integer.parseInt(numOfExecutionsProp);
        } catch (NumberFormatException numberFormatException) {
            numOfExecutions = 0;
        }
        
        if(numOfExecutions <= 0)
        {
            String warning = "Invalid input for property " + ConfigurationProperties.NUM_OF_SELECT_EXECUTIONS + ".\n" +
                             "Input value is not a positive number: " + numOfExecutionsProp + ".\n" +
                             "Using default value " + DEFAULT_NUM_OF_SELECT_EXECUTIONS + "\n\n";
            
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.WARNING, warning);
            
            numOfExecutions = DEFAULT_NUM_OF_SELECT_EXECUTIONS;
        }
        
        return numOfExecutions;
    }
    
    public int getNumberOfWarmupExecutions()
    {
        if(properties == null)
            return DEFAULT_NUM_OF_WARMUP_EXECUTIONS;
        
        int numOfExecutions;
        
        String numOfExecutionsProp = properties.getProperty(ConfigurationProperties.NUM_OF_WARMUP_EXECUTIONS);

        try {
            numOfExecutions = Integer.parseInt(numOfExecutionsProp);
        } catch (NumberFormatException numberFormatException) {
            numOfExecutions = -1;
        }
        
        if(numOfExecutions < 0)
        {
            String warning = "Invalid input for property " + ConfigurationProperties.NUM_OF_WARMUP_EXECUTIONS + ".\n" +
                             "Input value is not a number: " + numOfExecutionsProp + ".\n" +
                             "Using default value " + DEFAULT_NUM_OF_WARMUP_EXECUTIONS + "\n\n";
            
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.WARNING, warning);
            
            numOfExecutions = DEFAULT_NUM_OF_WARMUP_EXECUTIONS;
        }
        
        return numOfExecutions;
    }
    
    public DBMSName getDbmsName()
    {
        if(properties == null)
            return null;
        
        String dbmsNameProp = properties.getProperty(ConfigurationProperties.DBMS_NAME,"");
        
        DBMSName dbmsName = null;
        try {
            dbmsName = DBMSName.valueOf(dbmsNameProp);
        } catch (Exception e) {
            String error = "Invalid input for property " + ConfigurationProperties.DBMS_NAME + ": " + dbmsNameProp + ".\n\n";
            Logger.getLogger(ConfigurationHelper.class.getName()).log(Level.SEVERE, error);
        }
        
        return dbmsName;
    }
    
    public String getDbServerName()
    {
        if(properties == null)
            return null;
        
        String prop = properties.getProperty(ConfigurationProperties.DB_SERVER_NAME,"");

        return prop;
    }
    
    public String getDbServerPortNumber()
    {
        if(properties == null)
            return null;
        
        String prop = properties.getProperty(ConfigurationProperties.DB_SERVER_PORT,"");

        return prop;
    }
    
    public String getDatabaseName()
    {
        if(properties == null)
            return null;
        
        String prop = properties.getProperty(ConfigurationProperties.DATABASE_NAME,"");

        return prop;
    }
    
    public String getUsername()
    {
        if(properties == null)
            return null;
        
        String prop = properties.getProperty(ConfigurationProperties.USERNAME,"");

        return prop;
    }
    
    public String getPassword()
    {
        if(properties == null)
            return null;
        
        String prop = properties.getProperty(ConfigurationProperties.PASSWORD,"");

        return prop;
    }
    
}
