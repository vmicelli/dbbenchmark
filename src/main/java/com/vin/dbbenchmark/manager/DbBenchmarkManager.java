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
package com.vin.dbbenchmark.manager;

import com.vin.dbbenchmark.configuration.ConfigurationHelper;
import com.vin.dbbenchmark.database.DBMSName;
import com.vin.dbbenchmark.database.DbHelper;
import com.vin.dbbenchmark.database.DbHelperFactory;
import com.vin.dbbenchmark.test.DbInsertTester;
import com.vin.dbbenchmark.test.DbSelectTester;

/**
 * This class is an implementation of a {@link BenchmarkManager} that executes insert and select tests on a database.
 * 
 * @author Vincenzo Micelli
 */
public class DbBenchmarkManager extends BenchmarkManager{

    @Override
    protected int init() {
        
        // get the configuration properties about tests to be performed
        ConfigurationHelper helper = new ConfigurationHelper();
        int numOfBatchInsertExecutions = helper.getNumberOfBatchInsertExecutions();
        int numOfInsertStatementsPerTransaction = helper.getNumberOfInsertsPerTransaction();
        int numOfSelectExecutions = helper.getNumberOfSelectExecutions();
        int numOfWarmupExecutions = helper.getNumberOfWarmupExecutions();
        DBMSName dbmsName = helper.getDbmsName();
        
        // if dbms has not been properly specified in the configuration file it is not possible to execute the tests.
        if(dbmsName == null)
            return -1;
        
        // get db helper 
        DbHelper dbHelper = DbHelperFactory.getDbHelper(dbmsName);
        
        if(dbHelper.connect() != 0)
        {
            return -2;
        }

        //create the table that will be used for the tests. 
        int createTableResult = dbHelper.createTable();
        dbHelper.closeConnection();
        
        // if there were errors during the creation of the table it is not possible to execute the tests.
        if(createTableResult != 0)
            return -3;
        
        // add the tester the will perform insert statements tests
        DbInsertTester insertTester = new DbInsertTester(dbmsName,numOfBatchInsertExecutions,numOfInsertStatementsPerTransaction,numOfWarmupExecutions);
        addTester(insertTester);

        // add the tester the will perform select statements tests
        DbSelectTester selectTester = new DbSelectTester(dbmsName,numOfSelectExecutions,numOfWarmupExecutions);
        addTester(selectTester);

        return 0;
    }

    @Override
    protected void finish() {
        // we could drop the table here but we are not doing it so that users can check out the records that have been inserted
    }
    

}
