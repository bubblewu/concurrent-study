# concurrent-study
Java并发相关的多线程案例

## demo： 多线程设计模式案例
### 并发模式-1：Single Threaded Execution模式，即"以一个线程执行"。
就像独木桥一样，同一时间内只允许一个人通过，**该模式用于设置限制，以确保同一时间内只能让一个线程执行处理。**
- 其实`主要思想`也就是：
<p> 当我们修改多个线程共享的实例时，实例就会失去安全性。所以我们找出这个不安全的范围，将这个范围设置为临界区，并对临界区进行保护（使用synchronized），使其只允许一个线程同时执行。

### 并发模式-2：Immutable不变模式
<p> Immutable不变模式就是指：**确保实例的内部状态不会发生改变，这样在访问这些实例时就不需要增加耗时的互斥处理**（如Single Threaded Execution模式中的对临界区进行互斥保护）。
<p> 如`String类`就是一个Immutable类。因为**String类中使用final关键字修饰字符串数组`private final char value[];`来保存字符串**，并没有修改字符串内容的方法。所以，String的实例所表示的字符串的内容不会发生变化。

### 并发模式-3：Guarded Suspension模式：保护暂停
<p> Guarded Suspension模式思想就是：如果执行现在的处理会造成问题，那就让执行处理的线程进行等待。
<p> Guarded Suspension模式通过让线程等待来保护实例的安全性。就像你没穿衣服，让快递员在门口等你一会儿来保护你的隐私一样。
<p> 也就是说，该模式存在一个持有状态的对象，该对象只有在自身状态合适时，才会允许线程进行目标处理。
<p> 在Single Threaded Execution模式中，只要有一个线程进入临界区，其他线程就无法进入，只能等待。而在Guarded Suspension模式中，线程是否等待取决于守护条件。后者是在前者基础上添加了附加条件而形成的。


### 并发模式-4：Balking模式：停止并返回
- 思想：
<p> Balking就是停止返回的意思。
<p> Balking模式：`如果现在不适合或没必要执行这个操作，就停止处理，直接返回`。

- 与Guarded Suspension保护暂停模式区别
<p> Balking模式Guarded Suspension保护暂停模式一样都需要守护条件。
<p> 在Balking模式中，如果守护条件不成立，则立即中断处理。而后者是一直等待到可执行。

### 并发模式-5：Producer-Consumer模式：生产者消费者
<p> 生产者消费者模式，即`N个线程进行生产，同时N个线程进行消费，两种角色通过内存缓冲区进行通信。`
<p> 在Producer-Consumer模式，承担安全守护责任的是`Channel角色`。`Channel角色执行线程间的互斥处理，确保Producer角色正确地将Data角色传递给Consumer角色。`



