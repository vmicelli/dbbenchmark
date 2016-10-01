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
import com.vin.dbbenchmark.database.DbHelper.DbEntry;
import com.vin.dbbenchmark.utils.CommonUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Tester executes insert statements on a database (through the property {@code dbHelper}). 
 * The statements are committed to the database in blocks (Batches) of fixed size. The size of the blocks is given by parameter {@code numInsertsPerTransaction} that is passed to the constructor.
 * The tester evaluate the min, max and avg execution times of the batches.
 * In addition the tester provides the min, max and avg times to insert a single record.
 * 
 * @author Vincenzo Micelli
 */
public class DbInsertTester extends DbTester {

    private final int numInsertsPerTransaction;
    
    /**
     *
     * @param dbmsName It is the name of the database to be used. It is an enum and can have values POSTGRE_SQL and SQL_SERVER
     * @param numOfBatchInsertExecutions It is the number of times that the Batch Insert is executed in order to collect statistical results 
     * @param numInsertsPerTransaction It is the number of inserts that are performed with a single batch
     * @param numOfWarmupExecutions It is the number of times that the Batch Insert is executed for warmup purposes
     */
    public DbInsertTester(DBMSName dbmsName, int numOfBatchInsertExecutions, int numInsertsPerTransaction, int numOfWarmupExecutions) {
        super(dbmsName,numOfBatchInsertExecutions,numOfWarmupExecutions);
        this.numInsertsPerTransaction = numInsertsPerTransaction;
    }
    
    @Override
    protected int init()
    {
        int result = super.init();
        
        if(result != 0 )
            return result;
        
        //Obtain the prepared statement object from the connection.
        //This tester obtains the prepared statement object at the beginning and uses it for each test execution.
        //(
        // If you want to prepare the statement before every insert you can move the call to prepareInsertStatement to method beforeTest(). 
        // Or, if you want to include the time needed for this operation in the statistics, you can move this to the method execTest().
        // Of course if you move this call you have to be sure to call the following methods accordingly:
        //    - dbHelper.setInsertDataBatch(numInsertsPerTransaction) 
        //    - dbHelper.closeInsertStatement()
        // The correct sequence to perform the batch insert is:
        //    1 dbHelper.prepareInsertStatement()
        //    2 dbHelper.setInsertDataBatch(numInsertsPerTransaction)
        //    3 dbHelper.execInsertDataBatch();
        //    (.. Step 2 and 3 can be repeated multiple times before closing the statement ..)
        //    4 dbHelper.closeInsertStatement();
        //)
        dbHelper.prepareInsertStatement();
        
        return result;
    }
    
    @Override
    protected void beforeTest(State state) {
        
        List<DbEntry> entries = new ArrayList<>();
        
        //init data for insert batch
        for(int i = 0; i < numInsertsPerTransaction; i++ )
        {
            DbEntry entry = new DbEntry();
            entry.setVarcharField(CommonUtils.getRandomString(20));
            entry.setIntField(CommonUtils.getRandomInt(100000));
            entry.setDecimalField(CommonUtils.getRandomBigDecimal(100000, 2));
            entry.setDateField(CommonUtils.getCurrentTimeStamp());
            
            entries.add(entry);
        }
        
        //set data for the inserts with random values 
        //(if you want to include the time needed for this operation in the statistics, you can move this to the method execTest().
        // Also note that to move this line in executeTest, it would be a good idea to exetend the state object and pass the entries through it.
        // For more info about how to extend the state object read its documentation.)
        dbHelper.setInsertDataBatch(entries);
    }
    
    @Override
    protected void execTest(State state) {
        try {
            //we evaluate the time needed to execute the batch and to commit (using the PreparedStatement API)
            dbHelper.execInsertDataBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DbInsertTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void afterTest(State state) {
    }
    
     @Override
    protected void finish()
    {
        //close the prepared statement
        dbHelper.closeInsertStatement();
        
        super.finish();
    }

    @Override
    public String getTestName() {
        return "Insert Statements";
    }
    
    @Override
    public String getTestInfo() {
        
        String info = "Warmup Executions: " + this.getNumOfWarmupExecutions() + "\n" + 
                      "Batch Insert Executions: " + this.getNumOfExecutions() + "\n" + 
                      "Num of inserts per batch: " + numInsertsPerTransaction + "\n";
        
        return info;
    }
    
    @Override
    public Result getResult() {
        Result result = new Result();
        
        if(this.getNumOfWarmupExecutions() > 0 && numInsertsPerTransaction > 0)
        {
            result.put("min-warmup (to insert a batch of records)", this.getWarmupMinTime());
            result.put("max-warmup (to insert a batch of records)", this.getWarmupMaxTime());
            result.put("avg-warmup (to insert a batch of records)", this.getWarmupAvgTime());
        }
        
        if(this.getNumOfExecutions() > 0)
        {
            result.put("min (to insert a batch of records)", this.getMinTime());
            result.put("max (to insert a batch of records)", this.getMaxTime());
            result.put("avg (to insert a batch of records)", this.getAvgTime());

            long minTimePerRecord = 0;
            long maxTimePerRecord = 0;
            long avgTimePerRecord = 0;

            if(numInsertsPerTransaction > 0 && numInsertsPerTransaction > 0)
            {
                //compute the cost per single insert in the bacth with min time
                minTimePerRecord = this.getMinTime()/numInsertsPerTransaction;
                //compute the cost per single insert in the bacth with max time
                maxTimePerRecord = this.getMaxTime()/numInsertsPerTransaction;
                //compute the avarage cost per single insert 
                avgTimePerRecord = this.getAvgTime()/numInsertsPerTransaction;
            }

            result.put("time per record in the bacth with min time", minTimePerRecord);
            result.put("time per record in the bacth with max time", maxTimePerRecord);
            result.put("avg (to insert a record)", avgTimePerRecord);
        }

        return result;
    }
 
}
