/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vin.dbbenchmark.test;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Vin
 */
public class BaseTesterTest {
    
    public BaseTesterTest() {
    }
    
   


    /**
     * This is just a sample test that verifies that BaseTester collect execution times after execTests() call.
     * It also verifies that the number of collected times is correct if the method is executed multiple times. 
     */
    @Test
    public void testExecTestsCollectExecutionTimes() {
        
        int numOfWarmupExecutions = 20;
        int numOfExecutions = 100;
        
        BaseTester instance = new BaseTesterImpl(numOfExecutions,numOfWarmupExecutions);
        
        instance.execTests();
        
        //assert that times arrays are not null
        assertNotNull(instance.getWarmupExecutionTimes());
        assertNotNull(instance.getExecutionTimes());
        
        //assert that the number of collected times is equal to the number of executions
        assertEquals(numOfWarmupExecutions, instance.getWarmupExecutionTimes().size());
        assertEquals(numOfExecutions, instance.getExecutionTimes().size());
        
        long totalWarmupTime = 0;
        for(int i = 0; i < instance.getWarmupExecutionTimes().size(); i++)
        {
            totalWarmupTime += instance.getWarmupExecutionTimes().get(i);
        }
        
        long totalActualExecutionsTime = 0;
        for(int i = 0; i < instance.getExecutionTimes().size(); i++)
        {
            totalActualExecutionsTime += instance.getExecutionTimes().get(i);
        }
        
        long avgWarmup = totalWarmupTime/numOfWarmupExecutions;
        long avg = totalActualExecutionsTime/numOfExecutions;
        
        //assert that avg times have been computed correctly
        assertEquals(avgWarmup,instance.getWarmupAvgTime());
        assertEquals(avg,instance.getAvgTime());
        
        
    }

    

    public class BaseTesterImpl extends BaseTester {

        public BaseTesterImpl(int numOfExecutions,int numOfWarmupExecutions) {
            super(numOfExecutions, numOfWarmupExecutions);
        }

        @Override
        public int init() {
            return 0;
        }

        @Override
        public void finish() {
        }

        @Override
        public void beforeTest(State state) {
        }

        @Override
        public void execTest(State state) {
        }

        @Override
        public void afterTest(State state) {
        }

        @Override
        public String getTestName() {
            return "Test";
        }
    }
    
}
