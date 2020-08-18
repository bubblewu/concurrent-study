[toc]
# 并发模式-1： Single Threaded Execution模式：同一时间内只能让一个线程执行处理

Single Threaded Execution模式，即"以一个线程执行"。
就像独木桥一样，同一时间内只允许一个人通过，**该模式用于设置限制，以确保同一时间内只能让一个线程执行处理。**
- 其实`主要思想`也就是：
**当我们修改多个线程共享的实例时，实例就会失去安全性。所以我们找出这个不安全的范围，将这个范围设置为临界区，并对临界区进行保护（使用synchronized），使其只允许一个线程同时执行**。

## Single Threaded Execution模式
### 概述
Single Threaded Execution模式中会有一个发挥`SharedResource（共享资源）`作用的类。如下面案例中的门Gate这个类。

**SharedResource角色是可以被多个线程访问的类**，包含很多方法，主要分类下面两类：
- `safeMethod`：线程安全方法。多线程下不会发生问题。
- `unsafeMethod`：非线程安全方法。多个线程调用会出现问题，需进行保护，使其不被多个线程同时访问。

**Single Threaded Execution模式会保护unsafeMethod，使其只能由一个线程访问**，Java可以使用`synchronized关键字`。我们将**允许单个线程执行的程序范围**称为`临界区`。

