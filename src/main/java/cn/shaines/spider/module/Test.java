package cn.shaines.spider.module;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("Duplicates")
public class Test {

    public static void main(String[] args) {

        //固定为4的线程队列
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(24);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.DAYS, queue);
        for (int i = 0; i < 20; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("11111111111111");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("线程队列大小为-->" + i);
        }
        executor.shutdown();
    }

}
