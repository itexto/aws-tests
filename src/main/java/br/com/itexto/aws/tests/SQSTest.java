/*
 * Copyright 2015 kicolobo.
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
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The SQSTest
 * @author kicolobo
 */
public class SQSTest extends AWSTest {
    
    private final String queueName;
    
    private final String region;
    
    private final AmazonSQSClient sqsClient;
    
    private final SQSTestAction actionTest;
    
    public SQSTest(Element element) {
        super(element);
        AWSCredentials credentials = this.getCredentials(element);
        sqsClient = new AmazonSQSClient(credentials);
        
        NodeList nodesQueue = element.getElementsByTagName("queue");
        if (nodesQueue != null && nodesQueue.getLength() == 1) {
            Element elementQueue = (Element) nodesQueue.item(0);
            queueName = elementQueue.getAttribute("name");
            region = elementQueue.getAttribute("region");
            //sqsClient.setEndpoint(elementQueue.getAttribute("endpoint"));
        } else {
            throw new AWSException("No queue defined for test");
        }
        
        NodeList nodesAction = element.getElementsByTagName("action");
        if (nodesAction != null && nodesAction.getLength() == 1) {
            Element elementAction = (Element) nodesAction.item(0);
            String type = elementAction.getAttribute("type");
            String content = elementAction.getTextContent();
            actionTest = new SQSTestAction(type, content);
        } else {
            throw new AWSException("No action defined for test");
        }
    }
    
    
    private boolean executePublish() {
        SendMessageRequest request = new SendMessageRequest();
        //sqsClient.setRegion(Regions.fromName(this.region));
        
        request.setQueueUrl(sqsClient.getQueueUrl(this.queueName).getQueueUrl());
        request.setMessageBody(this.actionTest.getContent());
        SendMessageResult result = sqsClient.sendMessage(request);
        return result != null && result.getMessageId() != null;
    }
    
    private boolean executeReceive() {
        ReceiveMessageRequest request = new ReceiveMessageRequest();
        request.setQueueUrl(sqsClient.getQueueUrl(this.queueName).getQueueUrl());
        ReceiveMessageResult result = sqsClient.receiveMessage(request);
        boolean found = false;
        if (result != null && result.getMessages() != null && result.getMessages().size() > 0) {
            String expectedBody = this.actionTest.getContent();
            for (Message message : result.getMessages()) {
                if (message.getBody() != null && message.getBody().equals(expectedBody)) {
                    found = true;
                    sqsClient.deleteMessage(request.getQueueUrl(), message.getReceiptHandle());
                }
            }
        }
        return found;
    }
    
    @Override
    public boolean execute() {
        boolean result = false;
        switch (actionTest.getType()) {
            case "publish":
                result = executePublish();
                break;
            case "receive":
                result = executeReceive();
                break;
        }
        return result;
    }
    
    
    private static class SQSTestAction {
        
        private final String type;
        
        private final String content;
        
        public String getType() {return type;}
        
        public String getContent() {return content;}
       
        
        public SQSTestAction(String type, String content) {
            this.type = type;
            this.content = content;
            
        }
        
        
        
    }
    
}
