package com.red.spring.examples;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class App {

    public static void main(String[] args) throws Exception {
        testCompletableFutureFlow();
    }

    static void sneaky(SneakyRunner runner) {
        try {
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void testCompletableFutureFlow() throws Exception {
        class NumTask implements Supplier<Integer> {

            final int num;

            NumTask(int num) {
                this.num = num;
            }

            @Override
            public Integer get() {
                sneaky(() -> Thread.sleep(1000));
                System.out.println(num);
                return num;
            }
        }
        long startTime = System.currentTimeMillis();
        CompletableFuture.supplyAsync(new NumTask(2))
                .thenApplyAsync(num -> {
                    NumTask b = new NumTask(num * 4);
                    return b.get();
                }).thenAcceptAsync(System.out::println);
        System.out.println("HH");
        System.out.println(System.currentTimeMillis() - startTime);
        Thread.sleep(5000);
    }

    static void testCompletableFuture() throws Exception {
        class NumTask implements Supplier<Integer> {

            final int num;

            NumTask(int num) {
                this.num = num;
            }

            @Override
            public Integer get() {
                sneaky(() -> Thread.sleep(3000));
                System.out.println(num);
                return num;
            }
        }
        long startTime = System.currentTimeMillis();
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(new NumTask(1));
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(new NumTask(2));
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(new NumTask(3));
        CompletableFuture.allOf(future1, future2, future3).join();
        System.out.println(future1.join());
        System.out.println(future2.join());
        System.out.println(future3.join());
        System.out.println(System.currentTimeMillis() - startTime);
    }

    static void testForkJoinPool() throws Exception {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        System.out.println(pool.getParallelism());
        class SumTask extends RecursiveTask<Integer> {
            final int start;
            final int end;

            SumTask(int start, int end) {
                this.start = start;
                this.end = end;
            }

            @Override
            protected Integer compute() {
                if (start > end) {
                    return 0;
                }
                if (start == end) {
                    return start;
                }
                int mid = start + (end - start) / 2;
                SumTask left = new SumTask(start, mid);
                SumTask right = new SumTask(mid + 1, end);
                right.fork();
                return left.compute() + right.join();
            }
        }
        SumTask sumTask = new SumTask(1, 100);
        pool.execute(sumTask);
        System.out.println(sumTask.join());
    }

    static void testCountDownLatch() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        class Context {
            int count;
        }
        Context context = new Context();
        CountDownLatch latch = new CountDownLatch(1000);
        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                synchronized (context) {
                    context.count++;
                }
                latch.countDown();
            });
        }
        System.out.println("wait all tasks");
        latch.await();
        System.out.println("all completed !!");
        System.out.println(context.count);
        executorService.shutdown();
    }

    static void testCyclicBarrier() throws Exception {
        class Context {
            int count;
            final CyclicBarrier barrier = new CyclicBarrier(3, () -> {
                System.out.println("All completed !!");
                System.out.println(count);
            });
        }
        Context context = new Context();
        //create three adders
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder1");
            }
            sneaky(context.barrier::await);
        }).start();
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder2");
            }
            sneaky(context.barrier::await);
        }).start();
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder3");
            }
            sneaky(context.barrier::await);
        }).start();
        //create three adders again
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder1");
            }
            sneaky(context.barrier::await);
        }).start();
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder2");
            }
            sneaky(context.barrier::await);
        }).start();
        new Thread(() -> {
            synchronized (context) {
                sneaky(() -> Thread.sleep(1000));
                context.count++;
                System.out.println("Adder3");
            }
            sneaky(context.barrier::await);
        }).start();
    }

    static void testBasicJoin() throws Exception {
        class Context {
            String message;
        }
        Context context = new Context();
        //create producer
        Thread producer = new Thread(() -> {
            sneaky(() -> Thread.sleep(2000));
            context.message = "Hello";
        });
        producer.start();
        producer.join();
        System.out.println(context.message);
    }

    static void testBasicAwaitAndNotify() throws Exception {
        class Context {
            String message;
        }
        Context context = new Context();
        //create two consumers
        new Thread(() -> {
            synchronized (context) {
                sneaky(context::wait);
                System.out.println(context.message);

            }
        }).start();
        new Thread(() -> {
            synchronized (context) {
                sneaky(context::wait);
                System.out.println(context.message);

            }
        }).start();
        Thread.sleep(1000L);
        //notify all
        synchronized (context) {
            context.message = "Hello World";
            sneaky(context::notifyAll);
        }
    }


    @FunctionalInterface
    interface SneakyRunner {
        void run() throws Exception;
    }
}
