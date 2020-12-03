package com.zxn.netease.nimsdk.common.framework.infra;

import java.util.concurrent.Executor;

public class TaskWorker extends AbstractTaskWorker {
    /**
     * executor
     */
    private final Executor executor;

    public TaskWorker(Executor executor) {
        this.executor = executor;
    }

    @Override
    protected Executor getTaskHost(Task task) {
        return executor;
    }
}
