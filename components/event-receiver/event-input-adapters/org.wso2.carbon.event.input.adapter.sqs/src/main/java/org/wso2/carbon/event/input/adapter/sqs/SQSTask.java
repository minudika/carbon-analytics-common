package org.wso2.carbon.event.input.adapter.sqs;

import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;

import java.util.List;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class SQSTask implements Runnable{
    private SQSConfig sqsConfigs;
    private ReceiveMessageRequest request;
    private AmazonSQS sqs;
    private InputEventAdapterListener eventAdapterListener;
    public SQSTask(AmazonSQS sqs, SQSConfig configs, InputEventAdapterListener eventAdapterListener) {
        this.sqs = sqs;
        this.sqsConfigs = configs;
        this.eventAdapterListener = eventAdapterListener;
        this.request = new ReceiveMessageRequest(configs.getQueueURL())
                            .withMaxNumberOfMessages(configs.getMaxNumberOfMessages())
                            .withWaitTimeSeconds(configs.getWaitTime())
                            .withVisibilityTimeout(configs.getVisibilityTimeout());
    }

    @Override
    public void run() {
        List<Message> messages = sqs.receiveMessage(request).getMessages();
        for(Message message : messages) {
            String eventMsg = message.getBody();
            eventAdapterListener.onEvent(eventMsg);
        }
    }
}