![Single Threaded Execution模式下的Timethreads图](https://cdn.jsdelivr.net/gh/bubblewu/cdn/images/concurrent/single-thread-execution.png)

### 什么时候使用？
- `多线程时`：
单线程时不需要，使用的前提是多线程环境下。

- `多个线程访问时`：
**当SharedResource角色的实例有可能被多个线程同时访问时**，就需要使用Single Threaded Execution模式。

即使是多线程程序，如果所有线程都是完全独立操作的，那么就不需要使用该模式，当前状态为`线程互不干涉（interfere）`。
在某些多线程框架中，有时线程的独立性是由框架保证的，这时也不需使用该模式。

- `状态有可能发生变化时`：
如果SharedResource角色的状态会发生变化时，就需使用该模式。

如果创建实例后，实例的状态再也不会发生变化，则不需使用。如`只读不写`的情况。如`Immutable模式`时，实例的状态不会发生改变，也就不需要。


- `需要确保安全性时`：
只有在需要确保安全性时，才需要使用该模式。
如：Java的集合类大多为非线程安全的，在使用时，这是为了在不需要考虑安全性的时候提高程序的运行速度。

> 线程安全的方法：
> Java提供了下列方法，可以确保集合类是线程安全的。
> - synchronizedCollection方法；
> - synchronizedList方法；
> - synchronizedMap方法；
> - synchronizedSet方法；
> - synchronizedSortedMap方法；
> - synchronizedSortedSet方法；

### 安全性和synchronized
Java使用关键字synchronized来实现执行线程的互斥处理。
同步方法（synchronized方法）： **在方法前加synchronized关键字，每次只允许一个线程处理该方法。**

> synchronized实例方法、synchronized静态方法和synchronized代码块：
> - synchronized代码块可以精确的控制互斥处理的执行范围。
> - **synchronized静态方法和synchronized实例方法使用的锁是不一样的。`synchronized静态方法是使用该类的类对象的锁来执行线程的互斥处理的`**，和synchronized代码块锁类时是等效的。

> 需注意：
> - 某个线程在运行synchronized方法时，只会停止想要获取当前同一个实例的锁的线程；
> - 非synchronized方法可以在任意时间被多个线程执行，即使存在正在运行其他的synchronized方法的线程，非synchronized方法也仍然可以由多个线程运行。
> - 同一个实例的synchronized实例方法同时只能有一个线程运行，如实例不同，锁也就不同，所有就算是synchronized实例方法，也可以由多个线程同时运行。
> - 同一个类下的多个synchronized静态方法不可以由多个线程同时运行，因为锁的是当前类对象。
> - synchronized方法通常会降低生存性，如容易引起死锁；添加不必要的synchronized，性能会降低，如吞吐量；

synchronized保护哪个对象的实例，就需对哪个对象加锁。
synchronized方法执行的操作，是不可分割的，能够防止多个线程交错的执行赋值操作，是`原子操作（Atomic）`。
> 注意：如在一个bean实体中，分别对两个字段的set方法加锁也是不安全的，因为线程会单独赋值，**需要将字段合在一起保护**。

- `synchronized和lock/unlock`
**如果在lock和unlock之间出现return语句或异常处理，会导致unlock不会被调用**。
而**synchronized方法和代码块，无论是执行return还是抛出异常，都一定能释放锁**。

不过lock和unlock操作，可以使用`finally块来执行unlock`。这样调用lock方法后，无论执行什么操作，都会调用unlock方法解锁。

- `synchronized和volatile和juc包下的AtomicXXX`
不使用synchronized，而`在声明该字段的时候加上volatile关键字，对该字段的操作也是原子的了`。
`juc包下的AtomicXXX等类也是通过封装volatile功能而得到的类库`。

所以：
- 基本类型、引用类型的赋值和引用是原子操作；
- 但long和double在线程间共享时，需要加synchronized或声明为volatile。将其变为原子操作。

### 生存性和死锁
生存性是指无论什么时候，必要的处理都一定能够被执行。是程序正常运行的必要条件之一。
有时候安全性和生存性会互相制约。有时只重视安全性，生存性就会下降。典型代表就是`死锁（deadlook）`，即**多个线程互相等待对方释放锁的情形**。

**发生死锁的线程都无法再继续运行，程序也就失去了生存性**。

> 如：仅有勺子和叉子各一把，A和B都要吃意大利面，勺子和叉子缺一不可。A拿走了勺子，B拿走了叉子，两人互相僵持，最终谁也吃不了。

在Single Threaded Execution模式中，满足下列条件时，死锁就会发生：
- 存在多个SharedResource角色；
> 多个SharedResource角色相当于勺子和叉子

- 线程在持有着某个SharedResource角色的锁的同时，还想获取其他SharedResource角色的锁；
> 相当于A拿着勺子同时还想拿叉子，B则相反。

- 获取SharedResource角色的锁的顺序并不固定。（SharedResource角色是对称的）
> SharedResource角色是对称的，相当于“拿勺子->拿叉子”和“拿叉子->拿勺子”这两种操作。也就是说勺子和叉子二者并不分优先顺序。


只要破坏上面任何一个条件，就可以防止死锁的情况发生。
如：
- **多个线程按照相同的顺序去获取实例资源**；
- **将多个实例资源封装起来一齐拿，对整体做同步，如new Pair(A, B); 直接对pair做同步处理**。

### 可复用性和继承反常
如果编写一个SharedResource角色的子类，如子类能访问SharedResource角色的字段，那么子类编写时，就容易出现unsafeMethod。
如果不将子类在内的所有unsafeMethod都声明为synchronized方法，那就无法确保SharedResource角色的安全性。

对于多线程来说，继承会引起一些麻烦的问题，称为`继承反常（inheritance anomaly）`

### 临界区的大小和性能
我们将**允许单个线程执行的程序范围**称为`临界区`。
> 延长临界区的大小，可以使线程的安全性异常更早的暴露出来。如可以使用Thread.sleep()方法来提高检查出错误的可能性。
> 在临界区也可以调用Thread类等yield方法，加快线程的切换。
> > `Thread.yield()`方法作用是：**暂停当前正在执行的线程对象，并执行其他线程**。
> > yield()应该做的是让当前运行线程回到可运行状态，以允许具有相同优先级的其他线程获得运行机会。因此，使用yield()的目的是让相同优先级的线程之间能适当的轮转执行。但是，实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。
> > 结论：yield()从未导致线程转到等待/睡眠/阻塞状态。在大多数情况下，yield()将导致线程从运行状态转到可运行状态，但有可能没有效果。

一般情况下Single Threaded Execution模式会降低程序的性能。
- `获取锁耗费时间`：
进入synchronized方法时，线程需要获取锁的对象，会耗费一定时间。
如果SharedResource角色的数量少了，那么要获取锁的数量也会减少，从而能够抑制性能的下降。

- `线程冲突引起的等待`：
当线程A进入临界区内处理时，其他想要进临界区的线程会阻塞。这种状况称为`线程冲突（conflict）`。
发生冲突时，程序的整体性能会随线程等待时间的增加而下降。

> 不容易发生线程冲突的`ConcurrentHashMap`：
> **ConcurrentHashMap将内部数据结构分成多段，针对各段操作的线程互不干涉，因此无需针对其他线程执行互斥处理**。

## 案例
模拟三个人频繁地通过一个门，且该门一次只允许一个人经过的场景。
当人从该门通过时，统计人数会增加，同时还会记录通行者的姓名和出生地。

### 不使用Single Threaded Execution模式
面对该需求，如果不使用Single Thread Execution模式，在多线程环境下无法正确执行的程序会引发什么现象？
将该程序设计为三个类：

| 类名 | 说明 |
| --- | --- |
| Main | 创建门，并让三个人不断通过的类 |
| Gate | 表示门的类。会记录通行者的姓名和出生地 |
| UserThread | 表示人的类。将不断有人通过门 |

#### 实现
##### Main类
```java
package com.bubble.demo.single_thread_execution;

/**
 * 创建门，并让三个人不断通过的类
 *
 * @author wugang
 * date: 2020-07-29 15:53
 **/
public class Main {

    /**
     * 由于Gate是非线程安全的，所以输出结果是混乱的。
     *
     */
    public static void main(String[] args) {
        System.out.println("测试开始，按[Ctrl + C]键退出");
        // 创建一个门，让三个人不断地通过
        Gate gate = new Gate();
        new UserThread(gate,"A小王", "A北京").start();
        new UserThread(gate,"B小李", "B上海").start();
        new UserThread(gate,"C小苏", "C南京").start();
    }

}

```

##### Gate类
```java
import java.util.concurrent.TimeUnit;

/**
 * 表示门的类。会记录通行者的姓名和出生地
 * 非线程安全/pass/toString方法加synchronized为线程安全
 *
 * @author wugang
 * date: 2020-07-29 15:55
 **/
public class Gate {
    /**
     * 表示到目前为止已经通过这道门的人数
     **/
    private int counter = 0;
    /**
     * 表示最后一个通行者的姓名
     **/
    private String name = "NoBody";
    /**
     * 表示最后一个通行者的出生地
     **/
    private String address = "NoWhere";

    /**
     * 表示通过门
     */
    public void pass(String name, String address) {
        this.counter++;
        this.name = name;
        // 在name和address赋值之间调用sleep，延长临界区，可以提高检查出错误的可能性，不需等数万次执行才发现。
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.address = address;
        check();
    }

    /**
     * 检查门的最后一个通行者的记录数据是否正确。
     * 注意：不需要添加synchronized。
     * 因为check方法只有pass方法会调用。且时私有的，也就是不会被其他类调用，所以是安全的
     */
    private void check() {
        // 如姓名和首字母不同，说明数据异常
        if (name.charAt(0) != address.charAt(0)) {
            System.out.println("*** 异常 *** :" + toString());
        }
    }

    @Override
    public String toString() {
        return "No." + counter + ": name='" + name + ", address=" + address;
    }
}

```

##### UserThread类
```java
package com.bubble.demo.single_thread_execution;

/**
 * 表示人的类。将不断有人通过门
 *
 * @author wugang
 * date: 2020-07-29 16:04
 **/
public class UserThread extends Thread {
    private final Gate gate;
    private final String name;
    private final String address;

    public UserThread(Gate gate, String name, String address) {
        this.gate = gate;
        this.name = name;
        this.address = address;
    }

    @Override
    public void run() {
        System.out.println(name + " BEGIN");
        // 反复调pass方法，表示这个人在门里不断地穿梭通过
        while (true) {
            this.gate.pass(this.name, this.address);
        }
    }


}

```

#### 结果
由于Gate是非线程安全的，pass方法会被多个线程执行。
- 线程改写共享的实例字段时，并未考虑其他线程的操作。
- 对于name字段，互相竞争的线程获取的一方会先写入值，对于address同样如此，线程会再次竞争，获胜的一方先写入值。也就是所谓的`数据竞争`（Data Race）。

所以输出结果是混乱的。
如下：
```
*** 异常 *** :No.88434: name='A小王, address=B上海
*** 异常 *** :No.88657: name='A小王, address=C南京
*** 异常 *** :No.88828: name='C小苏, address=C南京
```

由上面执行日志可知：
- Gate类是非线程安全的。
- 测试无法证明安全性。
执行了上万次才发现异常，如仅执行几次就可能发现不了。
- 调试信息不可靠。
如：`*** 异常 *** :No.88828: name='C小苏, address=C南京`，输出了异常日志，但check验证的toString内容是正确的，好像并没有错误。
**因为某个线程在执行check方法时，其他线程不断地执行pass方法，改写了name和address的值。**

### 使用Single Threaded Execution模式
该案例在不使用Single Threaded Execution模式时，即Gate是非线程安全的类时，会出现数据竞争的情况，导致不符合程序执行的安全性标准。


#### 实现
将Gate类修改为线程安全的类，只需要分别在pass方法和toString方法前添加synchronized关键字，这样Gate类就变成了线程安全的类。
如下：
```java
import java.util.concurrent.TimeUnit;

/**
 * 表示门的类。会记录通行者的姓名和出生地
 * 非线程安全/pass/toString方法加synchronized为线程安全
 *
 * @author wugang
 * date: 2020-07-29 15:55
 **/
public class Gate {
    /**
     * 表示到目前为止已经通过这道门的人数
     **/
    private int counter = 0;
    /**
     * 表示最后一个通行者的姓名
     **/
    private String name = "NoBody";
    /**
     * 表示最后一个通行者的出生地
     **/
    private String address = "NoWhere";

    /**
     * 表示通过门
     */
    public synchronized void pass(String name, String address) {
        this.counter++;
        this.name = name;
        // 在name和address赋值之间调用sleep，延长临界区，可以提高检查出错误的可能性，不需等数万次执行才发现。
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.address = address;
        check();
    }

    /**
     * 检查门的最后一个通行者的记录数据是否正确。
     * 注意：不需要添加synchronized。
     * 因为check方法只有pass方法会调用。且时私有的，也就是不会被其他类调用，所以是安全的
     */
    private void check() {
        // 如姓名和首字母不同，说明数据异常
        if (name.charAt(0) != address.charAt(0)) {
            System.out.println("*** 异常 *** :" + toString());
        }
    }

    /**
     * 一般来说，多个线程共享的字段必须使用synchronized或者volatile来保护。
     *
     * 假设当线程A正在执行pass方法时，其他线程B调用了toString方法。
     * 在线程B引用了name字段的值，但尚未引用address期间，线程A可能会修改address的值。
     * 这样，toString方法对线程B创建时使用name和address对首字母就可能会不一致。
     */
    @Override
    public synchronized String toString() {
        return "No." + counter + ": name='" + name + ", address=" + address;
    }
}
```

这样的话，无论等待多久，都不会出现异常情况。
```
测试开始，按[Ctrl + C]键退出
A小王 BEGIN
B小李 BEGIN
C小苏 BEGIN
```

因为：
Java使用关键字synchronized来实现执行线程的互斥处理。 **在方法前加synchronized关键字，每次只允许一个线程处理该方法。**

针对该案例添加了synchronized方法：在线程A执行pass方法时，线程B就无法再执行pass方法，会阻塞在pass方法的入口处，直到线程A执行释放了pass方法的锁，线程B才可以去获取pass方法的锁，获得锁后再执行。

## 扩展

### 相关的设计模型
许多与多线程、并发性相关的模式都跟Single Threaded Execution模式有关联。

#### Guarded Suspension模式
Guarded Suspension模式：**如果执行现在的处理会造成问题，就让执行处理的线程等待**。这种模式通过让线程等待来保证实例的安全性。

在Single Threaded Execution模式中，是否发生线程等待取决于**是否有其他线程正在执行受保护的unsafeMethod**。
而在Guarded Suspension模式中，取决于**对象的状态是否合适**。在检查对象状态的部分就使用了STE模式。

#### Read-Write Lock模式
在Read-Write Lock模式中，`读取操作和写入操作是分开考虑的。在执行读取操作之前，线程必须获取用于读取的锁；在执行写入操作之前，线程必须获取用于写入的锁`。所以：
- **当一个线程在读取时，其他线程可以读取，但是不可以写入**。
- **当一个线程正在写入时，其他线程不可以读取或写入**。
因为执行互斥处理会降低程序的性能，但是如果把写入的互斥处理和读取的互斥处理分开来考虑，就可以提高系统性能。

在STE模式中，如受保护的unsafeMethod正在被一个线程执行，那么想要执行该方法的其他线程必须等待该线程执行结束。

而Read-Write Lock模式中，多个线程可以同时执行read方法，这时需要等待的只有想要执行的write方法的线程。
在Read-Write Lock模式中，检查线程种类和个数部分，就使用了STE模式。

#### Immutable模式
**一个对象的状态在对象被创建之后就不再变化，这就是所谓的不变模式。**
在STE模式中，unsafeMethod必须要加以保护，确保只允许一个线程执行。
而在Immutable不变模式中，其对象的状态不会发生变化，所以所有方法都不需要进行保护，也就是`Immutable模式中的所有方法都是safeMethod`。

#### Thread-Specific Storage模式
在STE模式中，会有多个线程访问SharedResource角色，所以需要保护方法，对线程进行交通管制。
而`Thread-Specific Storage模式会确保每个线程都有其固有的区域，且这块固有区域仅由一个线程访问`。所以也无需保护方法。
如:`ThreadLocal类` 一个线程会有自己独立的储物柜。

### 信号量：Semaphore
STE模式用于确保某个区域只能由一个线程来执行。
如果保证某个区域**最多只能由N个线程执行**，那就需要使用juc包下的计数信号量Semaphore来控制线程数量。

- `资源的许可个数permits`通过Semaphore的构造函数来制定：
```java
public Semaphore(int permits, boolean fair) {
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }
```
- `acquire方法`：用于确保存在可用资源。
在存在可用资源时，程序会立即从acquire方法返回，同时信号量内部的资源个数会减1.
如无可用资源，线程则阻塞在acquire方法内，直到有可用资源。
- `release方法`：用于释放资源。
释放资源后，信号量内部的资源个数会加1。
同时，如果acquire中存在等待的线程，那么其中一个线程会被唤醒，并从acquire方法返回。

#### 案例
10个线程交替使用资源，但同时使用的资源最多只能是3个。

- Main类：
```java
public static void main(String[] args) {
        // 设置3个资源
        BoundedResource resource = new BoundedResource(3);
        // 10个线程交替使用资源，但同时使用的资源最多只能是3个
        for (int i = 0; i < 10; i++) {
            new UserThread(resource).start();
        }
    }
```

输出：
```
Thread-0: -> Begin: used = NO.1
Thread-1: -> Begin: used = NO.2
Thread-2: -> Begin: used = NO.3
Thread-2: <--- End: used = NO.3
Thread-3: -> Begin: used = NO.3
Thread-0: <--- End: used = NO.3
...
```

- UserThread用户线程类：
```java
public class UserThread extends Thread {
    private final static Random random = new Random(2020);
    private final BoundedResource resource;

    public UserThread(BoundedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.resource.use();
                TimeUnit.MILLISECONDS.sleep(random.nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
```

- BoundedResource使用资源类：
```java
public class BoundedResource {
    private final Semaphore semaphore;
    private final int permits;
    private final static Random random = new Random(2020);

    public BoundedResource(int permits) {
        this.semaphore = new Semaphore(permits);
        this.permits = permits;
    }

    public void use() throws InterruptedException {
        semaphore.acquire();
        try {
            doSomething();
        } finally {
            semaphore.release();
        }

    }

    private void doSomething() throws InterruptedException {
        Log.println("-> Begin: used = NO." + (this.permits - this.semaphore.availablePermits()));
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        Log.println("<--- End: used = NO." + (this.permits - this.semaphore.availablePermits()));
    }

}
```

- Log日志类：
```java
public class Log {

    public static void println(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }
}
```
