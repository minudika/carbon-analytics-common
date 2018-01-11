/*
 * Copyright (c) 2005 - 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


import org.wso2.carbon.event.input.adapter.core.*;
import org.wso2.carbon.event.input.adapter.sqs.internal.util.SQSEventAdapterConstants;
import org.wso2.carbon.utils.CarbonUtils;

import java.util.*;

/**
 * The http event adapter factory class to create a http input adapter
 */
public class SQSEventAdapterFactory extends InputEventAdapterFactory {

    private ResourceBundle resourceBundle =
            ResourceBundle.getBundle("org.wso2.carbon.event.input.adapter.sqs.i18n.Resources", Locale.getDefault());

    public SQSEventAdapterFactory() {

    }

    @Override
    public String getType() {
        return SQSEventAdapterConstants.ADAPTER_TYPE_SQS;
    }

    @Override
    public List<String> getSupportedMessageFormats() {
        List<String> supportInputMessageTypes = new ArrayList<String>();
        supportInputMessageTypes.add(MessageType.XML);
        supportInputMessageTypes.add(MessageType.JSON);
        supportInputMessageTypes.add(MessageType.TEXT);
        return supportInputMessageTypes;
    }

    @Override
    public List<Property> getPropertyList() {

        List<Property> propertyList = new ArrayList<Property>();

        // Access Key for SQS
        Property accessKeyProperty = new Property(SQSEventAdapterConstants.ACCESS_KEY);
        accessKeyProperty.setRequired(true);
        accessKeyProperty.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.ACCESS_KEY));

        // Secret Key for SQS
        Property secretKeyProperty = new Property(SQSEventAdapterConstants.SECRET_KEY);
        secretKeyProperty.setRequired(true);
        secretKeyProperty.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.SECRET_KEY));

        // URL of SQS Queue
        Property queueURL = new Property(SQSEventAdapterConstants.QUEUE_URL);
        queueURL.setRequired(true);
        queueURL.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.QUEUE_URL));

        // Polling interval
        Property pollingInterval = new Property(SQSEventAdapterConstants.POLLING_INTERVAL_NAME);
        pollingInterval.setRequired(false);
        pollingInterval.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.POLLING_INTERVAL_NAME));

        // Wait time until messages available in the queue
        Property waitTime = new Property(SQSEventAdapterConstants.WAIT_TIME_NAME);
        waitTime.setRequired(false);
        waitTime.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.WAIT_TIME_NAME));

        // Wait time until messages available in the queue
        Property maxNumberOfMsgs = new Property(SQSEventAdapterConstants.MAX_NUMBER_OF_MSGS_NAME);
        waitTime.setRequired(false);
        waitTime.setDisplayName(
                resourceBundle.getString(SQSEventAdapterConstants.MAX_NUMBER_OF_MSGS_NAME));

        propertyList.add(accessKeyProperty);
        propertyList.add(secretKeyProperty);
        propertyList.add(queueURL);
        propertyList.add(pollingInterval);
        propertyList.add(waitTime);
        propertyList.add(maxNumberOfMsgs);

        return propertyList;
    }

    @Override
    public String getUsageTips() {
        return "";
//        return resourceBundle.getString(SQSEventAdapterConstants.ADAPTER_USAGE_TIPS_PREFIX) + httpPort + resourceBundle.getString(SQSEventAdapterConstants.ADAPTER_USAGE_TIPS_MID1) + httpsPort + resourceBundle.getString(SQSEventAdapterConstants.ADAPTER_USAGE_TIPS_MID2) + httpPort + resourceBundle.getString(SQSEventAdapterConstants.ADAPTER_USAGE_TIPS_MID3) + httpsPort + resourceBundle.getString(SQSEventAdapterConstants.ADAPTER_USAGE_TIPS_POSTFIX);
    }

    @Override
    public InputEventAdapter createEventAdapter(InputEventAdapterConfiguration eventAdapterConfiguration,
                                                Map<String, String> globalProperties) {
        return new SQSEventAdapter(eventAdapterConfiguration, globalProperties);
    }

    private int getPortOffset() {
        return CarbonUtils.getPortFromServerConfig(SQSEventAdapterConstants.CARBON_CONFIG_PORT_OFFSET_NODE) + 1;
    }
}
