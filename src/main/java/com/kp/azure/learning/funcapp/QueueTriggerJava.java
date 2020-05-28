package com.kp.azure.learning.funcapp;

import com.microsoft.azure.functions.annotation.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.microsoft.azure.functions.*;

/**
 * Azure Functions with Azure Storage Queue trigger.
 */
public class QueueTriggerJava {
    /**
     * This function will be invoked when a new message is received at the specified
     * path. The message contents are provided as input to this function.
     */
    @FunctionName("QueueTriggerJava")
    public void run(
            @QueueTrigger(name = "message", queueName = "product-add-queue", connection = "kailashstorageaccount_STORAGE") final String message,
            final ExecutionContext context) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        context.getLogger().info("Java Queue trigger function processed a message: " + message);
        final HttpPost postRequest = new HttpPost(
                "https://restapiprodmgmt.azurewebsites.net/rest-0.0.1-SNAPSHOT/products");
        postRequest.addHeader("content-type", "application/json");
        postRequest.addHeader("Accept", "application/json");
        StringEntity userEntity=null;
        try {
            userEntity = new StringEntity(message);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        postRequest.setEntity(userEntity);

        // Send the request; It will immediately return the response in HttpResponse
        // object if any
        HttpResponse response=null;
        try {
            response = httpClient.execute(postRequest);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // verify the valid error code first
        final int statusCode = response.getStatusLine().getStatusCode();
        if ((statusCode != 200)) 
        {
            throw new RuntimeException("Failed with HTTP error code : " + statusCode);
        }    
    }
}
