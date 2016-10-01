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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the {@link Tester} interface. This implementation executes the same test for a specific number of iterations 
 * and evaluate the min, max and average times (in nanoseconds). In addition, before starting the actual tests, the tester performs a specific number 
 * of warmup iterations to provide more reliable results for the actual iterations.
 * 
 * @author Vincenzo Micelli
 */
public abstract class BaseTester implements Tester{

    private final int numOfExecutions;
    private final int numOfWarmupExecutions;
    private List<Long> warmupExecutionTimes;
    private List<Long> executionTimes;
   
    private long warmupMinTime;
    private long warmupMaxTime;
    private long warmupAvgTime;
    private long minTime;
    private long maxTime;
    private long avgTime;
    
    public enum IterationType{
        WARMUP,
        TEST_EXECUTION
    }
    
    /**
     *
     * @param numOfExecutions It is the number of times that the {@link #execTest() execTest} method is executed in order to collect statistical results
     * @param numOfWarmupExecutions It is the number of times that the {@link #execTest() execTest} method is executed for warmup purposes
     */
    public BaseTester(int numOfExecutions,int numOfWarmupExecutions)
    {
        initTimeProperties();
        this.numOfExecutions = numOfExecutions;
        this.numOfWarmupExecutions = numOfWarmupExecutions;
    }
    
    private void initTimeProperties()
    {
        this.avgTime = 0;
        this.maxTime = 0;
        this.minTime = Long.MAX_VALUE;
        this.warmupAvgTime = 0;
        this.warmupMaxTime = 0;
        this.warmupMinTime = Long.MAX_VALUE;
        this.executionTimes = new ArrayList<>();
        this.warmupExecutionTimes = new ArrayList<>();
    }

    public int getNumOfExecutions() {
        return numOfExecutions;
    }

    public int getNumOfWarmupExecutions() {
        return numOfWarmupExecutions;
    }

    public List<Long> getWarmupExecutionTimes() {
        return warmupExecutionTimes;
    }

    public List<Long> getExecutionTimes() {
        return executionTimes;
    }

    public long getWarmupMinTime() {
        return warmupMinTime;
    }

    public long getWarmupMaxTime() {
        return warmupMaxTime;
    }

    public long getWarmupAvgTime() {
        return warmupAvgTime;
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public long getAvgTime() {
        return avgTime;
    }
    
    /**
     * Subclasses have to implement this method to init the resources that 
     * are needed for the tests.
     * 
     * @return 0 if the inizialization is completed successfully. A number different than zero otherwise.
     */
    protected abstract int init();
    
    /**
     * Subclasses have to implement this method to free the resources used during the tests (if needed).
     * 
     */
    protected abstract void finish();
    
    /**
     * This method can be implemented to execute some operations before every test (it is executed before every iteration).
     * 
     * @param state The state object of the tests execution. 
     */
    protected abstract void beforeTest(State state);
    
    /**
     * The code executed by this method represents the piece of code that is being tested. The tester measures the execution times of this method and provide statistical results. 
     * 
     * @param state The state object of the tests execution.
     */
    protected abstract void execTest(State state);
    
    /**
     * This method can be implemented to execute some operations after every test (it is executed after every iteration).
     * 
     * @param state The state object of the tests execution.
     */
    protected abstract void afterTest(State state);
    
    
    @Override
    public void execTests()  {    
        
        // try to init the tester
        int initResult = init();
        
        if(initResult != 0)
        {
            String error = "BaseTester initialization failed.\n\n";
            Logger.getLogger(BaseTester.class.getName()).log(Level.SEVERE, error);
            
            return;
        }
        
        //init times
        initTimeProperties();

        State state = makeState();
        
        long totalWarmupTime = 0;
        state.setIterationType(IterationType.WARMUP);
        
        // exec warmup iterations
        for(int i=0; i< numOfWarmupExecutions; i++)
        {
            state.setIterationNumber(i+1);
            
            //code to be executed before every test implemented by subclasses 
            beforeTest(state);
            
            
            // exec test and measure execution time
            long startTime = System.nanoTime();
            execTest(state);
            long executionTime = System.nanoTime() - startTime;
            
            //code to be executed after every test implemented by subclasses 
            afterTest(state);
            
            // add execution time to warm up execution times
            warmupExecutionTimes.add(executionTime);
            
            // check if the time of the current execution is a min or max time
            if(executionTime < warmupMinTime) 
                warmupMinTime = executionTime;
            if(executionTime > warmupMaxTime)
                warmupMaxTime = executionTime;
                
            // add current execution time to total warmup time
            totalWarmupTime += executionTime;
        }
        
        // compute average execution time for warm up iterations
        if(numOfWarmupExecutions > 0)
            warmupAvgTime = totalWarmupTime/numOfWarmupExecutions;

        long totalIterationsTime = 0;
        state.setIterationType(IterationType.TEST_EXECUTION);
        
        for(int i=0; i< numOfExecutions; i++)
        {
            state.setIterationNumber(i+1);
            
            //code to be executed before every test implemented by subclasses  
            beforeTest(state);
            
            // exec test and measure execution time
            long startTime = System.nanoTime();    
            execTest(state);
            long end = System.nanoTime(); 
            long executionTime = end - startTime;

            //code to be executed after every test implemented by subclasses
            afterTest(state);
            
            // add execution to the list
            executionTimes.add(executionTime);
            
            // check if the time of the current execution is a min or max time
            if(executionTime < minTime) 
                minTime = executionTime;
            if(executionTime > maxTime)
                maxTime = executionTime;
                
            // add current execution time to total iterations time
            totalIterationsTime += executionTime;
        }
        
        // compute average execution time
        if(numOfExecutions > 0)
            avgTime = totalIterationsTime/numOfExecutions;

        // free resources
        finish();        
    }

    @Override
    public Result getResult() {
        Result result = new Result();
        
        if(numOfWarmupExecutions > 0)
        {
            result.put("min-warmup", warmupMinTime);
            result.put("max-warmup", warmupMaxTime);
            result.put("avg-warmup", warmupAvgTime);
        }
        
        if(numOfExecutions > 0)
        {
            result.put("min", minTime);
            result.put("max", maxTime);
            result.put("avg", avgTime);
        }

        return result;
    }
    
    @Override
    public String getTestInfo() {
        
        String info = "Warmup Executions: " + numOfWarmupExecutions + "\n" + 
                      "Executions: " + numOfExecutions + "\n";
        
        return info;
    }
    
    protected State makeState()
    {
        return new State();
    }
    
    /**
     * This state is passed through methods {@link #beforeTest() beforeTest}, {@link #execTest() execTest} and {@link #afterTest() afterTest}.
     * Subclasses can extend this object to add other attributes to pass through the 3 methods.
     * {@link BaseTester} has the factory {@link #makeState() makeState} that subclasses can override to create their own state object.  
     * 
     */
    public static class State
    {
        private int iterationNumber;
        private IterationType iterationType;

        public State() {
            this.iterationType = IterationType.WARMUP;
            this.iterationNumber = 0;
        }

        public int getIterationNumber() {
            return iterationNumber;
        }

        public void setIterationNumber(int iterationNumber) {
            this.iterationNumber = iterationNumber;
        }

        public IterationType getIterationType() {
            return iterationType;
        }

        public void setIterationType(IterationType iterationType) {
            this.iterationType = iterationType;
        }
        
        
    }
    
}
