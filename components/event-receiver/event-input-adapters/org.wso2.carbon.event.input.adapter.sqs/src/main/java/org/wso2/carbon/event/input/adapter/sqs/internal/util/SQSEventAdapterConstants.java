/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.wso2.carbon.event.input.adapter.sqs.internal.util;


public final class SQSEventAdapterConstants {

    private SQSEventAdapterConstants() {
    }

    public static final String ADAPTER_TYPE_SQS = "sqs";
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    public static final String QUEUE_URL = "queueURL";
    public static final String POLLING_INTERVAL_NAME = "pollingInterval";
    public static final String WAIT_TIME_NAME = "waitTime";
    public static final String MAX_NUMBER_OF_MSGS_NAME = "maxNumberOfMessages";
    public static final String SERVICE_ENDPOINT = "serviceEndpoint";
    public static final String SIGNING_REGION = "signingRegion";
    public static final String VISIBILITY_TIMEOUT_NAME = "visibilityTimeout";

    public static final int POLLING_INTERVAL = 5;
    public static final int WAIT_TIME = 0;
    public static final int MAX_NUMBER_OF_MSGS = 20;
    public static final int VISIBILITY_TIMEOUT = 30;




    public static final String ADAPTER_TYPE_HTTP = "http";
    public static final int ADAPTER_MIN_THREAD_POOL_SIZE = 1;
    public static final int ADAPTER_MAX_THREAD_POOL_SIZE = 100;
    public static final int ADAPTER_EXECUTOR_JOB_QUEUE_SIZE = 10000;
    public static final long DEFAULT_KEEP_ALIVE_TIME_IN_MILLS = 20000;
    public static final String ENDPOINT_PREFIX = "/endpoints/";
    public static final String ENDPOINT_URL_SEPARATOR = "/";
    public static final String ENDPOINT_TENANT_KEY = "t";
    public static final String ADAPTER_MIN_THREAD_POOL_SIZE_NAME = "minThread";
    public static final String ADAPTER_MAX_THREAD_POOL_SIZE_NAME = "maxThread";
    public static final String ADAPTER_KEEP_ALIVE_TIME_NAME = "keepAliveTimeInMillis";
    public static final String ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME = "jobQueueSize";
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    public static final String LOCAL = "local";
    public static final String ALL = "all";
    public static final String CARBON_CONFIG_PORT_OFFSET_NODE = "Ports.Offset";
    public static final int DEFAULT_HTTP_PORT = 9763;
    public static final int DEFAULT_HTTPS_PORT = 9443;

}
