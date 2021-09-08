package com.jfeat.kit;

import org.junit.Test;

/**
 * Created by jackyhuang on 16/9/30.
 */
public class UIDKitTest {

    @Test
    public void testNext() {
        System.out.println(UIDKit.next());
    }

    //@Test
    public void test() throws InterruptedException {
        int count = 100;
        Thread[] threads = new Thread[count];
        for (int num = 0; num < count; num++) {
            threads[num] = new Thread(new MyThread("" + num));
        }

        for (int num = 0; num < count; num++) {
            threads[num].start();
        }
        for (int num = 0; num < count; num++) {
            threads[num].join();
        }
    }
     public class MyThread implements Runnable {

         private String name;

         public MyThread(String name) {
             this.name = name;
         }

         @Override
         public void run() {
             int i = 0;
             while (i++ < 100) {
                 System.out.println(name + " " + UIDKit.next());

             }
         }
     }
}
