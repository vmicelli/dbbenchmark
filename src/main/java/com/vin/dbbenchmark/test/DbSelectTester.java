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
package com.vin.dbbenchmark.test;

import com.vin.dbbenchmark.database.DBMSName;
import com.vin.dbbenchmark.utils.CommonUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Tester executes select statements on a database (through the property {@code dbHelper}). 
 * The tester evaluate the min, max and avg execution times to select a single record by its primary key.
 * 
 * @author Vincenzo Micelli
 */
public class DbSelectTester extends DbTester {

    private int maxPrimaryKeyValue;
    
    /**
     *
     * @param dbmsName It is the name of the database to be used. It is an enum and can have values POSTGRE_SQL and SQL_SERVER
     * @param numOfExecutions It is the number of times that the select statementt is executed in order to collect statistical results 
     * @param numOfWarmupExecutions It is the number of times that select statement is executed for warmup purposes
     */
    public DbSelectTester(DBMSName dbmsName, int numOfExecutions,int numOfWarmupExecutions) {
        super(dbmsName,numOfExecutions,numOfWarmupExecutions);
    }

    @Override
    protected int init()
    {
        int result = super.init();
        
        if(result != 0 )
            return result;
        //the helper reads from db the max primary key value in the database 
        //(max primary key value is used by the dbHelper to compute a random key to select) 
        maxPrimaryKeyValue = dbHelper.getMaxPrimaryKeyValue();
        
        //Obtain the prepared statement object from the connection.
        //This tester obtains the prepared statement object at the beginning and uses it for each test execution.
        //(
        // If you want to prepare the statement before every select you can move the call of prepareSelectStatement to method beforeTest(). 
        // Or, if you want to include the time needed for this operation in the statistics, you can move this to the method execTest().
        // Of course if you move this call you have to be sure to call the following methods accordingly:
        //    - dbHelper.setSelectDataPK() 
        //    - dbHelper.closeSelectStatement()
        // The correct sequence to perform to select a record is:
        //    1 dbHelper.prepareSelectStatement()
        //    2 dbHelper.setSelectDataPK()
        //    3 dbHelper.execSelectData();
        //    (.. Step 2 and 3 can be repeated multiple times before closing the statement ..)
        //    4 dbHelper.closeSelectStatement();
        //)
        dbHelper.prepareSelectStatement();
        
        return result;
    }
    
    @Override
    protected void beforeTest(State state) {
        
        int primaryKeyValue = 0;
        
        // here we assume that primary key values currently in the database are all the values from 1 to maxPrimaryKeyValue 
        // (they have been inserted during the insert test)
        if(maxPrimaryKeyValue > 0)
           primaryKeyValue = CommonUtils.getRandomInt(maxPrimaryKeyValue);
        
        //set the pk to use for the select statement 
        //(if you want to include the time needed for this operation in the statistics, you can move this to the method execTest()
        // Also note that to move this line in executeTest, it would be a good idea to exetend the state object and pass the primaryKeyValue through it.
        // For more info about how to extend the state object read its documentation.)
        dbHelper.setSelectDataPK(primaryKeyValue);
    }
    
    @Override
    protected void execTest(State state) {

        //we evaluate the time needed to exec the select statement
        ResultSet resultSet = dbHelper.execSelectData();
        ((ResultSetState)state).setResultSet(resultSet);
    }

    @Override
    protected void afterTest(State state) {
        
        try {
            ResultSet resultSet = ((ResultSetState)state).getResultSet();
            
            // close the result set
            if(resultSet != null)
                resultSet.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DbSelectTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
     @Override
    protected void finish()
    {
        //close the prepared statement
        dbHelper.closeSelectStatement();    
        super.finish();
    }

    @Override
    public String getTestName() {
        return "Select Statements by PK";
    }
    
    @Override
    public State makeState()
    {
        return new ResultSetState();
    }
    
    private static class ResultSetState extends State
    {
        private ResultSet resultSet;

        public ResultSet getResultSet() {
            return resultSet;
        }

        public void setResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        
    }
    
}
