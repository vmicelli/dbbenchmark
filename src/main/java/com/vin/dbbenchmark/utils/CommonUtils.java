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
package com.vin.dbbenchmark.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 *
 * @author Vincenzo Micelli
 */
public class CommonUtils {
    
    public static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }
    
    public static String getRandomString(int size)
    {
        String randomString;
        
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        randomString = sb.toString();
        
        return randomString;
    }
    
    public static int getRandomInt(int maxValue)
    {
        Random generator = new Random();
        int i = generator.nextInt(maxValue);
        
        return i;
    }
    
     public static BigDecimal getRandomBigDecimal(int maxValue, int scale)
    {
        Random generator = new Random();
        double d = (double)generator.nextInt(maxValue) + generator.nextDouble();
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal result =  decimal.setScale(scale, RoundingMode.FLOOR);
        
        return result;
    }
    
}
