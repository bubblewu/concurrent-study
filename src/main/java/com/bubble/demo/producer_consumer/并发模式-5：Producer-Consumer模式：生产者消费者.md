[toc]
# 并发模式-5：Producer-Consumer模式：生产者消费者


## Producer-Consumer模式
生产者消费者模式，即`N个线程进行生产，同时N个线程进行消费，两种角色通过内存缓冲区进行通信。`

### 三个角色
在Producer-Consumer模式，承担安全守护责任的是`Channel角色`。`Channel角色执行线程间的互斥处理，确保Producer角色正确地将Data角色传递给Consumer角色。`

#### Channel角色
Producer-Consumer模式为了Producer向Consumer传递Data，在中间设置了Channel角色。

- `Producer直接调用Consumer的方法`：
如Producer直接调用Consumer的方法，那么执行处理的就不是Consumer的线程，而是Producer的线程了。这样执行处理花费的时间就必须由Producer的线程来承担，准备下一个数据的处理也会发生相应的延迟，会使程序的响应性变得差。

就好像：糕点师傅做好蛋糕，直接交给客人，在客人吃完后再做下一个蛋糕一样。

- `借助Channel角色`：
Producer将Data传递给Channel角色后，无需等待Consumer角色对Data进行处理，就可以立即开始准备下一个Data。
也就是，**Producer可以持续不断地创建Data，而不会受到Consumer角色的处理进度影响。**

##### 如何传递Data
- `队列：先接收的先传递`
使用FIFO先进先出的队列来实现。

- `栈：后接收的先传递`
使用LIFO后进先出的栈来实现。

- `优先队列：优先级高的先传递`
使用优先队列来实现，Channel角色给收到的Data设置优先级，优先级高的先传递给Consumer来处理。

##### 存在意义
因为Channel的存在，Producer和Consumer这些线程才能保持协调运行。
Channel这个中间角色可以实现线程的协调运行。
- 线程的`协调运行`要考虑：`放在中间的东西`。
- 线程的`互斥处理`要考虑：`应该保护的东西`。

协调运行和互斥处理是内外统一的。
为了让线程协调运行，必须执行互斥处理，以防止共享的内容被破坏；
线程的互斥处理是为了线程的协调运行才执行的。

## 案例
### 场景
生产者消费者模式Demo：
旋转小餐厅里，有3位师傅制作蛋糕放到桌子上，然后有3位客人来吃这些蛋糕。
主要业务点：
- 师傅（MakerThread）制作蛋糕（String），并将其放置在桌子（Table）上；
- 桌子上最多可以放置3个蛋糕；
- 如果桌子上已经放满3个，就需等有空余位置时才能继续放置；
- 客人（EaterThread）按蛋糕放置等顺序来取桌子（Table）上等蛋糕来吃；
- 当桌子没有蛋糕时，客人就需等待直到有蛋糕放入；

### 实现
- Main函数：
```java
public static void main(String[] args) {
        Table table = new Table(3);
        for (int i = 0; i < 3; i++) {
            MakerThread makerThread = new MakerThread("-> Maker." + i, table, 2020 + i);
            makerThread.start();
            EaterThread eaterThread = new EaterThread("Eater." + i, table, 2020 + i);
            eaterThread.start();
        }
    }
```
输出：
```
-> Maker.1 put: Cake No.0 by -> Maker.1
Eater.2 take: Cake No.0 by -> Maker.1
-> Maker.2 put: Cake No.1 by -> Maker.2
Eater.0 take: Cake No.1 by -> Maker.2
-> Maker.0 put: Cake No.2 by -> Maker.0
Eater.2 take: Cake No.2 by -> Maker.0
-> Maker.1 put: Cake No.3 by -> Maker.1
Eater.1 take: Cake No.3 by -> Maker.1
-> Maker.0 put: Cake No.4 by -> Maker.0
Eater.1 take: Cake No.4 by -> Maker.0
-> Maker.2 put: Cake No.5 by -> Maker.2
Eater.0 take: Cake No.5 by -> Maker.2
-> Maker.1 put: Cake No.6 by -> Maker.1
Eater.0 take: Cake No.6 by -> Maker.1
```

