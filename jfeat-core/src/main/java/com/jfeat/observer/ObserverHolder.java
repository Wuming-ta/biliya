/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.observer;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObserverHolder {
    private static Logger logger = LoggerFactory.getLogger(ObserverHolder.class);
    
    private static ObserverHolder me = new ObserverHolder();

    private final Map<SubjectEvent, List<Class<? extends Observer>>> observerStoreMap = new LinkedHashMap<>();
    private final Map<SubjectEvent, List<Class<? extends Observer>>> syncObserverStoreMap = new LinkedHashMap<>();

    public static ObserverHolder me() {
        return me;
    }

    public void registerSync(Class<? extends Subject> subject, int event, Class<? extends Observer> observer) {
        logger.debug("registering sync observer, subject={}, event={}", subject.getName(), event);
        SubjectEvent subjectEvent = new SubjectEvent(subject, event);
        if (syncObserverStoreMap.get(subjectEvent) == null) {
            List<Class<? extends Observer>> observers = new LinkedList<>();
            observers.add(observer);
            syncObserverStoreMap.put(subjectEvent, observers);
            logger.info("1 observer: " + observer.getName() + " registered.");
        }
        else {
            List<Class<? extends Observer>> observers = syncObserverStoreMap.get(subjectEvent);
            if (!observers.contains(observer)) {
                observers.add(observer);
                logger.info("2 observer: " + observer.getName() + " registered.");
            }
            else {
                logger.info("2 observer: " + observer.getName() + " already registered.");
            }
            syncObserverStoreMap.put(subjectEvent, observers);
        }
    }

    public void register(Class<? extends Subject> subject, int event, Class<? extends Observer> observer) {
        logger.debug("registering observer, subject={}, event={}", subject.getName(), event);
        SubjectEvent subjectEvent = new SubjectEvent(subject, event);
        if (observerStoreMap.get(subjectEvent) == null) {
            List<Class<? extends Observer>> observers = new LinkedList<>();
            observers.add(observer);
            observerStoreMap.put(subjectEvent, observers);
            logger.info("1 observer: " + observer.getName() + " registered.");
        }
        else {
            List<Class<? extends Observer>> observers = observerStoreMap.get(subjectEvent);
            if (!observers.contains(observer)) {
                observers.add(observer);
                logger.info("2 observer: " + observer.getName() + " registered.");
            }
            else {
                logger.info("2 observer: " + observer.getName() + " already registered.");
            }
            observerStoreMap.put(subjectEvent, observers);
        }
    }

    public List<Class<? extends Observer>> getObserverList(Class<? extends Subject> subject, int event) {
        SubjectEvent subjectEvent = new SubjectEvent(subject, event);
        return observerStoreMap.get(subjectEvent) != null ? observerStoreMap.get(subjectEvent) : new LinkedList<Class<? extends Observer>>();
    }

    public Map<SubjectEvent, List<Class<? extends Observer>>> getObserverStoreMap() {
        return observerStoreMap;
    }

    public List<Class<? extends Observer>> getSyncObserverList(Class<? extends Subject> subject, int event) {
        SubjectEvent subjectEvent = new SubjectEvent(subject, event);
        return syncObserverStoreMap.get(subjectEvent) != null ? syncObserverStoreMap.get(subjectEvent) : new LinkedList<Class<? extends Observer>>();
    }

    public Map<SubjectEvent, List<Class<? extends Observer>>> getSyncObserverStoreMap() {
        return syncObserverStoreMap;
    }
}
