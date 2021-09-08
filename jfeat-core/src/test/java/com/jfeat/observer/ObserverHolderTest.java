package com.jfeat.observer;

import com.jfeat.ext.plugin.async.AsyncTaskKit;
import com.jfeat.ext.plugin.async.AsyncTaskPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObserverHolderTest {

    @Test
    public void testRegisterSync() {
        ObserverKit.me().registerSync(AModel.class, 1, BModel.class);
        ObserverKit.me().registerSync(AModel.class, 2, CModel.class);
        ObserverKit.me().registerSync(BModel.class, 3, CModel.class);
        ObserverKit.me().registerSync(BModel.class, 3, CModel.class);
        Map<SubjectEvent, List<Class<? extends Observer>>> storeMap = ObserverHolder.me().getSyncObserverStoreMap();
        System.out.println("subject\tobserver");
        for (Map.Entry<SubjectEvent, List<Class<? extends Observer>>> entry : storeMap.entrySet()) {
            SubjectEvent subjectEvent = entry.getKey();
            System.out.print(subjectEvent.getSubjectClass().getName() + "\t");
            List<Class<? extends Observer>> observers = (List<Class<? extends Observer>>) entry.getValue();
            for (Class<? extends Observer> observer : observers) {
                System.out.print(observer.getName() + "\t");
            }
            System.out.println();
        }
    }

    @Test
    public void testNotifySync() {
        ObserverKit.me().registerSync(AModel.class, 1, BModel.class);
        AModel a = new AModel();
        a.save();
    }

    @Test
    public void testRegister() {
        ObserverKit.me().register(AModel.class, 1, BModel.class);
        ObserverKit.me().register(AModel.class, 2, CModel.class);
        ObserverKit.me().register(BModel.class, 3, CModel.class);
        ObserverKit.me().register(BModel.class, 3, CModel.class);
        Map<SubjectEvent, List<Class<? extends Observer>>> storeMap = ObserverHolder.me().getObserverStoreMap();
        for (Map.Entry<SubjectEvent, List<Class<? extends Observer>>> entry : storeMap.entrySet()) {
            SubjectEvent subjectEvent = entry.getKey();
            System.out.println(subjectEvent.getSubjectClass().getName());
            List<Class<? extends Observer>> observers = (List<Class<? extends Observer>>) entry.getValue();
            for (Class<? extends Observer> observer : observers) {
                System.out.println(observer.getName());
            }
        }

    }

    @Test
    public void testNotify() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        AsyncTaskKit.init(executor);
        ObserverKit.me().register(AModel.class, 1, BModel.class);
        AModel a = new AModel();
        a.save();
    }

}

class SuperModel implements Subject {

    @Override
    public void notifyAllObserver(Subject subject, int event, Object param) {
        ObserverKit.me().notifyObserverSync(subject, event, param);
        ObserverKit.me().notifyObserver(subject, event, param);
    }

    public void save() {
        System.out.println("thread=" + Thread.currentThread().getName() + ", SuperModel.save()");
        notifyAllObserver(this, 1, null);
    }
}

class AModel extends SuperModel {
    @Override
    public void save() {
        System.out.println("AModel.save()");
        super.save();
    }
}

class BModel extends SuperModel implements Observer {
    @Override
    public void save() {
        System.out.println("BModel.save()");
        super.save();
    }

    @Override
    public void invoke(Subject subject, int event, Object param) {
        AModel a = (AModel) subject;
        System.out.println("thread=" + Thread.currentThread().getName() + ", BModel.invoke(). " +a.toString() + ". event=" + event);
    }
}

class CModel extends SuperModel implements Observer {
    @Override
    public void save() {
        System.out.println("CModel.save()");
        super.save();
    }

    @Override
    public void invoke(Subject subject, int event, Object param) {

    }
}
