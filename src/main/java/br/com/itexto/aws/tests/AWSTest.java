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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Henrique Lobo Weissmann
 */
public abstract class AWSTest {
    
    protected final Element configuration;
    
    protected Element getConfiguration() {
        return configuration;
    }
    
    protected final String id;
    
    public String getId() {
        return id;
    }
    
    protected AWSCredentials getCredentials(Element element) {
        if (element != null) {
            NodeList nodeCredentials = element.getElementsByTagName("credentials");
            if (nodeCredentials != null && nodeCredentials.getLength() == 1) {
                Element credElement = (Element) nodeCredentials.item(0);
                return new BasicAWSCredentials(credElement.getAttribute("accessKey"), credElement.getAttribute("secretKey"));
            }
        }
        return null;
    }
    
    public AWSTest(Element element) {
        this.configuration = element;
        this.id = element.getAttribute("id");
    }
    
    public abstract boolean execute();
    
}