- Channel角色 Table类：
```java
/**
 * Channel角色：表示桌子
 *
 * @author wugang
 * date: 2020-07-16 19:59
 **/
public class Table {
    /**
     * 盘子：放置蛋糕的数组
     */
    private final String[] dishArray;
    /**
     * 下一次放置蛋糕的位置
     */
    private int tail;
    /**
     * 下一次取蛋糕的位置
     */
    private int head;
    /**
     * 当前桌子上放置的蛋糕个数
     */
    private int count;

    public Table(int totalCount) {
        this.dishArray = new String[totalCount];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    /**
     * 放置蛋糕
     *
     * @param cake 蛋糕
     */
    public synchronized void put(String cake) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " put: " + cake);
        // 最多只能放置3个
        while (count >= dishArray.length) {
            wait();
        }
        dishArray[tail] = cake;
        // 取下一次要放置的位置
        tail = (tail + 1) % dishArray.length;
        count++;
        notifyAll();
    }

    /**
     * 取蛋糕
     *
     * @return 蛋糕
     */
    public synchronized String take() throws InterruptedException {
        // 桌子上没有蛋糕，等待
        while (count <= 0) {
            wait();
        }
        String cake = dishArray[head];
        // 取下一次要取的位置
        head = (head + 1) % dishArray.length;
        count--;
        notifyAll();
        System.out.println(Thread.currentThread().getName() + " take: " + cake);
        return cake;
    }

}
```

- 基于juc的队列来实现Table：
```java
/**
 * 基于juc的队列来实现Table
 *
 * @author wugang
 * date: 2020-07-17 17:57
 **/
public class TableQueue extends ArrayBlockingQueue<String> {
    public TableQueue(int capacity) {
        super(capacity);
    }

    @Override
    public void put(String cake) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " put: " + cake);
        super.put(cake);
    }

    @Override
    public String take() throws InterruptedException {
        String cake = super.take();
        System.out.println(Thread.currentThread().getName() + " take: " + cake);
        return cake;
    }

}
```

- MakerThread生产者：
```java
/**
 * 表示糕点师
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class MakerThread extends Thread {
    private final Random random;
    private final Table table;
    /**
     * 蛋糕的流水号，所有糕点师共用
     */
    private static int id = 0;

    public MakerThread(String name, Table table, long seed) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }


    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                String cake = "Cake No." + nextId() + " by " + getName();
                table.put(cake);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized int nextId() {
        return id++;
    }

}
```

- EaterThread消费者：
```java
/**
 * 表示来吃蛋糕的客人
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class EaterThread extends Thread {
    private final Random random;
    private final Table table;

    public EaterThread(String name, Table table, long seed) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String cake = table.take();
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
```

## 扩展
### 常用队列
- `ArrayBlockingQueue类`：基于数组的BlockingQueue
表示元素个数有最大限制的BlockingQueue。
当队列满了仍put数据时，或队列为空仍要take数据时，线程会阻塞，

- `LinkedBlockingQueue类`：基于链表的BlockingQueue
表示元素个数没有最大限制的BlockingQueue。
该类基于链表，如果没有特别指定，元素个数没有最大限制，只要还有内存，就可以put数据。

- `PriorityBlockingQueue类`：带有优先级的BlockingQueue
表示有优先级的BlockingQueue。
数据的优先级时根据Comparable接口的自然排序，或构造函数的Comparator接口决定的顺序指定。

- `DelayQueue类`：一定时间之后才可以take的BlockingQueue
表示用于存储java.util.concurrent.Delayed对象的队列。
当从该队列take时，只有在各元素指定的时间到期后才可以take。

- `SynchronousQueue类`：直接传递的BlockingQueue
SynchronousQueue类表示的是BlockingQueue，该BlockingQueue用于执行由Producer角色到Consumer角色的直接传递。
如果Producer先put，在Consumer进行take之前，Producer的线程会一直阻塞。相反，如Consumer先take，在Producer执行put之前，Consumer的线程将会一直阻塞。

- `ConcurrentLinkedQueue类`：元素个数没有最大限制的线程安全队列
ConcurrentLinkedQueue类并不是BlockingQueue的实现类，它表示元素个数没有最大限制的线程安全队列。
在ConcurrentLinkedQueue中，内部的数据结构是分开的，线程之间互不影响，所以就无需进行互斥处理。

### java.util.concurrent.Exchanger类交换缓冲区

