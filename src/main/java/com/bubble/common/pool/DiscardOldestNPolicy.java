package com.bubble.common.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义拒绝策略：
 * 该策略根据传入的参数丢弃最老的N个线程。
 * 以便在出现异常时释放更多的资源，保障后续线程任务整体、稳定地运行。
 *
 * @author wugang
 * date: 2020-09-07 17:51
 **/
public class DiscardOldestNPolicy implements RejectedExecutionHandler {
    private int discardNumber = 5;
    private List<Runnable> discardList = new ArrayList<>();

    public DiscardOldestNPolicy(int discardNumber) {
        this.discardNumber = discardNumber;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.getQueue().size() > discardNumber) {
            // 批量删除线程队列中的N个线程任务
            // 一次性从队列中批量获取所有可用的数据对象，同时可以指定获取数据的个数，
            // 通过该方法可以提升获取数据的效率，避免多次频繁操作引起的队列锁定
            executor.getQueue().drainTo(discardList, discardNumber);
            // 清空discardList列表
            discardList.clear();
            if (!executor.isShutdown()) {
                // 尝试提交当前任务
                executor.execute(r);
            }
        }
    }
}
