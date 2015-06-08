/*
 * Copyright 2015 itexto.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.itexto.aws;

import br.com.itexto.aws.tests.AWSTest;
import br.com.itexto.aws.tests.AWSTestFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * The kickstarter class of the itexto AWS Tests
 * @author Henrique Lobo Weissmann
 */
public class Main {
    
    private Properties properties;
    
    private void execute(String args[]) throws FileNotFoundException {
        if (args.length < 1) {
            System.out.println("WRONG - must be ...");
            System.out.println("\t java -jar aws-tests.jar testFile.xml");
        } else {
            FileInputStream ifs = new FileInputStream(new File(args[0]));
            AWSTest[] tests = new AWSTestFactory().getTests(ifs);
            for (AWSTest test : tests) {
                System.out.println("Running test [" + test.getId() + "]");
                try {
                    boolean result = test.execute();
                    System.out.println("\tResult: " + (result ? "OK" : "NOK"));
                } catch (Throwable t) {
                    System.out.println("\tError: " + t.getMessage());
                }
            }
            
        }
        
    }
    
    public Main(String args[]) {
        try {
            properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("itexto-aws-tests.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Could not load itexto AWS Tests properties");
            System.exit(1);
        }
        System.out.println("itexto AWS Tests - " + properties.getProperty("version"));
        System.out.println("Author: Henrique Lobo Weissmann - kico@itexto.com.br");
        System.out.println("http://www.itexto.com.br");
        System.out.println("======================================================");
        try {
            execute(args);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found: " + ex.getMessage());
        }
    }
    
    public static void main(String args[]) {
        Main main = new Main(args);
    }
    
}