`java.util.concurrent.Exchanger类`用于`让两个线程安全地交换对象`。

如上案例，可以将buffer1缓冲区传递给ProducerThread，然后将buffer2缓冲区传递给ConsumerThread，同时还会将通用的Exchanger的实例分别传递给ProducerThread和ConsumerThread。

![exchanger交换缓冲区](https://cdn.jsdelivr.net/gh/bubblewu/cdn/images/concurrent/exchanger.png)

- Main类：
```java
Exchanger<Object[]> exchanger = new Exchanger<>();
        Object[] buffer1 = new Object[3];
        Object[] buffer2 = new Object[3];
        MakerExchangerThread makerThread = new MakerExchangerThread("-> Maker.", exchanger, buffer1, 2020);
        makerThread.start();
        EaterExchangerThread eaterThread = new EaterExchangerThread("Eater.", exchanger, buffer2, 2030);
        eaterThread.start();
```

输出
```
Eater.: Before exchange
-> Maker. put: 0
-> Maker. put: 1
-> Maker. put: 2
-> Maker.: Before exchange
-> Maker.: After exchange
Eater.: After exchange
Eater. take: 0
Eater. take: 1
-> Maker. put: 3
Eater. take: 2
-> Maker. put: 4
-> Maker. put: 5
-> Maker.: Before exchange
Eater.: Before exchange
Eater.: After exchange
-> Maker.: After exchange
Eater. take: 3
Eater. take: 4
-> Maker. put: 6
Eater. take: 5
Eater.: Before exchange
-> Maker. put: 7
-> Maker. put: 8
-> Maker.: Before exchange
-> Maker.: After exchange
Eater.: After exchange
Eater. take: 6
-> Maker. put: 9
Eater. take: 7
-> Maker. put: 10
```

- MakerExchangerThread生成者：
```java
/**
 * 表示糕点师
 * 基于juc包下的Exchanger类： 用于让两个线程安全地交换对象。
 * 主要步骤：
 * - 生产端填充字符，直到缓冲区被填满；
 * - 使用exchange方法将填满的缓冲区传递给消费端；
 * - 传递完成后，作为交换，接收消费端已经消费完的空的缓冲区；
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class MakerExchangerThread extends Thread {
    private final Exchanger<Object[]> exchanger;
    private Object[] dishArray;
    private final Random random;
    /**
     * 蛋糕的流水号，所有糕点师共用
     */
    private static int id = 0;

    public MakerExchangerThread(String name, Exchanger<Object[]> exchanger, Object[] dishArray, long seed) {
        super(name);
        this.exchanger = exchanger;
        this.dishArray = dishArray;
        this.random = new Random(seed);
    }


    @Override
    public void run() {
        while (true) {
            try {
                // 向缓冲区填充字符
                for (int i = 0; i < dishArray.length; i++) {
                    dishArray[i] = nextId();
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                    System.out.println(getName() + " put: " + dishArray[i]);
                }
                // 交换缓冲区
                System.out.println(getName() + ": Before exchange");
                dishArray = exchanger.exchange(dishArray);
                System.out.println(getName() + ": After exchange");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized int nextId() {
        return id++;
    }

}
```

- EaterExchangerThread消费者：
```java
/**
 * 表示来吃蛋糕的客人
 * 基于juc包下的Exchanger类： 用于让两个线程安全地交换对象。
 * 主要步骤：
 * - 使用exchange方法将空的缓冲区传递给生产端；
 * - 传递完成后，作为交换，接收生产端已经填满的缓冲区；
 * - 使用满的缓冲区的数据；
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class EaterExchangerThread extends Thread {
    private final Exchanger<Object[]> exchanger;
    private Object[] dishArray;
    private final Random random;

    public EaterExchangerThread(String name, Exchanger<Object[]> exchanger, Object[] dishArray, long seed) {
        super(name);
        this.exchanger = exchanger;
        this.dishArray = dishArray;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 交换缓冲区
                System.out.println(getName() + ": Before exchange");
                dishArray = exchanger.exchange(dishArray);
                System.out.println(getName() + ": After exchange");
                // 从缓冲区中取出蛋糕
                for (int i = 0; i < dishArray.length; i++) {
                    System.out.println(getName() + " take: " + dishArray[i]);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
```
