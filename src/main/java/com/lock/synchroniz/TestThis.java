package com.lock.synchroniz;


public class TestThis implements Runnable {

    public  void test() {
        try {
            System.out.println("我是线程：" + Thread.currentThread());
            Thread.sleep(1000000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestThis testThis = new TestThis();
        TestThis testThis1 = new TestThis();
        Thread a = new Thread(testThis);
        Thread b = new Thread(testThis1);
        a.start();
        b.start();
    }

    @Override
    public void run() {
        synchronized (TestThis.class) {
            test();

        }
    }
}
