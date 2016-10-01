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

import com.vin.dbbenchmark.database.DbHelper;
import com.vin.dbbenchmark.database.DBMSName;
import com.vin.dbbenchmark.database.DbHelperFactory;

/**
 * This is a BaseTester that implements {@link #init() init} and {@link #finish() finish} methods to handle the connection to the database (open/close).
 * This Class can be subclassed to perform generic tests on databases.
 * 
 * @author Vincenzo Micelli
 */
public abstract class DbTester extends BaseTester{
    
    private final DBMSName dbmsName;
    
    /**
     * This {@link DbHelper} can be used by subclasses to execute statements on the database
     */
    protected DbHelper dbHelper;
    
    /**
     *
     * @param dbmsName It is the name of the database to be used. It is an enum and can have values POSTGRE_SQL and SQL_SERVER
     * @param numOfExecutions It is the number of times that the {@link #execTest() execTest} method is executed in order to collect statistical results
     * @param numOfWarmupExecutions It is the number of times that the {@link #execTest() execTest} method is executed for warmup purposes
     */
    public DbTester(DBMSName dbmsName, int numOfExecutions,int numOfWarmupExecutions) {
        super(numOfExecutions,numOfWarmupExecutions);
        this.dbmsName = dbmsName;
    }
    
    @Override
    protected int init()
    {
        dbHelper = DbHelperFactory.getDbHelper(dbmsName);
        int connectionResult = dbHelper.connect();
        
        return connectionResult;
    }

    @Override
    protected void finish()
    {
        dbHelper.closeConnection();
    }
    
}
