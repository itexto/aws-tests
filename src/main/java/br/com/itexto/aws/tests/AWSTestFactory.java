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
package br.com.itexto.aws.tests;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Henrique Lobo 
 */
public class AWSTestFactory {
    
    public AWSTest[] getTests(InputStream input) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new AWSException("Error parsing test document", ex);
        }
        try {
            Document document = db.parse(input);
            NodeList nodes = document.getElementsByTagName("test");
            AWSTest[] result = new AWSTest[nodes.getLength()];
            for (int i = 0; i < nodes.getLength(); i++) {
                result[i] = create((Element) nodes.item(i));
            }
            return result;
        } catch (SAXException ex) {
            throw new AWSException("Error parsing test document", ex);
        } catch (IOException ex) {
            throw new AWSException("Error parsing test document", ex);
        }
    }
    
    public AWSTest create(Element element) {
        if (element != null) {
            String testName = element.getAttribute("type");
            if (testName != null) {
                
                Class clazz = null;
                try {
                    clazz = Class.forName("br.com.itexto.aws.tests." + testName);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    return (AWSTest) clazz.getConstructor(Element.class).newInstance(element);
                } catch (InstantiationException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(AWSTestFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                 
            }
        }
        return null;
    }
    
}
