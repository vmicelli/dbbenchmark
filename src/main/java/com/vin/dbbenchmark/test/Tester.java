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

/**
 * Interface implemented in order to test the perfomance of a specific piece of code and provide a {@link Result}. 
 * 
 * @author Vincenzo Micelli
 */
public interface Tester {
    
    /**
     * This method execs the same test multiple times and measures the time of the executions. The statistics of collected data can be obtained calling method getResult()
     */
    void execTests();
    
    /**
     * Gets a {@link Result} object that contains the statistics of data collected during the execution of the tests.
     * The data is relative to the last call to method {@link #execTests() execTests}.
     * 
     * @return A {@link Result} object that contains the times measured by the tester during the execution. 
     */
    Result getResult();
    
    /**
     *
     * @return A string that represents a set of general info about the test executed by the tester.
     */
    String getTestInfo();
    
    /**
     *
     * @return A string that represents the name of the test executed by the tester.
     */
    String getTestName();
}
