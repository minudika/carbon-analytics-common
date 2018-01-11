package org.wso2.carbon.event.input.adapter.sqs;

public class SQSConfig {
    private String accessKey;
    private String secretKey;
    private String queueURL;
    private int pollingInterval;
    private int waitTime;
    private int maxNumberOfMessages;
    private String serviceEndpoint;
    private String signingRegion;
    private int visibilityTimeout;

    public SQSConfig(String accessKey, String secretKey, String queueURL, int pollingInterval,
                     int waitTime, int maxNumberOfMessages, String serviceEndpoint, String signingRegion, int visibilityTimeout) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.queueURL = queueURL;
        this.pollingInterval = pollingInterval;
        this.waitTime = waitTime;
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.serviceEndpoint = serviceEndpoint;
        this.signingRegion = signingRegion;
        this.visibilityTimeout = visibilityTimeout;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getQueueURL() {
        return queueURL;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getMaxNumberOfMessages() {
        return maxNumberOfMessages;
    }

    public String getServiceEndpoint() {
        return serviceEndpoint;
    }

    public String getSigningRegion() {
        return signingRegion;
    }

    public int getVisibilityTimeout() {
        return visibilityTimeout;
    }
}
