/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.event.receiver.core.internal.management;

import org.apache.log4j.Logger;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.event.processor.manager.core.EventManagementUtil;
import org.wso2.carbon.event.processor.manager.core.EventSync;
import org.wso2.carbon.event.processor.manager.core.Manager;
import org.wso2.carbon.event.receiver.core.internal.ds.EventReceiverServiceValueHolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueInputEventDispatcher extends AbstractInputEventDispatcher implements EventSync {

    private final StreamDefinition streamDefinition;
    private Logger log = Logger.getLogger(AbstractInputEventDispatcher.class);
    private final BlockingEventQueue eventQueue;
    private Lock readLock;
    private String syncId;
    private int tenantId;
    private ReentrantLock threadBarrier = new ReentrantLock();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isContinue = false;
    private String originalEventStreamId;

    public QueueInputEventDispatcher(int tenantId, String syncId, Lock readLock,
                                     StreamDefinition exportedStreamDefinition,
                                     int eventQueueSizeMb, int eventSyncQueueSize) {
        this.readLock = readLock;
        this.tenantId = tenantId;
        this.syncId = syncId;
        this.eventQueue = new BlockingEventQueue(eventQueueSizeMb, eventSyncQueueSize);
        this.originalEventStreamId = exportedStreamDefinition.getStreamId();
        this.streamDefinition = EventManagementUtil.constructDatabridgeStreamDefinition(syncId, exportedStreamDefinition);
        this.executorService.submit(new QueueInputEventDispatcherWorker());
    }

    @Override
    public void onEvent(Event event) {
        try {
            threadBarrier.lock();
            eventQueue.put(event);
            threadBarrier.unlock();
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting to put the event to queue.", e);
        }
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public byte[] getState() {
        threadBarrier.lock();
        byte[] state = objectToBytes(eventQueue);
        threadBarrier.unlock();
        return state;
    }

    @Override
    public void syncState(byte[] bytes) {
        BlockingEventQueue events = (BlockingEventQueue) bytesToObject(bytes);
        while (events.peek() != null) {
            if (events.poll().equals(eventQueue.peek())) {
                eventQueue.poll();
            } else {
                break;
            }
        }
    }

    @Override
    public void process(Event event) {
        if(isContinueProcess()){
            readLock.lock();
            readLock.unlock();
            callBack.sendEvent(event);
        }
    }

    @Override
    public StreamDefinition getStreamDefinition() {
        return streamDefinition;
    }

    @Override
    public boolean isContinueProcess() {
        return isContinue;
    }

    @Override
    public void setContinueProcess(boolean isContinue) {
        this.isContinue = isContinue;
    }

    @Override
    public String getOriginalEventStreamId() {
        return originalEventStreamId;
    }

    private byte[] objectToBytes(Object obj) {
        long start = System.currentTimeMillis();
        byte[] out = null;
        if (obj != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                out = baos.toByteArray();
            } catch (IOException e) {
                log.error("Error when writing byte array. " + e.getMessage(), e);
                return null;
            }
        }
        long end = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("Encoded in :" + (end - start) + " msec");
        }
        return out;
    }

    private Object bytesToObject(byte[] bytes) {
        long start = System.currentTimeMillis();
        Object out = null;
        if (bytes != null) {
            try {
                ByteArrayInputStream bios = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bios);
                out = ois.readObject();
            } catch (IOException e) {
                log.error("Error when writing to object. " + e.getMessage(), e);
                return null;
            } catch (ClassNotFoundException e) {
                log.error("Error when writing to object. " + e.getMessage(), e);
                return null;
            }
        }
        long end = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("Decoded in :" + (end - start) + " msec");
        }
        return out;
    }

    class QueueInputEventDispatcherWorker implements Runnable {

        @Override
        public void run() {
            try {
                PrivilegedCarbonContext.startTenantFlow();
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(tenantId);
                PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain(true);
                while (true) {
                    try {
                        readLock.lock();
                        readLock.unlock();
                        Event event = eventQueue.take();
                        readLock.lock();
                        readLock.unlock();
                        if(isContinueProcess()){
                            callBack.sendEvent(event);
                        }
                        if (isSendToOther()) {
                            EventReceiverServiceValueHolder.getEventManagementService().syncEvent(syncId, Manager.ManagerType.Receiver, event);
                        }
                    } catch (InterruptedException e) {
                        log.error("Interrupted while waiting to get an event from queue.", e);
                    }
                }
            } catch (Exception e) {
                log.error("Error in dispatching events:" + e.getMessage(), e);
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        }
    }
}
