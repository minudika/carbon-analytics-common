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

package org.wso2.carbon.event.input.adapter.sqs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapter;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterConfiguration;
import org.wso2.carbon.event.input.adapter.core.InputEventAdapterListener;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterException;
import org.wso2.carbon.event.input.adapter.core.exception.InputEventAdapterRuntimeException;
import org.wso2.carbon.event.input.adapter.core.exception.TestConnectionNotSupportedException;
import org.wso2.carbon.event.input.adapter.sqs.internal.ds.SQSEventAdapterServiceValueHolder;
import org.wso2.carbon.event.input.adapter.sqs.internal.util.SQSEventAdapterConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.servlet.ServletException;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import com.amazonaws.services.sqs.AmazonSQS;


public final class SQSEventAdapter implements InputEventAdapter {

    private final InputEventAdapterConfiguration eventAdapterConfiguration;
    private final Map<String, String> globalProperties;
    private InputEventAdapterListener eventAdaptorListener;
    private final String id = UUID.randomUUID().toString();
    public static ScheduledExecutorService executorService;
    private static final Log log = LogFactory.getLog(SQSEventAdapter.class);
    private boolean isConnected = false;
    private AmazonSQS sqs;

    private SQSProvider sqsProvider;

    private int minThreadPoolSize;
    private int maxThreadPoolSize;
    private long keepAliveTimeInMillis;
    private int jobQueueSize;
    private int waitingTime = 0;

    public SQSEventAdapter(InputEventAdapterConfiguration eventAdapterConfiguration,
                           Map<String, String> globalProperties) {
        this.eventAdapterConfiguration = eventAdapterConfiguration;
        this.globalProperties = globalProperties;
    }

    @Override
    public void init(InputEventAdapterListener eventAdaptorListener) throws InputEventAdapterException {
        this.eventAdaptorListener = eventAdaptorListener;

        //ThreadPoolExecutor will be assigned  if it is null
        if (executorService == null) {
            //If global properties are available those will be assigned else constant values will be assigned
            if (globalProperties.get(SQSEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME) != null) {
                minThreadPoolSize = Integer
                        .parseInt(globalProperties.get(SQSEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE_NAME));
            } else {
                minThreadPoolSize = SQSEventAdapterConstants.ADAPTER_MIN_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(SQSEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME) != null) {
                maxThreadPoolSize = Integer
                        .parseInt(globalProperties.get(SQSEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE_NAME));
            } else {
                maxThreadPoolSize = SQSEventAdapterConstants.ADAPTER_MAX_THREAD_POOL_SIZE;
            }

            if (globalProperties.get(SQSEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME) != null) {
                keepAliveTimeInMillis = Integer
                        .parseInt(globalProperties.get(SQSEventAdapterConstants.ADAPTER_KEEP_ALIVE_TIME_NAME));
            } else {
                keepAliveTimeInMillis = SQSEventAdapterConstants.DEFAULT_KEEP_ALIVE_TIME_IN_MILLS;
            }

            if (globalProperties.get(SQSEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME) != null) {
                jobQueueSize = Integer
                        .parseInt(globalProperties.get(SQSEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE_NAME));
            } else {
                jobQueueSize = SQSEventAdapterConstants.ADAPTER_EXECUTOR_JOB_QUEUE_SIZE;
            }

            RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        log.error("Exception while adding event to executor queue : " + e.getMessage(), e);
                    }
                }

            };

            executorService = Executors.newScheduledThreadPool(minThreadPoolSize);
            sqsProvider = new SQSProvider(eventAdapterConfiguration, eventAdaptorListener);

            /*executorService = new ThreadPoolExecutor(minThreadPoolSize, maxThreadPoolSize, keepAliveTimeInMillis,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(jobQueueSize), rejectedExecutionHandler);*/
        }
    }

    @Override
    public void testConnect() throws TestConnectionNotSupportedException {
        throw new TestConnectionNotSupportedException("not-supported");
    }

    @Override
    public void connect() {
        isConnected = true;
    }

    @Override
    public void disconnect() {
        if (isConnected){
            isConnected = false;
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SQSEventAdapter))
            return false;

        SQSEventAdapter that = (SQSEventAdapter) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean isEventDuplicatedInCluster() {
        return false;
    }

    @Override
    public boolean isPolling() {
        return false;
    }
}