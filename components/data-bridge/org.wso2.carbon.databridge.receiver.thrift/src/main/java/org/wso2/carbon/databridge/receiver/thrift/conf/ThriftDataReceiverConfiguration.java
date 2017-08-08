/**
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.databridge.receiver.thrift.conf;

import org.wso2.carbon.databridge.commons.thrift.utils.CommonThriftConstants;
import org.wso2.carbon.databridge.core.conf.DataBridgeConfiguration;
import org.wso2.carbon.databridge.core.conf.DataReceiver;
import org.wso2.carbon.databridge.receiver.thrift.internal.utils.ThriftDataReceiverConstants;
import org.wso2.carbon.utils.CarbonUtils;

/**
 * configuration details related to DataReceiver
 */
public class ThriftDataReceiverConfiguration {
    private int secureDataReceiverPort;
    private int dataReceiverPort;
    private String sslProtocols;
    private String ciphers;
    private String receiverHostName;
    private int waitingTimeInMilliSeconds;
    private int maxWorkerThreads;
    private int minWorkerThreads;
    private int requestTimeout;
    private int stopTimeoutVal;

    public ThriftDataReceiverConfiguration(int defaultSslPort, int defaultPort) {
        secureDataReceiverPort = defaultSslPort;
        dataReceiverPort = defaultPort;
    }

    public ThriftDataReceiverConfiguration(DataBridgeConfiguration dataBridgeConfiguration) {
        DataReceiver dataReceiver = dataBridgeConfiguration.getDataReceiver(ThriftDataReceiverConstants.
                DATA_BRIDGE_RECEIVER_NAME);
        int portOffset = getPortOffset();
        secureDataReceiverPort = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.SECURE_PORT_ELEMENT,
                CommonThriftConstants.DEFAULT_RECEIVER_PORT+CommonThriftConstants.SECURE_EVENT_RECEIVER_PORT_OFFSET).toString()) + portOffset;
        dataReceiverPort = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.PORT_ELEMENT,
                CommonThriftConstants.DEFAULT_RECEIVER_PORT).toString()) + portOffset;
        receiverHostName = dataReceiver.getConfiguration(ThriftDataReceiverConstants.RECEIVER_HOST_NAME,
                ThriftDataReceiverConstants.DEFAULT_HOSTNAME).toString();
        waitingTimeInMilliSeconds = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants
                .WAITING_TIME_IN_MILISEONDS, 0).toString());

        Object sslProtocolObj = dataReceiver.getConfiguration(ThriftDataReceiverConstants.PROTOCOLS_ELEMENT, null);
        sslProtocols =  sslProtocolObj != null ? sslProtocolObj.toString() : null;
        Object ciphersObj = dataReceiver.getConfiguration(ThriftDataReceiverConstants.CIPHERS_ELEMENT, null);
        ciphers =  sslProtocolObj != null ? ciphersObj.toString() : null;

        maxWorkerThreads = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.THRIFT_MAX_WORKER_THREADS,
                ThriftDataReceiverConstants.THRIFT_DEFAULT_MAX_WORKER_THREADS).toString());
        minWorkerThreads = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.THRIFT_MIN_WORKER_THREADS, -1).toString());
        requestTimeout = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.THRIFT_REQUEST_TIMEOUT, -1).toString());
        stopTimeoutVal = Integer.parseInt(dataReceiver.getConfiguration(ThriftDataReceiverConstants.THRIFT_STOP_TIMEOUT_VAL, -1).toString());

    }

    public ThriftDataReceiverConfiguration(int defaultSslPort, int defaultPort,
                                           String confHostName) {
        secureDataReceiverPort = defaultSslPort;
        dataReceiverPort = defaultPort;
        receiverHostName = confHostName;
    }


    public int getDataReceiverPort() {
        return dataReceiverPort;
    }

    public void setDataReceiverPort(int dataReceiverPort) {
        this.dataReceiverPort = dataReceiverPort;
    }

    public int getSecureDataReceiverPort() {
        return secureDataReceiverPort;
    }

    public void setSecureDataReceiverPort(int secureDataReceiverPort) {
        this.secureDataReceiverPort = secureDataReceiverPort;
    }

    public String getReceiverHostName() {
        return receiverHostName;
    }

    public void setReceiverHostName(String receiverHostName) {
        this.receiverHostName = receiverHostName;
    }

    public int getPortOffset() {
        return CarbonUtils.
                getPortFromServerConfig(ThriftDataReceiverConstants.CARBON_CONFIG_PORT_OFFSET_NODE) + 1;
    }

    public int getWaitingTimeInMilliSeconds(){
        return waitingTimeInMilliSeconds;
    }

    public String getSslProtocols() {
        return sslProtocols;
    }

    public void setSslProtocols(String sslProtocols) {
        this.sslProtocols = sslProtocols;
    }

    public String getCiphers() {
        return ciphers;
    }

    public void setCiphers(String ciphers) {
        this.ciphers = ciphers;
    }

    public int getMaxWorkerThreads() {
        return maxWorkerThreads;
    }

    public void setMaxWorkerThreads(int maxWorkerThreads) {
        this.maxWorkerThreads = maxWorkerThreads;
    }

    public int getMinWorkerThreads() {
        return minWorkerThreads;
    }

    public void setMinWorkerThreads(int minWorkerThreads) {
        this.minWorkerThreads = minWorkerThreads;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getStopTimeoutVal() {
        return stopTimeoutVal;
    }

    public void setStopTimeoutVal(int stopTimeoutVal) {
        this.stopTimeoutVal = stopTimeoutVal;
    }
}
