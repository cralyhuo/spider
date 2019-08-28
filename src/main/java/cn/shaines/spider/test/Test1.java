package cn.shaines.spider.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author houyu
 * @createTime 2019/4/16 16:09
 */
public class Test1 {

    public static void main(String[] args) throws Exception {
        //new ThreadPoolExecutor(nThreads, nThreads,
        //                                       0L, TimeUnit.MILLISECONDS,
        //                                       new LinkedBlockingQueue<Runnable>())
        ExecutorService executorService = new ThreadPoolExecutor(2, 2,
                                                                 0, TimeUnit.MILLISECONDS,
                                                                 new LinkedBlockingQueue<Runnable>());

        //Executors.newCachedThreadPool()
        //new ThreadPoolExecutor(0, Integer.MAX_VALUE,
        //                                       60L, TimeUnit.SECONDS,
        //                                       new SynchronousQueue<Runnable>())
        executorService.submit(() -> {
            try {
                System.out.println("开始1");
                Thread.sleep(5000);
                System.out.println("完成1" + Thread.currentThread().getName());
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        executorService.submit(() -> {
            try {
                System.out.println("开始2");
                Thread.sleep(5000);
                System.out.println("完成2" + Thread.currentThread().getName());
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("准备开始3");
        executorService.submit(() -> {
            try {
                System.out.println("开始3");
                Thread.sleep(5000);
                System.out.println("完成3" + Thread.currentThread().getName());
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("所有代码执行完");
        executorService.shutdown();

    }

    /**
     * CountDownLatch countDownLatch = new CountDownLatch(50);
     * execute(() -> {
     *     System.out.println("我是方法1");
     *     System.out.println("我是方法2");
     * }, countDownLatch);
     *
     * @description 执行方法, 没有返回值
     * @date 2019-08-28 11:31:23
     * @author houyu for.houyu@foxmail.com
     */
    public static void execute(Runnable supplier, CountDownLatch countDownLatch) {
        try {
            // 执行代码块
            supplier.run();
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * CountDownLatch countDownLatch = new CountDownLatch(50);
     * String s = execute(() -> {
     *     System.out.println("我是方法1");
     *     System.out.println("我是方法2");
     *     return "";
     * }, countDownLatch);
     *
     * @description 执行方法, 携带返回值
     * @date 2019-08-28 11:31:23
     * @author houyu for.houyu@foxmail.com
     */
    public static <T> T execute(Supplier<? extends T> supplier, CountDownLatch countDownLatch) {
        try {
            // 执行代码块
            return supplier.get();
        } finally {
            countDownLatch.countDown();
        }
    }

    public static void run2(Consumer<? extends Object> consumer) {
    }

    public static void run3(Function predicate) {
        predicate.apply(new Object());
    }


    public static String test1(String s) {
        System.out.println("================test1");
        return s;
    }

    public static String test2() {
        System.out.println("================test2");
        return "test2";
    }

    public static void test3() {
        System.out.println("================test3");
    }

}
