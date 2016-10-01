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

import com.vin.dbbenchmark.test.Tester;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * BenchmarkManager is responsible for tests execution. 
 * This is an abstract class that manages a list of {@link Tester} instances executing their tests and printing results.
 * Subclasses have to init the manager (implementing the function init()) adding {@link Tester} instances to the list 
 * and initializing the resources that are needed.
 * In addition subclasses, if needed, have to free resources after tests completion (implementing the function finish()). 
 * 
 * @author Vincenzo Micelli
 */
public abstract class BenchmarkManager {
    
    private final List<Tester> testers;

    public BenchmarkManager() {
        this.testers = new ArrayList<>();
    }
    
    /**
     * Subclasses have to implement this method to add {@link Tester} instances to be executed and to init the resources that 
     * are needed for the tests.
     * 
     * @return This funcion returns an int value. If the value is different than 0 the inizialization is supposed to be failed 
     * and the BenchmarkManager does not perform the tests.
     */
    protected abstract int init();
    
    /**
     * Subclasses have to implement this method to free resources used during the tests (if needed).
     */
    protected abstract void finish();

    /**
     * Get the Testers that have been added to the manager.
     * 
     * @return The list of {@link Tester} instances that will perform the tests.
     */
    public List<Tester> getTesters() {
        return testers;
    }

    /**
     * Use this function to add a {@link Tester} that has to be executed by the manager.
     * 
     * @param tester An implementation of {@link Tester} to add to the manager.
     */
    protected void addTester(Tester tester)
    {
        testers.add(tester);
    }
    
    /**
     * Call this method to execute the tests. This method:<br>
     * - calls init() to init the tests<br>
     * - for each {@link Tester} that has been added to the manager executes its tests and prints the perfomance results.<br>
     * - calls finish() to free the resources
     */
    public void execTests()
    {
        int initResult = init();
        
        if(initResult != 0)
        {
            String error = "BenchmarkManager initialization failed.\n\n";
            Logger.getLogger(BenchmarkManager.class.getName()).log(Level.SEVERE, error);
            
            return;
        }
        
        for(Tester tester : testers)
        { 
            printTesterStart(tester);
            tester.execTests();
            printTesterResult(tester);
        }
        
        finish();
    }
    
    private void printTesterStart(Tester tester)
    {
        String testerStart = "Executing tester: " + tester.getTestName() + "\n";
            System.out.print(testerStart);
    }
    
    private void printTesterResult(Tester tester)
    {

        System.out.print("-----------------------------------------------------\n");

        String testerTitle = "Result for tester: " + tester.getTestName() + "\n";
        System.out.print(testerTitle);

        String testInfo = "\n" + tester.getTestInfo() + "\n";
        System.out.print(testInfo);

        tester.getResult().print();

        System.out.print("-----------------------------------------------------\n");
        System.out.print("\n\n");
        
    }
}
