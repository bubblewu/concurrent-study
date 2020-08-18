[toc]

# 并发模式-3：Guarded Suspension模式：保护暂停

Guarded Suspension模式思想就是：`如果执行现在的处理会造成问题，那就让执行处理的线程进行等待`。
`Guarded Suspension模式通过让线程等待来保护实例的安全性`。就像你没穿衣服，让快递员在门口等你一会儿来保护你的隐私一样。
也就是说，`该模式存在一个持有状态的对象，该对象只有在自身状态合适时，才会允许线程进行目标处理`。

`在Single Threaded Execution模式中，只要有一个线程进入临界区，其他线程就无法进入，只能等待`。而`在Guarded Suspension模式中，线程是否等待取决于守护条件`。后者是在前者基础上添加了附加条件而形成的。

## Guarded Suspension模式
### GuardedObject（被守护的对象）

GuardedObject角色是一个持有被守护方法的类。`当线程执行该守护方法guardedMethod时，如守护条件成立，则可以立即执行；否则就需进行等待。`
守护条件的成立与否会跟随GuardedObject角色的状态不同而发生变化。
除了guardedMethod之外，GuardedObject角色还有可能持有其他改变实例状态的方法stateChangingMethod，特别是改变守护条件。

在Java中，可以`使用while语句和wait方法来实现守护方法guardedMethod`，而`改变实例状态的方法stateChangingMethod可以通过notify/notifyAll来实现`。

![GuardedSuspension模式](https://cdn.jsdelivr.net/gh/bubblewu/cdn/images/concurrent/guarded_suspension.png)



## 案例
案例中的RequestQueue类扮演GuardedObject守护角色，getRequest方法就是guardedMethod守护方法，putRequest方法就是stateChangingMethod改变实例状态的方法。

- Main：
一个线程ClientThread将请求Request的实例传递给另一个线程ServerThread。
```java
public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
        new ClientThread(requestQueue, "大泡泡", 2020).start();
        new ServerThread(requestQueue, "Bubble", 2020).start();
    }
```

输出：
```
大泡泡请求：[Request: No.0]
Bubble处理：[Request: No.0]
大泡泡请求：[Request: No.1]
Bubble处理：[Request: No.1]
大泡泡请求：[Request: No.2]
Bubble处理：[Request: No.2]
大泡泡请求：[Request: No.3]
Bubble处理：[Request: No.3]
大泡泡请求：[Request: No.4]
Bubble处理：[Request: No.4]
```


- Request:
```java
/**
 * 表示一个请求的类
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class Request {

    private final String name;

    public Request(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[Request: " + name + "]";
    }
}
```

- RequestQueue:
```java
/**
 * 请求存放队列
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class RequestQueue {
    /**
     * FIFO队列，存放请求。
     * 下面的get和put方法都是用了Synchronized来保护queue字段（LinkedList的实例），
     * 保证它是SingleThreadedExecution模式的即getRequest()中的两个处理（前置条件和目标处理）必须同时由一个线程来执行。
     * - 判断queue中是痘存在可取的元素；
     * - 从queue中取出一个元素
     */
    private final Queue<Request> queue = new LinkedList<>();

    /**
     * 取出并返回最先存放的那个请求。
     * 如队列为空，就一直等待，直到唤醒。
     *
     * @return 最先存放的那个请求
     */
    public synchronized Request getRequest() {
        // 如队列存在元素，就会返回头元素（不删除）；如为空，则返回null
        // 也就是Guarded Suspension模式中的守护条件，即目前进行处理的前置条件
        while (queue.peek() == null) {
            try {
                // 线程要执行某个实例的wait方法时，线程必须获取该实例的锁。
                // wait方法被调用时，获取的时this的锁。
                // 执行this的wait方法后，线程进入this的等待队列，并释放持有的this锁。
                // notify、notifyAll或interrupt会让线程退出等待队列，但在实际地继续执行处理之前，还必须再获取this的锁。
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        // 移除队列中的第一个元素并返回，如队列为空则抛出NoSuchElementException
        return queue.remove();
    }

    /**
     * 添加一个请求到队列
     *
     * @param request 请求
     */
    public synchronized void putRequest(Request request) {
        queue.offer(request);
        notifyAll();
    }

}
```

- ClientThread:
```java
/**
 * 发送请求的类：将请求加入到队列中
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class ClientThread extends Thread {
    private final Random random;
    private final RequestQueue queue;

    public ClientThread(RequestQueue queue, String name, long seed) {
        super(name);
        this.queue = queue;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            Request request = new Request("No." + i);
            System.out.println(Thread.currentThread().getName() + "请求：" + request);
            queue.putRequest(request);
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException ignore) {
            }
        }
    }

}
```

- ServerThread:
```java
/**
 * 接收请求的类
 *
 * @author wugang
 * date: 2020-08-17 18:26
 **/
public class ServerThread extends Thread {
    private final Random random;
    private final RequestQueue queue;

    public ServerThread(RequestQueue queue, String name, long seed) {
        super(name);
        this.queue = queue;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            Request request = queue.getRequest();
            System.out.println(Thread.currentThread().getName() + "处理：" + request);
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException ignore) {
            }
        }
    }

}
```

## 扩展

### guarded wait和busy wait
#### guarded wait
guarded wait是`被守护而等待`的意思。
- 实现方法为：
`线程使用wait进行等待，被notify或notifyAll后，再次检查条件是否成立`。

**由于线程在使用wait进行等待期间，是待在等待队列中停止执行的，所以不会浪费Java虚拟机的处理时间。**

```java
// 等待端
while(!ready) {
    wait();
}

// 唤醒端
ready = true;
notifyAll();

```

#### busy wait
busy wait是`忙于等待`的意思。
- 实现方法：
线程不使用wait进行等待，而是`执行yield方法（尽可能将优先级让给其他线程）的同时检查守护条件`。

**由于等待端的线程也是持续运行的，所以浪费Java虚拟机的时间**。

wait是Object类的final方法，而yield是Thread类的静态本地方法。

