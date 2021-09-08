package com.jfeat.observer;

import com.jfeat.ext.plugin.async.AsyncTaskKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacky on 4/15/16.
 */
public class ObserverKit {

    private static Logger logger = LoggerFactory.getLogger(ObserverKit.class);

    private static ObserverKit me = new ObserverKit();

    private ObserverKit() {

    }

    public static ObserverKit me() {
        return me;
    }

    public void registerSync(Class<? extends Subject> subject, int event, Class<? extends Observer> observer) {
        ObserverHolder.me().registerSync(subject, event, observer);
    }

    public void register(Class<? extends Subject> subject, int event, Class<? extends Observer> observer) {
        ObserverHolder.me().register(subject, event, observer);
    }

    public void notifyObserver(Subject subject, int event, Object param) {
        if (ObserverHolder.me().getObserverList(subject.getClass(), event).size() > 0) {
            NotifyTask task = new NotifyTask(subject, event, param);
            AsyncTaskKit.submit(task);
        }
    }

    public void notifyObserverSync(Subject subject, int event, Object param) {
        for (Class clazz : ObserverHolder.me().getSyncObserverList(subject.getClass(), event)) {
            try {
                Observer observer = (Observer) clazz.newInstance();
                observer.invoke(subject, event, param);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage());
            }

        }
    }


    /**
     * async task to notify observer
     */
    class NotifyTask implements Runnable {

        private Subject subject;
        private int event;
        private Object param;
        public NotifyTask(Subject subject, int event, Object param) {
            this.subject = subject;
            this.event = event;
            this.param = param;
        }

        @Override
        public void run() {
            for (Class clazz : ObserverHolder.me().getObserverList(subject.getClass(), event)) {
                try {
                    Observer observer = (Observer) clazz.newInstance();
                    observer.invoke(subject, event, param);
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error(e.getMessage());
                }

            }
        }
    }

}
