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

    private String accessKey;
    private String secretKey;
    private String serviceEndPoint;
    private String signingRegion;
    private int waitTime;
    private int pollingInterval;
    private int maxNumberOfMessages;
    private String queueURL;
    private int visibilityTimeout;

    private SQSConfig sqsConfigs;

    public SQSProvider(InputEventAdapterConfiguration configs, InputEventAdapterListener eventAdapterListener){
        Map adapterProperties = configs.getProperties();

        if (adapterProperties != null) {
            accessKey = adapterProperties.get(SQSEventAdapterConstants.ACCESS_KEY).toString();
            secretKey = adapterProperties.get(SQSEventAdapterConstants.SECRET_KEY).toString();
            serviceEndPoint = adapterProperties.get(SQSEventAdapterConstants.SERVICE_ENDPOINT).toString();
            signingRegion = adapterProperties.get(SQSEventAdapterConstants.SIGNING_REGION).toString();
            queueURL = adapterProperties.get(SQSEventAdapterConstants.QUEUE_URL).toString();

            waitTime = adapterProperties.get(SQSEventAdapterConstants.WAIT_TIME_NAME) != null ?
                    Integer.parseInt(adapterProperties.get(SQSEventAdapterConstants.WAIT_TIME_NAME).toString()) :
                    SQSEventAdapterConstants.WAIT_TIME;

            pollingInterval = adapterProperties.get(SQSEventAdapterConstants.POLLING_INTERVAL_NAME) != null ?
                    Integer.parseInt(adapterProperties.get(SQSEventAdapterConstants.POLLING_INTERVAL_NAME).toString()) :
                    SQSEventAdapterConstants.POLLING_INTERVAL;

            maxNumberOfMessages = adapterProperties.get(SQSEventAdapterConstants.MAX_NUMBER_OF_MSGS_NAME) != null ?
                    Integer.parseInt(adapterProperties.get(SQSEventAdapterConstants.MAX_NUMBER_OF_MSGS_NAME).toString()) :
                    SQSEventAdapterConstants.MAX_NUMBER_OF_MSGS;

            visibilityTimeout = adapterProperties.get(SQSEventAdapterConstants.VISIBILITY_TIMEOUT_NAME) != null ?
                    Integer.parseInt(adapterProperties.get(SQSEventAdapterConstants.VISIBILITY_TIMEOUT_NAME).toString()) :
                    SQSEventAdapterConstants.VISIBILITY_TIMEOUT;

            sqsConfigs = new SQSConfig(accessKey, secretKey, queueURL, pollingInterval, waitTime, maxNumberOfMessages,
                    serviceEndPoint, signingRegion, visibilityTimeout);

             BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
             AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                        new AwsClientBuilder.EndpointConfiguration(serviceEndPoint, signingRegion);

             sqs = AmazonSQSClientBuilder.standard()
                     .withCredentials(new AWSStaticCredentialsProvider(credentials))
                     .withEndpointConfiguration(endpointConfiguration)
                     .build();
        }
    }

    public SQSTask getNewSQSTask() {
        SQSEventAdapter.executorService.submit(new SQSTask(sqs, sqsConfigs, eventAdapterListener));
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
