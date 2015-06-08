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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Henrique Lobo Weissmann
 */
public class S3Test extends AWSTest {

    private final AmazonS3Client s3Client;
    
    private final String bucketName;
    
    private final String bucketRegion;
    
    private final S3Action action;
    
    private final String fileName;
    
    public S3Test(Element element) {
        super(element);
        s3Client = new AmazonS3Client(getCredentials(element));
        NodeList nodeBucket = element.getElementsByTagName("bucket");
        if (nodeBucket != null && nodeBucket.getLength() == 1) {
            Element bucketElement = (Element) nodeBucket.item(0);
            bucketName = bucketElement.getAttribute("name");
            bucketRegion = bucketElement.getAttribute("region");
        } else {
            throw new AWSException("No bucket set for test " + getId());
        }
        
        NodeList nodeAction = element.getElementsByTagName("action");
        if (nodeAction != null && nodeAction.getLength() == 1) {
            Element elementAction = (Element) nodeAction.item(0);
            this.action = new S3Action(elementAction.getAttribute("type"),
                                       elementAction.getAttribute("key"),
                                       elementAction.getTextContent());
            this.fileName = elementAction.getAttribute("file");
            
        } else {
            throw new AWSException("No action set for test " + getId());
        }
        
    }
    
    private boolean executePut() {
        File file = null;
        boolean tempFile = false;
        if (fileName != null) {
          file = new File(fileName);
        } else if (action.getContent() != null) {
            tempFile = true;
            file = new File(System.getProperty("user.dir") + "/" + java.util.UUID.randomUUID().toString());
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(action.getContent().getBytes());
                fos.close();
            } catch (IOException ex) {
                throw new AWSException("Error writing temp file", ex);
            }
        } else {
            throw new AWSException("Invalid test: where is the content do publish?");
        }
        try {
            PutObjectRequest request = new PutObjectRequest(this.bucketName, action.getKey(), file);
            PutObjectResult result = s3Client.putObject(request);
            return result != null && result.getContentMd5() != null;
        } catch (Throwable t) {
            throw new AWSException("Error in test " + getId() + " " + t.getMessage(), t);
        } 
         finally {
            if (tempFile) {
                file.delete();
            }
        }
        
    }
    
    private boolean executeGet() {
        GetObjectRequest request = new GetObjectRequest(this.bucketName, action.getKey());
        S3Object result = s3Client.getObject(request);
        
        if (action != null && action.getContent() == null) {
            throw new AWSException("Invalid test: no content to check");
        }
        
        if (result != null) {
            StringBuilder builder = new StringBuilder();
            byte[] buffer = new byte[16384];
            int c = -1;
            try {
                while ((c = result.getObjectContent().read(buffer)) != -1) {
                    builder.append(new String(buffer, 0, c));
                }
            } catch (IOException ex) {
                throw new AWSException("Error reading S3 content", ex);
            } 
            return builder.toString().equals(action.getContent());
        }
        return false;
    }
    
    @Override
    public boolean execute() {
        boolean result = false;
        switch (action.getType()) {
            case "put":
                result = executePut();
            break;
            case "get":
                result = executeGet();
            break;
        }
        return result;
    }
    
    
    private static class S3Action {
        private final String type;
        
        public String getType() {return type;}
        
        private final String key;
        
        public String getKey() {return key;}
        
        private final String content;
        
        public String getContent() {return content;}
        
        public S3Action(String type, String key, String content) {
            this.type = type;
            this.key = key;
            this.content = content;
        }
    }
    
}
