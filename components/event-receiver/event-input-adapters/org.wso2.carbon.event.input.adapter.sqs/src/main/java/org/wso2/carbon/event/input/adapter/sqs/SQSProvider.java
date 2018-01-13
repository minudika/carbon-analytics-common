package org.wso2.carbon.event.input.adapter.sqs;

import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;
import org.wso2.carbon.event.input.adapter.sqs.internal.util.SQSEventAdapterConstants;

import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class SQSProvider {
    private BasicAWSCredentials credentials;
    private AmazonSQS sqs;
    private InputEventAdapterListener eventAdapterListener;
    private SQSConfig configs;

    public SQSProvider(SQSConfig configs, InputEventAdapterListener eventAdapterListener){
        this.configs = configs;
        BasicAWSCredentials credentials = new BasicAWSCredentials(configs.getAccessKey(), configs.getSecretKey());
        AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                new AwsClientBuilder.EndpointConfiguration(configs.getServiceEndpoint(), configs.getSigningRegion());

        sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    public SQSTask getNewSQSTask() {
        return new SQSTask(sqs, configs, eventAdapterListener);
    }

    public AmazonSQS getAWSSQSClient(){
        return sqs;
    }

    /**
     * gets messages from your queue
     * @param queueUrl
     * @return
     */
    public List<Message> getMessagesFromQueue(String queueUrl, int waitTime){
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(waitTime)
                .withVisibilityTimeout(10);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    /**
     * deletes a single message from your queue.
     * @param queueUrl
     * @param message
     */
    public void deleteMessageFromQueue(String queueUrl, Message message){
        String messageRecieptHandle = message.getReceiptHandle();
        System.out.println("message deleted : " + message.getBody() + "." + message.getReceiptHandle());
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }
}
