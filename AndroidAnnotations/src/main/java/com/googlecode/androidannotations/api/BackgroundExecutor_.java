package com.googlecode.androidannotations.api;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BackgroundExecutor_ {

    private final static Executor executor = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

}
