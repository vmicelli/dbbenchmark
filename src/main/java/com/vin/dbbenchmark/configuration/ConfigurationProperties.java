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

/**
 *
 * @author Vincenzo Micelli
 */
public class ConfigurationProperties {
    public final static String NUM_OF_BATCH_INSERT_EXECUTIONS = "com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfBatchInsertExecutions"; 
    public final static String NUM_OF_INSERTS_PER_TRANSACTION= "com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfInsertStatementsPerTransaction"; 
    public final static String NUM_OF_SELECT_EXECUTIONS = "com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfSelectExecutions"; 
    public final static String NUM_OF_WARMUP_EXECUTIONS = "com.vin.dbbenchmark.manager.DbBenchmarkManager.numOfWarmupExecutions"; 
    public final static String DBMS_NAME = "com.vin.dbbenchmark.manager.DbBenchmarkManager.dbmsName";
    
    public final static String DB_SERVER_NAME = "com.vin.dbbenchmark.database.BaseDbHelper.serverName";
    public final static String DB_SERVER_PORT = "com.vin.dbbenchmark.database.BaseDbHelper.portNumber";
    public final static String DATABASE_NAME = "com.vin.dbbenchmark.database.BaseDbHelper.databaseName";
    public final static String USERNAME = "com.vin.dbbenchmark.database.BaseDbHelper.username";
    public final static String PASSWORD = "com.vin.dbbenchmark.database.BaseDbHelper.password";
}
